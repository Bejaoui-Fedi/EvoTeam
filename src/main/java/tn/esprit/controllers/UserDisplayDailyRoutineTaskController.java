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
import tn.esprit.entities.User;
import tn.esprit.services.ServiceDailyRoutineTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDisplayDailyRoutineTaskController {

    @FXML private FlowPane cardsContainer;
    @FXML private Label lblCount;
    @FXML private ToggleGroup filterGroup;

    private final ServiceDailyRoutineTask serviceTask = new ServiceDailyRoutineTask();
    private List<DailyRoutineTask> allTasks = new ArrayList<>();
    private User currentUser;
    private UserDashboardController dashboardController;

    @FXML
    public void initialize() {
        // Initially load nothing until user is set, or try loading if already set
        if (currentUser != null) {
            loadTasks();
        }

        // Listener pour les filtres
        filterGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                applyFilter();
            }
        });
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (this.currentUser != null) {
            loadTasks();
        }
    }

    public void setUserDashboardController(UserDashboardController controller) {
        this.dashboardController = controller;
    }

    private void loadTasks() {
        if (currentUser == null) {
            System.err.println("❌ Erreur : Utilisateur non défini dans UserDisplayDailyRoutineTaskController");
            return;
        }
        // Filter by user ID
        allTasks = serviceTask.getByUserId(currentUser.getId());
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
        titleLabel.setTextFill(Color.BLACK);

        // Statut
        Label statusLabel = new Label(task.isCompleted() ? "✅ Complétée" : "⏳ À faire");
        statusLabel.setTextFill(task.isCompleted() ? Color.web("#2ECC71") : Color.web("#F39C12"));
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Info - User ID only
        Label infoLabel = new Label("👤 User: " + task.getUserId());
        infoLabel.setFont(Font.font("System", 12));
        infoLabel.setTextFill(Color.DARKGRAY);

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

        // EDIT BUTTON
        Button btnEdit = new Button("✏️");
        btnEdit.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        btnEdit.setOnAction(e -> goToUpdate(task));

        // REMOVED DELETE BUTTON

        if (!task.isCompleted()) {
            Button btnComplete = new Button("✓");
            btnComplete.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
            btnComplete.setOnAction(e -> markCompleted(task));
            buttons.getChildren().add(btnComplete);
        }

        buttons.getChildren().addAll(btnEdit); // Only Edit remains
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

    // REMOVED deleteTask method

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

    // REMOVED goToAdd method

    @FXML
    void refresh() {
        if (currentUser != null) {
            loadTasks();
        }
    }

    // REMOVED goToDashboard (handled by UserDashboardController logic usually, but if needed we can keep it or use dashboardController)
    // The user didn't ask to remove it explicitly, but they are embedding this INTO the dashboard.
    // If embedded, "Go to Dashboard" button inside the view might be redundant or broken if it tries to set scene.
    // However, I will check the user request again. "je veux ajouter cette interface dans le bouton bien etre dans le nav bar de userdashboard"
    // So it's embedded. I should probably REMOVE the "Go to Dashboard" button from the bottom if it existed, or just ignore it.
    // The original FXML had "Footer" with copyright, but the Controller had `goToDashboard`.
    // I will remove `goToDashboard` from here since it's inside the dashboard efficiently.

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
