package tn.esprit.controllers.professionnel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import tn.esprit.entities.Appointment;
import tn.esprit.entities.Consultation;
import tn.esprit.services.AppointmentService;
import tn.esprit.services.ConsultationService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class ProConsultationController {

    @FXML
    private ComboBox<Appointment> rdvCombo;
    @FXML
    private DatePicker dateConsultationPicker;
    @FXML
    private TextField diagnosticField;
    @FXML
    private TextArea observationArea;
    @FXML
    private TextField traitementField;
    @FXML
    private TextArea ordonnanceArea;
    @FXML
    private Spinner<Integer> dureeSpinner;
    @FXML
    private ChoiceBox<String> statutConsultationChoice;

    @FXML
    private FlowPane cardsContainer;

    @FXML
    private Button saveButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button retourButton;
    @FXML
    private Label messageLabel;

    private ConsultationService consultationService = new ConsultationService();
    private AppointmentService appointmentService = new AppointmentService();
    private ObservableList<Consultation> consultationList = FXCollections.observableArrayList();
    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private Consultation selectedConsultation;

    @FXML
    public void initialize() {
        setupSpinner();
        setupChoiceBoxes();
        loadAppointments();
        loadConsultations();

        // Auto-remplissage de la date de consultation à partir du RDV sélectionné
        rdvCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                dateConsultationPicker.setValue(newVal.getDateRdv());
            }
        });

        dateConsultationPicker.setValue(LocalDate.now());
        dateConsultationPicker.setDisable(true);
        messageLabel.setText("");
    }

    private void renderCards(ObservableList<Consultation> consultations) {
        cardsContainer.getChildren().clear();
        for (Consultation c : consultations) {
            VBox card = createConsultationCard(c);
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createConsultationCard(Consultation c) {
        VBox card = new VBox(10);
        card.getStyleClass().add("item-card");
        card.setPrefWidth(320);

        // Header: ID and Status
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label idLabel = new Label("CONS #" + c.getId() + " (RDV #" + c.getAppointmentId() + ")");
        idLabel.getStyleClass().add("card-id");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = new Label(c.getStatutConsultation().toUpperCase());
        statusBadge.getStyleClass().add("card-badge");
        applyStatusStyle(statusBadge, c.getStatutConsultation());

        header.getChildren().addAll(idLabel, spacer, statusBadge);

        // Content: Diagnostic
        Label diagLabel = new Label("Diagnostic:");
        diagLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: -primary-color;");
        Label diagValue = new Label(c.getDiagnostic());
        diagValue.getStyleClass().add("card-title");
        diagValue.setWrapText(true);

        // Details
        VBox details = new VBox(5);
        details.getChildren().addAll(
                createDetailRow("📅 Date:", c.getDateConsultation().toString()),
                createDetailRow("⏱️ Durée:", c.getDuree() + " min"),
                createDetailRow("💊 Traitement:", c.getTraitement()));

        Region separator = new Region();
        separator.getStyleClass().add("card-separator");
        separator.setPrefHeight(1);

        // Selection handling
        card.setOnMouseClicked(e -> {
            for (javafx.scene.Node n : cardsContainer.getChildren()) {
                n.setStyle("");
            }
            card.setStyle("-fx-border-color: -primary-color; -fx-border-width: 3;");
            selectedConsultation = c;
            fillForm(c);
            saveButton.setDisable(true);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
        });

        card.getChildren().addAll(header, diagLabel, diagValue, details, separator);
        return card;
    }

    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox(5);
        Label l = new Label(label);
        l.setStyle("-fx-font-weight: bold; -fx-min-width: 80;");
        Label v = new Label(value);
        v.setWrapText(true);
        row.getChildren().addAll(l, v);
        return row;
    }

    private void applyStatusStyle(Label label, String statut) {
        String lowerStatut = statut.toLowerCase();
        if (lowerStatut.equals("en cours")) {
            label.setStyle("-fx-background-color: #d1ecf1; -fx-text-fill: #0c5460;");
        } else if (lowerStatut.equals("attente")) {
            label.setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
        } else if (lowerStatut.equals("clôturée") || lowerStatut.equals("cloturée")) {
            label.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724;");
        } else if (lowerStatut.equals("annulée")) {
            label.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24;");
        }
    }

    private void setupSpinner() {
        dureeSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 120, 30));
        dureeSpinner.setEditable(true);
    }

    private void setupChoiceBoxes() {
        statutConsultationChoice.setItems(
                FXCollections.observableArrayList("attente", "en cours", "clôturée", "annulée"));
        statutConsultationChoice.setValue("attente");
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

            rdvCombo.setConverter(new javafx.util.StringConverter<Appointment>() {
                @Override
                public String toString(Appointment a) {
                    if (a == null)
                        return "";
                    return String.format("RDV #%d - %s %s - %s (Patient #%d)",
                            a.getId(),
                            a.getDateRdv(),
                            a.getHeureRdv(),
                            a.getMotif().length() > 20 ? a.getMotif().substring(0, 20) + "..." : a.getMotif(),
                            a.getUserId());
                }

                @Override
                public Appointment fromString(String s) {
                    return null;
                }
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
            renderCards(consultationList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger les consultations : " + e.getMessage());
        }
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

    @FXML
    private void handleSave() {
        if (!validateInputs())
            return;

        Appointment selectedRdv = rdvCombo.getValue();
        if (selectedRdv == null) {
            showAlert(Alert.AlertType.WARNING, "Validation",
                    "Veuillez sélectionner un rendez-vous");
            return;
        }

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
                statutConsultationChoice.getValue());

        try {
            consultationService.add(consultation);
            loadConsultations();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Consultation ajoutée avec succès");

            if (selectedRdv.getStatut().equals("confirmé")) {
                selectedRdv.setStatut("terminé");
                appointmentService.update(selectedRdv);
                loadAppointments();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Échec de l'ajout de la consultation : " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedConsultation == null) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner une consultation à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/professionnel/modifier_consultation_pro_dialog.fxml"));

            if (loader.getLocation() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Fichier FXML introuvable : /fxml/professionnel/modifier_consultation_pro_dialog.fxml");
                return;
            }

            Parent root = loader.load();

            ModifierConsultationProDialogController controller = loader.getController();
            controller.setConsultation(selectedConsultation);

            Stage stage = new Stage();
            controller.setStage(stage);

            stage.setTitle("Modifier la consultation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(updateButton.getScene().getWindow());
            stage.setResizable(false);

            stage.showAndWait();

            if (controller.isModificationReussie()) {
                loadConsultations();
                clearForm();
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur technique",
                    "Impossible d'ouvrir la fenêtre de modification.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedConsultation == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer la consultation ?");
        confirm.setContentText(String.format(
                "‼️ ATTENTION ‼️\n\n" +
                        "Vous êtes sur le point de supprimer DÉFINITIVEMENT cette consultation :\n\n" +
                        "📋 Consultation #%d\n" +
                        "📅 RDV #%d\n" +
                        "🔍 Diagnostic: %s\n\n" +
                        "⚠️ Cette action est IRREVERSIBLE !",
                selectedConsultation.getId(),
                selectedConsultation.getAppointmentId(),
                selectedConsultation.getDiagnostic()));

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
        for (javafx.scene.Node n : cardsContainer.getChildren()) {
            n.setStyle("");
        }
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
                    "Impossible de retourner à la gestion des rendez-vous: " + e.getMessage());
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