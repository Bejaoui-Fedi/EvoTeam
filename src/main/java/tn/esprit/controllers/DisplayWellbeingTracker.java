package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.WellbeingTracker;
import tn.esprit.services.ServiceWellbeingTracker;

import java.io.IOException;
import java.util.List;
import javafx.stage.Modality;

public class DisplayWellbeingTracker {

    @FXML private FlowPane cardsContainer;
    @FXML private Label lblCount;
    @FXML private Label lblAvgMood;
    @FXML private Label lblAvgSleep;

    private final ServiceWellbeingTracker serviceTracker = new ServiceWellbeingTracker();

    @FXML
    public void initialize() {
        loadCards();
    }

    @FXML
    private void refresh() {
        loadCards();
    }

    private void loadCards() {
        cardsContainer.getChildren().clear();
        try {
            List<WellbeingTracker> trackers = serviceTracker.getAll();
            lblCount.setText(trackers.size() + " suivi(s)");

            // Calculer les moyennes
            double avgMood = trackers.stream().mapToInt(WellbeingTracker::getMood).average().orElse(0);
            double avgSleep = trackers.stream().mapToDouble(WellbeingTracker::getSleepHours).average().orElse(0);

            lblAvgMood.setText(String.format("😊 Humeur moyenne: %.1f/5", avgMood));
            lblAvgSleep.setText(String.format("😴 Sommeil moyen: %.1fh", avgSleep));

            for (WellbeingTracker t : trackers) {
                cardsContainer.getChildren().add(createCard(t));
            }

        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les suivis: " + e.getMessage());
        }
    }

    private VBox createCard(WellbeingTracker t) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setStyle("""
            -fx-background-color: white;
            -fx-padding: 16;
            -fx-border-color: #E2E2E2;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);
            -fx-cursor: hand;
            """);

        // En-tête avec date
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label dateLabel = new Label("📅 " + t.getDate());
        dateLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnEdit = new Button("✏️");
        Button btnDelete = new Button("🗑️");

        btnEdit.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        btnDelete.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");

        btnEdit.setOnAction(e -> openUpdateWindow(t));
        btnDelete.setOnAction(e -> confirmDelete(t));

        header.getChildren().addAll(dateLabel, spacer, btnEdit, btnDelete);

        // Indicateurs (Mood, Stress, Energy, Sleep)
        HBox indicators = new HBox(15);
        indicators.setAlignment(Pos.CENTER_LEFT);

        Label mood = new Label("😊 " + t.getMood() + "/5");
        Label stress = new Label("😰 " + t.getStress() + "/5");
        Label energy = new Label("⚡ " + t.getEnergy() + "/5");
        Label sleep = new Label("😴 " + t.getSleepHours() + "h");

        String indicatorStyle = "-fx-background-color: #F0F0F0; -fx-padding: 4 10; -fx-background-radius: 20; -fx-text-fill: #2C3E50;";
        mood.setStyle(indicatorStyle);
        stress.setStyle(indicatorStyle);
        energy.setStyle(indicatorStyle);
        sleep.setStyle(indicatorStyle);

        indicators.getChildren().addAll(mood, stress, energy, sleep);

        // Note
        Label note = new Label(t.getNote() == null || t.getNote().isEmpty() ?
                "(aucune note)" : "📝 " + t.getNote());
        note.setWrapText(true);
        note.setStyle("-fx-text-fill: #555; -fx-font-style: italic;");

        // Métadonnées (User ID et Task ID)
        Label meta = new Label("👤 User: " + t.getUserId() + " | ✅ Task: " + t.getRoutineTaskId());
        meta.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 11;");

        card.getChildren().addAll(header, new Separator(), indicators, note, meta);
        return card;
    }

    private void confirmDelete(WellbeingTracker tracker) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer ce suivi du " + tracker.getDate() + " ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceTracker.delete(tracker.getId());
                    refresh();
                    showInfo("Succès", "✅ Suivi supprimé !");
                } catch (Exception e) {
                    showAlert("Erreur", "Impossible de supprimer le suivi");
                }
            }
        });
    }

    private void openUpdateWindow(WellbeingTracker tracker) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateWellbeingTracker.fxml"));
            Parent root = loader.load();

            UpdateWellbeingTracker controller = loader.getController();
            controller.initData(tracker);

            Stage stage = new Stage();
            stage.setTitle("Modifier suivi - " + tracker.getDate());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            refresh(); // Refresh after closing
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la modification: " + e.getMessage());
        }
    }
    @FXML
    private void goToAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddWellbeingTracker.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un suivi bien-être");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh after closing
            refresh();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'ajout: " + e.getMessage());
        }
    }
    @FXML
    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard Administrateur");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner au dashboard: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}