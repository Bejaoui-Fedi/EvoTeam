package tn.esprit.controllers.professionnel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.entities.Appointment;
import tn.esprit.entities.Consultation;
import tn.esprit.services.AppointmentService;
import tn.esprit.services.ConsultationService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class ProConsultationController {

    @FXML private ComboBox<Appointment> rdvCombo;
    @FXML private DatePicker dateConsultationPicker;
    @FXML private TextField diagnosticField;
    @FXML private TextArea observationArea;
    @FXML private TextField traitementField;
    @FXML private TextArea ordonnanceArea;
    @FXML private Spinner<Integer> dureeSpinner;
    @FXML private ChoiceBox<String> statutConsultationChoice;

    @FXML private TableView<Consultation> tableView;
    @FXML private TableColumn<Consultation, Integer> colId;
    @FXML private TableColumn<Consultation, Integer> colAppointmentId;
    @FXML private TableColumn<Consultation, LocalDate> colDate;
    @FXML private TableColumn<Consultation, String> colDiagnostic;
    @FXML private TableColumn<Consultation, String> colStatut;
    @FXML private TableColumn<Consultation, Integer> colDuree;

    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;
    @FXML private Button retourButton;
    @FXML private Label messageLabel;

    private ConsultationService consultationService = new ConsultationService();
    private AppointmentService appointmentService = new AppointmentService();
    private ObservableList<Consultation> consultationList = FXCollections.observableArrayList();
    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private Consultation selectedConsultation;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSpinner();
        setupChoiceBoxes();
        loadAppointments();
        loadConsultations();
        setupSelectionListener();

        dateConsultationPicker.setValue(LocalDate.now());
        messageLabel.setText("");
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAppointmentId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateConsultation"));
        colDiagnostic.setCellValueFactory(new PropertyValueFactory<>("diagnostic"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statutConsultation"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("duree"));

        // Formatage de la date
        colDate.setCellFactory(column -> new TableCell<Consultation, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.toString());
                }
            }
        });

        // Colorer le statut
        colStatut.setCellFactory(column -> new TableCell<Consultation, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statut);
                    if ("en cours".equals(statut)) {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupSpinner() {
        dureeSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 120, 30)
        );
        dureeSpinner.setEditable(true);
    }

    private void setupChoiceBoxes() {
        statutConsultationChoice.setItems(
                FXCollections.observableArrayList("en cours", "clôturée")
        );
        statutConsultationChoice.setValue("en cours");
    }

    private void loadAppointments() {
        try {
            appointmentList.clear();
            for (Appointment a : appointmentService.getAll()) {
                if (a.getStatut().equals("confirmé") || a.getStatut().equals("terminé")) {
                    appointmentList.add(a);
                }
            }
            rdvCombo.setItems(appointmentList);

            // Affichage personnalisé dans la combo
            rdvCombo.setConverter(new javafx.util.StringConverter<Appointment>() {
                @Override
                public String toString(Appointment a) {
                    if (a == null) return "";
                    return String.format("RDV #%d - %s %s - %s",
                            a.getId(),
                            a.getDateRdv(),
                            a.getHeureRdv(),
                            a.getMotif().length() > 20 ?
                                    a.getMotif().substring(0, 20) + "..." : a.getMotif()
                    );
                }
                @Override
                public Appointment fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger les rendez-vous : " + e.getMessage());
        }
    }

    private void loadConsultations() {
        try {
            consultationList.clear();
            consultationList.addAll(consultationService.getAll());
            tableView.setItems(consultationList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger les consultations : " + e.getMessage());
        }
    }

    private void setupSelectionListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, old, newSel) -> {
            selectedConsultation = newSel;
            if (newSel != null) {
                fillForm(newSel);
                saveButton.setDisable(true);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                clearForm();
                saveButton.setDisable(false);
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
    }

    private void fillForm(Consultation c) {
        for (Appointment a : appointmentList) {
            if (a.getId() == c.getAppointmentId()) {
                rdvCombo.setValue(a);
                break;
            }
        }
        dateConsultationPicker.setValue(c.getDateConsultation());
        diagnosticField.setText(c.getDiagnostic());
        observationArea.setText(c.getObservation());
        traitementField.setText(c.getTraitement());
        ordonnanceArea.setText(c.getOrdonnance());
        dureeSpinner.getValueFactory().setValue(c.getDuree());
        statutConsultationChoice.setValue(c.getStatutConsultation());
        messageLabel.setText("");
    }

    // ================ CRUD OPERATIONS ================

    @FXML
    private void handleSave() {
        if (!validateInputs()) return;

        Appointment selectedRdv = rdvCombo.getValue();
        if (selectedRdv == null) {
            showAlert(Alert.AlertType.WARNING, "Validation",
                    "Veuillez sélectionner un rendez-vous");
            return;
        }

        // Vérifier si une consultation existe déjà
        boolean exists = consultationList.stream()
                .anyMatch(c -> c.getAppointmentId() == selectedRdv.getId());

        if (exists) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "Une consultation existe déjà pour ce rendez-vous");
            return;
        }

        Consultation consultation = new Consultation(
                selectedRdv.getId(),
                dateConsultationPicker.getValue(),
                diagnosticField.getText().trim(),
                observationArea.getText().trim(),
                traitementField.getText().trim(),
                ordonnanceArea.getText().trim(),
                dureeSpinner.getValue(),
                statutConsultationChoice.getValue()
        );

        try {
            consultationService.add(consultation);
            loadConsultations();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Consultation ajoutée avec succès");

            // Mettre à jour le statut du RDV si nécessaire
            if (selectedRdv.getStatut().equals("confirmé")) {
                selectedRdv.setStatut("terminé");
                appointmentService.update(selectedRdv);
                loadAppointments(); // Recharger la liste des RDV
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Échec de l'ajout de la consultation : " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedConsultation == null) return;
        if (!validateInputs()) return;

        selectedConsultation.setDateConsultation(dateConsultationPicker.getValue());
        selectedConsultation.setDiagnostic(diagnosticField.getText().trim());
        selectedConsultation.setObservation(observationArea.getText().trim());
        selectedConsultation.setTraitement(traitementField.getText().trim());
        selectedConsultation.setOrdonnance(ordonnanceArea.getText().trim());
        selectedConsultation.setDuree(dureeSpinner.getValue());
        selectedConsultation.setStatutConsultation(statutConsultationChoice.getValue());

        try {
            consultationService.update(selectedConsultation);
            loadConsultations();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Consultation modifiée avec succès");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Échec de la modification : " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedConsultation == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer la consultation ?");
        confirm.setContentText(String.format(
                "Consultation #%d\nRDV #%d\nDiagnostic: %s\n\nCette action est irréversible !",
                selectedConsultation.getId(),
                selectedConsultation.getAppointmentId(),
                selectedConsultation.getDiagnostic()
        ));

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                consultationService.delete(selectedConsultation);
                loadConsultations();
                clearForm();
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Consultation supprimée avec succès");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Échec de la suppression : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        rdvCombo.setValue(null);
        dateConsultationPicker.setValue(LocalDate.now());
        diagnosticField.clear();
        observationArea.clear();
        traitementField.clear();
        ordonnanceArea.clear();
        dureeSpinner.getValueFactory().setValue(30);
        statutConsultationChoice.setValue("en cours");
        selectedConsultation = null;
        saveButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        tableView.getSelectionModel().clearSelection();
        messageLabel.setText("");
    }

    private boolean validateInputs() {
        if (rdvCombo.getValue() == null) {
            messageLabel.setText("❌ Sélectionnez un rendez-vous");
            return false;
        }
        if (diagnosticField.getText().trim().isEmpty()) {
            messageLabel.setText("❌ Le diagnostic est obligatoire");
            return false;
        }
        if (statutConsultationChoice.getValue() == null) {
            messageLabel.setText("❌ Sélectionnez un statut");
            return false;
        }
        messageLabel.setText("");
        return true;
    }

    @FXML
    private void retourGestionRdv() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/professionnel/pro_gestion_rdv.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retourButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des rendez-vous");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de retourner à la gestion des rendez-vous");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}