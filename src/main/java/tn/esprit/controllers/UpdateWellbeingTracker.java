package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.WellbeingTracker;
import tn.esprit.services.ServiceWellbeingTracker;
import tn.esprit.services.ServiceDailyRoutineTask;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class UpdateWellbeingTracker {

    @FXML private Label lblId;
    @FXML private ComboBox<Integer> cbUserId;
    @FXML private ComboBox<Integer> cbRoutineTaskId;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Integer> cbMood;
    @FXML private ComboBox<Integer> cbStress;
    @FXML private ComboBox<Integer> cbEnergy;
    @FXML private TextField tfSleepHours;
    @FXML private TextArea taNote;
    @FXML private Label lblCreatedAt;

    private WellbeingTracker tracker;
    private final ServiceWellbeingTracker serviceTracker = new ServiceWellbeingTracker();
    private final ServiceDailyRoutineTask serviceTask = new ServiceDailyRoutineTask();
    private final UserService serviceUser = new UserService();

    @FXML
    public void initialize() {
        loadUserIds();
        loadTaskIds();
        loadDefaultValues();
    }

    private void loadUserIds() {
        try {
            List<Integer> userIds = serviceUser.getAllUsersIds();
            cbUserId.setItems(FXCollections.observableArrayList(userIds));
        } catch (Exception e) {
            showValidationError("Impossible de charger les utilisateurs");
        }
    }

    private void loadTaskIds() {
        try {
            List<Integer> taskIds = serviceTask.getAllTaskIds();
            cbRoutineTaskId.setItems(FXCollections.observableArrayList(taskIds));
        } catch (Exception e) {
            showValidationError("Impossible de charger les tâches");
        }
    }

    private void loadDefaultValues() {
        cbMood.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbStress.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbEnergy.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
    }

    public void initData(WellbeingTracker tracker) {
        this.tracker = tracker;

        lblId.setText("ID: " + tracker.getId());
        cbUserId.setValue(tracker.getUserId());
        cbRoutineTaskId.setValue(tracker.getRoutineTaskId());
        datePicker.setValue(LocalDate.parse(tracker.getDate()));
        cbMood.setValue(tracker.getMood());
        cbStress.setValue(tracker.getStress());
        cbEnergy.setValue(tracker.getEnergy());
        tfSleepHours.setText(String.valueOf(tracker.getSleepHours()));
        taNote.setText(tracker.getNote());
        lblCreatedAt.setText("Créé le: " + tracker.getCreatedAt());
    }

    @FXML
    public void handleUpdate() {
        if (!validateForm()) {
            return;
        }

        updateTrackerData();

        try {
            serviceTracker.update(tracker);
            showSuccessMessage();
            closeWindow();
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la modification");
        }
    }

    private boolean validateForm() {
        if (cbUserId.getValue() == null) {
            showValidationError("Veuillez sélectionner un utilisateur");
            return false;
        }
        if (cbRoutineTaskId.getValue() == null) {
            showValidationError("Veuillez sélectionner une tâche");
            return false;
        }
        if (datePicker.getValue() == null) {
            showValidationError("Veuillez sélectionner une date");
            return false;
        }
        if (cbMood.getValue() == null) {
            showValidationError("Veuillez sélectionner une humeur");
            return false;
        }
        if (cbStress.getValue() == null) {
            showValidationError("Veuillez sélectionner un stress");
            return false;
        }
        if (cbEnergy.getValue() == null) {
            showValidationError("Veuillez sélectionner une énergie");
            return false;
        }
        if (tfSleepHours.getText() == null || tfSleepHours.getText().trim().isEmpty()) {
            showValidationError("Veuillez saisir les heures de sommeil");
            return false;
        }

        try {
            double hours = Double.parseDouble(tfSleepHours.getText().trim());
            if (hours < 0 || hours > 24) {
                showValidationError("Les heures doivent être entre 0 et 24");
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Format d'heures invalide");
            return false;
        }

        return true;
    }

    private void updateTrackerData() {
        tracker.setUserId(cbUserId.getValue());
        tracker.setRoutineTaskId(cbRoutineTaskId.getValue());
        tracker.setDate(datePicker.getValue().toString());
        tracker.setMood(cbMood.getValue());
        tracker.setStress(cbStress.getValue());
        tracker.setEnergy(cbEnergy.getValue());
        tracker.setSleepHours(Double.parseDouble(tfSleepHours.getText().trim()));
        tracker.setNote(taNote.getText());
    }

    @FXML
    public void goToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
            Stage stage = (Stage) lblId.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            showValidationError("Impossible de retourner au dashboard");
        }
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) lblId.getScene().getWindow();
        stage.close();
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("✅ Suivi modifié avec succès !");
        alert.showAndWait();
    }
}