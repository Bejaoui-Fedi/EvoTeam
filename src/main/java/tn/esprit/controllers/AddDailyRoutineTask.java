package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.DailyRoutineTask;
import tn.esprit.services.ServiceDailyRoutineTask;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class AddDailyRoutineTask {

    @FXML private ComboBox<Integer> cbUserId;
    @FXML private TextField tfTitle;
    @FXML private CheckBox chkCompleted;

    private final ServiceDailyRoutineTask serviceTask = new ServiceDailyRoutineTask();
    private final UserService serviceUser = new UserService();

    @FXML
    public void initialize() {
        loadUserIds();
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

    @FXML
    void handleAddTask(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        DailyRoutineTask task = new DailyRoutineTask(
                cbUserId.getValue(),
                tfTitle.getText().trim()
        );

        task.setCompleted(chkCompleted.isSelected());
        if (chkCompleted.isSelected()) {
            task.setCompletedAt(LocalDateTime.now().toString());
        }

        try {
            serviceTask.ajouter(task);

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setHeaderText(null);
            success.setContentText("✅ Tâche ajoutée avec succès !");
            success.showAndWait();

            // Close the current window
            closeWindow();

        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ajouter la tâche: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (cbUserId.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur");
            return false;
        }
        if (tfTitle.getText() == null || tfTitle.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez saisir un titre");
            return false;
        }
        return true;
    }

    @FXML
    private void goToDisplay() {
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
        tfTitle.clear();
        chkCompleted.setSelected(false);
    }

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