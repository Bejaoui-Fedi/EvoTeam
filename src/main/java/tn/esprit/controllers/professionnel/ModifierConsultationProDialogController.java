package tn.esprit.controllers.professionnel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Appointment;
import tn.esprit.entities.Consultation;
import tn.esprit.services.AppointmentService;
import tn.esprit.services.ConsultationService;
import tn.esprit.services.PDFService;

import java.sql.SQLException;
import java.time.LocalDate;

public class ModifierConsultationProDialogController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label consultationInfoLabel;

    // Informations actuelles
    @FXML
    private Label ancienRdvLabel;
    @FXML
    private Label ancienneDateLabel;
    @FXML
    private Label ancienDiagnosticLabel;
    @FXML
    private Label ancienneObservationLabel;
    @FXML
    private Label ancienTraitementLabel;
    @FXML
    private Label ancienneOrdonnanceLabel;
    @FXML
    private Label ancienneDureeLabel;
    @FXML
    private Label ancienStatutLabel;

    // Formulaire de modification
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
    private Label messageLabel;

    @FXML
    private Button exportPDFButton;
    @FXML
    private Button confirmerButton;
    @FXML
    private Button annulerButton;

    private Consultation consultation;
    private ConsultationService consultationService = new ConsultationService();
    private AppointmentService appointmentService = new AppointmentService();
    private PDFService pdfService = new PDFService();
    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private Stage stage;
    private boolean modificationReussie = false;

    @FXML
    public void initialize() {
        setupSpinner();
        setupChoiceBoxes();
        setupDatePicker();
        loadAppointments();

        // Auto-remplissage et désactivation de la date
        dateConsultationPicker.setDisable(true);
        rdvCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                dateConsultationPicker.setValue(newVal.getDateRdv());
            }
        });

        // Gestion de la couleur du statut
        statutConsultationChoice.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateStatusStyle(statutConsultationChoice, newVal);
        });

        messageLabel.setText("");
    }

    private void updateStatusStyle(Control control, String status) {
        if (status == null)
            return;

        String lowerStatus = status.toLowerCase();
        if (lowerStatus.equals("en cours")) {
            control.setStyle(
                    "-fx-background-color: #d1ecf1; -fx-text-fill: #0c5460; -fx-font-weight: bold; -fx-border-color: #bee5eb; -fx-border-radius: 25; -fx-background-radius: 25;");
        } else if (lowerStatus.equals("attente")) {
            control.setStyle(
                    "-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold; -fx-border-color: #ffeeba; -fx-border-radius: 25; -fx-background-radius: 25;");
        } else if (lowerStatus.equals("clôturée") || lowerStatus.equals("cloturée")) {
            control.setStyle(
                    "-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold; -fx-border-color: #c3e6cb; -fx-border-radius: 25; -fx-background-radius: 25;");
        } else if (lowerStatus.equals("annulée")) {
            control.setStyle(
                    "-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold; -fx-border-color: #f5c6cb; -fx-border-radius: 25; -fx-background-radius: 25;");
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
    }

    private void setupDatePicker() {
        dateConsultationPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
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

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
        afficherInfosAnciennes();
        preRemplirFormulaire();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void afficherInfosAnciennes() {
        if (consultation != null) {
            titleLabel.setText("MODIFICATION DE LA CONSULTATION #" + consultation.getId());
            consultationInfoLabel
                    .setText(String.format("Consultation liée au RDV #%d", consultation.getAppointmentId()));

            ancienRdvLabel.setText(String.valueOf(consultation.getAppointmentId()));
            ancienneDateLabel
                    .setText(consultation.getDateConsultation() != null ? consultation.getDateConsultation().toString()
                            : "Non définie");
            ancienDiagnosticLabel.setText(consultation.getDiagnostic());
            ancienneObservationLabel.setText(
                    consultation.getObservation() != null ? consultation.getObservation() : "Aucune observation");
            ancienTraitementLabel
                    .setText(consultation.getTraitement() != null ? consultation.getTraitement() : "Aucun traitement");
            ancienneOrdonnanceLabel
                    .setText(consultation.getOrdonnance() != null ? consultation.getOrdonnance() : "Aucune ordonnance");
            ancienneDureeLabel.setText(consultation.getDuree() + " minutes");
            ancienStatutLabel.setText(consultation.getStatutConsultation());
            updateStatusStyle(ancienStatutLabel, consultation.getStatutConsultation());
        }
    }

    private void preRemplirFormulaire() {
        if (consultation != null) {
            // Sélectionner le bon rendez-vous dans la combo
            for (Appointment a : appointmentList) {
                if (a.getId() == consultation.getAppointmentId()) {
                    rdvCombo.setValue(a);
                    break;
                }
            }

            dateConsultationPicker.setValue(consultation.getDateConsultation());
            diagnosticField.setText(consultation.getDiagnostic());
            observationArea.setText(consultation.getObservation());
            traitementField.setText(consultation.getTraitement());
            ordonnanceArea.setText(consultation.getOrdonnance());
            dureeSpinner.getValueFactory().setValue(consultation.getDuree());
            statutConsultationChoice.setValue(consultation.getStatutConsultation());
            updateStatusStyle(statutConsultationChoice, consultation.getStatutConsultation());
        }
    }

    @FXML
    private void handleConfirmer() {
        if (!validateInputs())
            return;

        try {
            Appointment selectedRdv = rdvCombo.getValue();

            consultation.setAppointmentId(selectedRdv.getId());
            consultation.setDateConsultation(dateConsultationPicker.getValue());
            consultation.setDiagnostic(diagnosticField.getText().trim());
            consultation.setObservation(observationArea.getText().trim());
            consultation.setTraitement(traitementField.getText().trim());
            consultation.setOrdonnance(ordonnanceArea.getText().trim());
            consultation.setDuree(dureeSpinner.getValue());
            consultation.setStatutConsultation(statutConsultationChoice.getValue());

            consultationService.update(consultation);

            modificationReussie = true;

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setHeaderText(null);
            success.setContentText("✅ Consultation modifiée avec succès !");
            success.showAndWait();

            fermer();

        } catch (SQLException e) {
            messageLabel.setText("❌ Erreur base de données : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            messageLabel.setText("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExportPDF() {
        if (consultation == null || consultation.getOrdonnance() == null || consultation.getOrdonnance().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "L'ordonnance est vide. Rien à exporter.");
            return;
        }

        try {
            // In a real app, we would get the patient and doctor names from the database
            String patientName = "Patient #" + consultation.getAppointmentId();
            String doctorName = "Dr. EvoTeam";

            pdfService.generateOrdonnance(consultation, patientName, doctorName);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'ordonnance a été exportée en PDF avec succès.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la génération du PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler() {

        modificationReussie = false;
        fermer();
    }

    private boolean validateInputs() {
        if (rdvCombo.getValue() == null) {
            messageLabel.setText("❌ Veuillez sélectionner un rendez-vous");
            return false;
        }
        if (dateConsultationPicker.getValue() == null) {
            messageLabel.setText("❌ Veuillez choisir une date");
            return false;
        }
        if (diagnosticField.getText().trim().isEmpty()) {
            messageLabel.setText("❌ Le diagnostic est obligatoire");
            return false;
        }
        if (statutConsultationChoice.getValue() == null) {
            messageLabel.setText("❌ Veuillez choisir un statut");
            return false;
        }
        if (dureeSpinner.getValue() == null || dureeSpinner.getValue() < 1) {
            messageLabel.setText("❌ La durée doit être d'au moins 1 minute");
            return false;
        }

        messageLabel.setText("");
        return true;
    }

    private void fermer() {
        if (stage != null) {
            stage.close();
        }
    }

    public boolean isModificationReussie() {
        return modificationReussie;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}