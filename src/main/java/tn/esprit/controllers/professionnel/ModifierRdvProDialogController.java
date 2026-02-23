package tn.esprit.controllers.professionnel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Appointment;
import tn.esprit.services.AppointmentService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class ModifierRdvProDialogController {

    @FXML private Label titleLabel;
    @FXML private Label rdvInfoLabel;

    @FXML private DatePicker datePicker;
    @FXML private TextField heureField;
    @FXML private TextField motifField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private ChoiceBox<String> statutChoice;
    @FXML private TextField userIdField;

    @FXML private Label ancienneDateLabel;
    @FXML private Label ancienneHeureLabel;
    @FXML private Label ancienMotifLabel;
    @FXML private Label ancienTypeLabel;
    @FXML private Label ancienStatutLabel;
    @FXML private Label ancienUserIdLabel;

    @FXML private Label messageLabel;

    @FXML private Button confirmerButton;
    @FXML private Button annulerButton;

    private Appointment appointment;
    private AppointmentService service = new AppointmentService();
    private Stage stage;
    private boolean modificationReussie = false;

    @FXML
    public void initialize() {
        setupTypeChoice();
        setupStatutChoice();
        setupDatePicker();
        setupUserIdField();
        messageLabel.setText("");
    }

    private void setupTypeChoice() {
        typeChoice.setItems(FXCollections.observableArrayList(
                "Présentiel", "Téléconsultation"
        ));
    }

    private void setupStatutChoice() {
        statutChoice.setItems(FXCollections.observableArrayList(
                "en attente", "confirmé", "terminé", "annulé"
        ));
    }

    private void setupDatePicker() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Le professionnel peut modifier n'importe quelle date
                setDisable(empty);
            }
        });
    }

    private void setupUserIdField() {
        userIdField.setEditable(false);
        userIdField.setStyle("-fx-background-color: #f0f0f0;");
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
        afficherInfosAnciennes();
        preRemplirFormulaire();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void afficherInfosAnciennes() {
        if (appointment != null) {
            titleLabel.setText("MODIFICATION DU RENDEZ-VOUS #" + appointment.getId());
            rdvInfoLabel.setText(String.format("Rendez-vous du patient #%d", appointment.getUserId()));

            ancienneDateLabel.setText(appointment.getDateRdv().toString());
            ancienneHeureLabel.setText(appointment.getHeureRdv().toString());
            ancienMotifLabel.setText(appointment.getMotif());
            ancienTypeLabel.setText(appointment.getTypeRdv());
            ancienStatutLabel.setText(appointment.getStatut());
            ancienUserIdLabel.setText(String.valueOf(appointment.getUserId()));
        }
    }

    private void preRemplirFormulaire() {
        if (appointment != null) {
            datePicker.setValue(appointment.getDateRdv());
            heureField.setText(appointment.getHeureRdv().toString());
            motifField.setText(appointment.getMotif());
            typeChoice.setValue(appointment.getTypeRdv());
            statutChoice.setValue(appointment.getStatut());
            userIdField.setText(String.valueOf(appointment.getUserId()));
        }
    }

    @FXML
    private void handleConfirmer() {
        if (!validateInputs()) return;

        try {
            int originalUserId = appointment.getUserId();

            appointment.setDateRdv(datePicker.getValue());
            appointment.setHeureRdv(LocalTime.parse(heureField.getText().trim()));
            appointment.setMotif(motifField.getText().trim());
            appointment.setTypeRdv(typeChoice.getValue());
            appointment.setStatut(statutChoice.getValue());
            appointment.setUserId(originalUserId);

            service.update(appointment);

            modificationReussie = true;

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setHeaderText(null);
            success.setContentText("✅ Rendez-vous modifié avec succès !");
            success.showAndWait();

            fermer();

        } catch (SQLException e) {
            messageLabel.setText("❌ Erreur base de données : " + e.getMessage());
            e.printStackTrace();
        } catch (DateTimeParseException e) {
            messageLabel.setText("❌ Format d'heure invalide. Utilisez HH:MM (ex: 14:30)");
        } catch (Exception e) {
            messageLabel.setText("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler() {
        modificationReussie = false;
        fermer();
    }

    private boolean validateInputs() {
        if (datePicker.getValue() == null) {
            messageLabel.setText("❌ Veuillez choisir une date");
            return false;
        }
        if (heureField.getText() == null || heureField.getText().trim().isEmpty()) {
            messageLabel.setText("❌ Veuillez saisir une heure");
            return false;
        }

        // Validation du format de l'heure
        String heure = heureField.getText().trim();
        if (!heure.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            messageLabel.setText("❌ Format d'heure invalide. Utilisez HH:MM (ex: 14:30)");
            return false;
        }

        if (motifField.getText().trim().isEmpty()) {
            messageLabel.setText("❌ Veuillez saisir le motif");
            return false;
        }
        if (typeChoice.getValue() == null) {
            messageLabel.setText("❌ Veuillez choisir le type de rendez-vous");
            return false;
        }
        if (statutChoice.getValue() == null) {
            messageLabel.setText("❌ Veuillez choisir le statut");
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
}