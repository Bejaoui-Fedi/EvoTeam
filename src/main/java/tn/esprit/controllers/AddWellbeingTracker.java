package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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

public class AddWellbeingTracker {

    @FXML private ComboBox<Integer> cbUserId;
    @FXML private ComboBox<Integer> cbRoutineTaskId;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Integer> cbMood;
    @FXML private ComboBox<Integer> cbStress;
    @FXML private ComboBox<Integer> cbEnergy;
    @FXML private TextField tfSleepHours;
    @FXML private TextArea taNote;

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
            if (userIds.isEmpty()) {
                cbUserId.setPromptText("Aucun utilisateur disponible");
                cbUserId.setDisable(true);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les utilisateurs: " + e.getMessage());
        }
    }

    private void loadTaskIds() {
        try {
            List<Integer> taskIds = serviceTask.getAllTaskIds();
            cbRoutineTaskId.setItems(FXCollections.observableArrayList(taskIds));
            if (taskIds.isEmpty()) {
                cbRoutineTaskId.setPromptText("Aucune tâche disponible");
                cbRoutineTaskId.setDisable(true);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les tâches: " + e.getMessage());
        }
    }

    private void loadDefaultValues() {
        cbMood.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbStress.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbEnergy.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));

        datePicker.setValue(LocalDate.now());
        cbMood.setValue(3);
        cbStress.setValue(3);
        cbEnergy.setValue(3);
        tfSleepHours.setText("8.0");
    }

    @FXML
    void handleAddTracker(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        double sleepHours = Double.parseDouble(tfSleepHours.getText().trim());

        WellbeingTracker tracker = new WellbeingTracker(
                cbUserId.getValue(),
                cbRoutineTaskId.getValue(),
                datePicker.getValue().toString(),
                cbMood.getValue(),
                cbStress.getValue(),
                cbEnergy.getValue(),
                sleepHours,
                taNote.getText()
        );

        try {
            serviceTracker.ajouter(tracker);

            // Show success message
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setHeaderText(null);
            success.setContentText("✅ Suivi bien-être ajouté avec succès !");
            success.showAndWait();

            // Close the current window
            closeWindow();

        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ajouter le suivi: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (cbUserId.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur");
            return false;
        }
        if (cbRoutineTaskId.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une tâche");
            return false;
        }
        if (datePicker.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une date");
            return false;
        }
        if (cbMood.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une humeur");
            return false;
        }
        if (cbStress.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner un niveau de stress");
            return false;
        }
        if (cbEnergy.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner un niveau d'énergie");
            return false;
        }
        if (tfSleepHours.getText() == null || tfSleepHours.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez saisir les heures de sommeil");
            return false;
        }

        try {
            double hours = Double.parseDouble(tfSleepHours.getText().trim());
            if (hours < 0 || hours > 24) {
                showAlert("Erreur", "Les heures de sommeil doivent être entre 0 et 24");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format d'heures de sommeil invalide");
            return false;
        }

        return true;
    }

    @FXML
    private void goToDisplay() {
        // Just close this window - the display window is already open
        closeWindow();
    }

    @FXML
    private void goToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdminDashboard.fxml"));
            Stage stage = (Stage) cbUserId.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner au dashboard");
        }
    }

    @FXML
    private void resetForm() {
        cbUserId.getSelectionModel().clearSelection();
        cbRoutineTaskId.getSelectionModel().clearSelection();
        datePicker.setValue(LocalDate.now());
        cbMood.setValue(3);
        cbStress.setValue(3);
        cbEnergy.setValue(3);
        tfSleepHours.setText("8.0");
        taNote.clear();
    }

    /**
     * Close the current window
     */
    private void closeWindow() {
        Stage stage = (Stage) cbUserId.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}