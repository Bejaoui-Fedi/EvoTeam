package tn.esprit.controllers.user;

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

public class ModifierRdvDialogController {

    @FXML private Label titleLabel;
    @FXML private Label rdvInfoLabel;

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> heureCombo;
    @FXML private TextField motifField;
    @FXML private ChoiceBox<String> typeChoice;

    @FXML private Label ancienneDateLabel;
    @FXML private Label ancienneHeureLabel;
    @FXML private Label ancienMotifLabel;
    @FXML private Label ancienTypeLabel;
    @FXML private Label ancienStatutLabel;

    @FXML private Label messageLabel;

    private Appointment appointment;
    private AppointmentService service = new AppointmentService();
    private Stage stage;
    private boolean modificationReussie = false;

    @FXML
    public void initialize() {
        setupHeures();
        setupTypeChoice();
        setupDatePicker();
        messageLabel.setText("");
    }

    private void setupHeures() {
        ObservableList<String> creneaux = FXCollections.observableArrayList();
        for (int i = 8; i <= 18; i++) {
            creneaux.add(String.format("%02d:00", i));
            creneaux.add(String.format("%02d:30", i));
        }
        heureCombo.setItems(creneaux);
        heureCombo.setEditable(true);
    }

    private void setupTypeChoice() {
        typeChoice.setItems(FXCollections.observableArrayList(
                "Présentiel", "Téléconsultation"
        ));
    }

    private void setupDatePicker() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
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
            rdvInfoLabel.setText(String.format("Rendez-vous du %s à %s",
                    appointment.getDateRdv(), appointment.getHeureRdv()));

            ancienneDateLabel.setText(appointment.getDateRdv().toString());
            ancienneHeureLabel.setText(appointment.getHeureRdv().toString());
            ancienMotifLabel.setText(appointment.getMotif());
            ancienTypeLabel.setText(appointment.getTypeRdv());
            ancienStatutLabel.setText(appointment.getStatut());
        }
    }

    private void preRemplirFormulaire() {
        if (appointment != null) {
            datePicker.setValue(appointment.getDateRdv());
            heureCombo.setValue(appointment.getHeureRdv().toString());
            motifField.setText(appointment.getMotif());
            typeChoice.setValue(appointment.getTypeRdv());
        }
    }

    @FXML
    private void handleConfirmer() {
        if (!validateInputs()) return;

        try {
            int originalUserId = appointment.getUserId();
            String originalStatut = appointment.getStatut();

            appointment.setDateRdv(datePicker.getValue());
            appointment.setHeureRdv(LocalTime.parse(heureCombo.getValue()));
            appointment.setMotif(motifField.getText().trim());
            appointment.setTypeRdv(typeChoice.getValue());
            appointment.setStatut(originalStatut);
            appointment.setUserId(originalUserId);

            service.update(appointment);

            modificationReussie = true;
            fermer();

        } catch (SQLException e) {
            messageLabel.setText("❌ Erreur base de données : " + e.getMessage());
            e.printStackTrace();
        } catch (DateTimeParseException e) {
            messageLabel.setText("❌ Format d'heure invalide. Utilisez HH:MM");
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
        if (heureCombo.getValue() == null || heureCombo.getValue().trim().isEmpty()) {
            messageLabel.setText("❌ Veuillez choisir une heure");
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

        if (datePicker.getValue().isBefore(LocalDate.now())) {
            messageLabel.setText("❌ La date ne peut pas être dans le passé");
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