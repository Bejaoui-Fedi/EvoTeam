package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.DailyRoutineTask;
import tn.esprit.services.ServiceDailyRoutineTask;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DisplayDailyRoutineTask {

    @FXML private FlowPane cardsContainer;
    @FXML private Label lblCount;
    @FXML private ToggleGroup filterGroup;

    private final ServiceDailyRoutineTask serviceTask = new ServiceDailyRoutineTask();
    private List<DailyRoutineTask> allTasks;

    @FXML
    public void initialize() {
        loadTasks();

        // Listener pour les filtres
        filterGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                applyFilter();
            }
        });
    }

    private void loadTasks() {
        allTasks = serviceTask.getAll();
        displayTasks(allTasks);
    }

    private void displayTasks(List<DailyRoutineTask> tasks) {
        cardsContainer.getChildren().clear();
        lblCount.setText(tasks.size() + " tâche(s)");

        for (DailyRoutineTask task : tasks) {
            VBox card = createTaskCard(task);
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createTaskCard(DailyRoutineTask task) {
        VBox card = new VBox(10);
        card.setPrefWidth(280);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-border-color: " + (task.isCompleted() ? "#2ECC71" : "#E0E0E0") + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Titre
        Label titleLabel = new Label("📌 " + task.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setWrapText(true);
        titleLabel.setTextFill(Color.BLACK); // Force black text

        // Statut
        Label statusLabel = new Label(task.isCompleted() ? "✅ Complétée" : "⏳ À faire");
        statusLabel.setTextFill(task.isCompleted() ? Color.web("#2ECC71") : Color.web("#F39C12"));
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Info - User ID only
        Label infoLabel = new Label("👤 User: " + task.getUserId());
        infoLabel.setFont(Font.font("System", 12));
        infoLabel.setTextFill(Color.DARKGRAY); // Changed from GRAY to DARKGRAY for better visibility

        // Dates
        Label createdLabel = new Label("📅 Créée: " + task.getCreatedAt());
        createdLabel.setFont(Font.font("System", 11));
        createdLabel.setTextFill(Color.DARKGRAY);

        card.getChildren().addAll(titleLabel, statusLabel, infoLabel, createdLabel);

        if (task.isCompleted() && task.getCompletedAt() != null) {
            Label completedLabel = new Label("✓ Complétée: " + task.getCompletedAt());
            completedLabel.setFont(Font.font("System", 11));
            completedLabel.setTextFill(Color.web("#2ECC71"));
            card.getChildren().add(completedLabel);
        }

        // Boutons d'action
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button btnEdit = new Button("✏️");
        btnEdit.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        btnEdit.setOnAction(e -> goToUpdate(task));

        Button btnDelete = new Button("🗑️");
        btnDelete.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> deleteTask(task));

        if (!task.isCompleted()) {
            Button btnComplete = new Button("✓");
            btnComplete.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
            btnComplete.setOnAction(e -> markCompleted(task));
            buttons.getChildren().add(btnComplete);
        }

        buttons.getChildren().addAll(btnEdit, btnDelete);
        card.getChildren().add(buttons);

        return card;
    }

    private void applyFilter() {
        RadioButton selected = (RadioButton) filterGroup.getSelectedToggle();
        String filter = selected.getText();

        List<DailyRoutineTask> filtered = allTasks.stream()
                .filter(t -> {
                    switch (filter) {
                        case "À faire": return !t.isCompleted();
                        case "Complétées": return t.isCompleted();
                        default: return true;
                    }
                })
                .collect(Collectors.toList());

        displayTasks(filtered);
    }

    private void markCompleted(DailyRoutineTask task) {
        serviceTask.markAsCompleted(task.getId());
        refresh();
    }

    private void deleteTask(DailyRoutineTask task) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer cette tâche '" + task.getTitle() + "' ?");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                serviceTask.delete(task.getId());
                refresh();
            }
        });
    }

    private void goToUpdate(DailyRoutineTask task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateDailyRoutineTask.fxml"));
            Parent root = loader.load();

            UpdateDailyRoutineTask controller = loader.getController();
            controller.initData(task);

            Stage stage = new Stage();
            stage.setTitle("Modifier Tâche - " + task.getTitle());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh after closing
            refresh();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la modification");
        }
    }

    @FXML
    void goToAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddDailyRoutineTask.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nouvelle Tâche");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh after closing
            refresh();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire");
        }
    }

    @FXML
    void refresh() {
        loadTasks();
    }

    @FXML
    void goToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdminDashboard.fxml"));
            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner au dashboard");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}