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

public class UpdateDailyRoutineTask {

    @FXML private Label lblId;
    @FXML private ComboBox<Integer> cbUserId;
    @FXML private TextField tfTitle;
    @FXML private CheckBox chkCompleted;
    @FXML private Label lblCompletedAt;
    @FXML private Label lblCreatedAt;

    private DailyRoutineTask task;
    private final ServiceDailyRoutineTask serviceTask = new ServiceDailyRoutineTask();
    private final UserService serviceUser = new UserService();

    @FXML
    public void initialize() {
        try {
            List<Integer> userIds = serviceUser.getAllUsersIds();
            cbUserId.setItems(FXCollections.observableArrayList(userIds));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initData(DailyRoutineTask task) {
        this.task = task;

        lblId.setText("ID: " + task.getId());
        cbUserId.setValue(task.getUserId());
        tfTitle.setText(task.getTitle());
        chkCompleted.setSelected(task.isCompleted());
        lblCompletedAt.setText(task.getCompletedAt() != null ? task.getCompletedAt() : "Non complétée");
        lblCreatedAt.setText(task.getCreatedAt());
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        if (cbUserId.getValue() == null || tfTitle.getText() == null || tfTitle.getText().trim().isEmpty()) {
            showAlert("Erreur", "Tous les champs sont obligatoires !");
            return;
        }

        task.setUserId(cbUserId.getValue());
        task.setTitle(tfTitle.getText().trim());
        task.setCompleted(chkCompleted.isSelected());

        if (chkCompleted.isSelected() && task.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now().toString());
        } else if (!chkCompleted.isSelected()) {
            task.setCompletedAt(null);
        }

        try {
            serviceTask.update(task);

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setHeaderText(null);
            success.setContentText("✅ Tâche modifiée avec succès !");
            success.showAndWait();

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier la tâche: " + e.getMessage());
        }
    }

    @FXML
    void goToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdminDashboard.fxml"));
            Stage stage = (Stage) lblId.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner au dashboard");
        }
    }

    @FXML
    void closeWindow() {
        Stage stage = (Stage) lblId.getScene().getWindow();
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