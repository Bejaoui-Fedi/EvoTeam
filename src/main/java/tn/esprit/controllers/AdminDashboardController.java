package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.Separator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class AdminDashboardController implements Initializable {

    @FXML
    private StackPane contentArea;

    // ================ BOUTONS DE NAVIGATION ================
    @FXML private Button btnDashboard;
    @FXML private Button btnUsers;
    @FXML private Button btnEvents;
    @FXML private Button btnExercises;
    @FXML private Button btnWellness;
    @FXML private Button btnAppointments;
    @FXML private Button btnLogout;

    // Style pour bouton actif
    private final String ACTIVE_BUTTON_STYLE = "-fx-background-color: rgba(255,255,255,0.2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16; " +
            "-fx-font-weight: bold; " +
            "-fx-alignment: CENTER_LEFT; " +
            "-fx-pref-width: 210; " +
            "-fx-padding: 12 15 12 15; " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: white; " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 15; " +
            "-fx-cursor: hand;";

    private final String INACTIVE_BUTTON_STYLE = "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16; " +
            "-fx-alignment: CENTER_LEFT; " +
            "-fx-pref-width: 210; " +
            "-fx-padding: 12 15 12 15; " +
            "-fx-background-radius: 15; " +
            "-fx-cursor: hand;";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("AdminDashboardController initialise");
        handleDashboard();
    }

    // ================ METHODES DE NAVIGATION ================

    @FXML
    private void handleDashboard() {
        System.out.println("Navigation vers Tableau de bord");
        resetButtonStyles();
        setActiveButton(btnDashboard);
        showPlaceholder("Tableau de bord");
    }

    @FXML
    private void handleUsers() {
        System.out.println("Navigation vers UserListView");
        resetButtonStyles();
        setActiveButton(btnUsers);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserListView.fxml"));
            Parent userListView = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(userListView);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible de charger la vue des utilisateurs");
        } catch (NullPointerException e) {
            System.err.println("Fichier FXML non trouve ! Verifiez le chemin : /UserListView.fxml");
            showError("Fichier FXML non trouve !");
        }
    }

    /**
     * Methode publique pour charger un contenu dans le contentArea.
     * Utilisee par DisplayEvent et DisplayReview pour naviguer
     * SANS perdre le sidebar.
     */
    public void setContent(Parent node) {
        contentArea.getChildren().setAll(node);
    }

    // =====================================================
    // CORRIGE : On recupere le controller DisplayEvent
    // et on lui passe "this" comme reference dashboard
    // pour que la navigation Event -> Review reste dans le contentArea
    // =====================================================
    @FXML
    private void handleEvents() {
        System.out.println("Navigation vers Evenements");
        resetButtonStyles();
        setActiveButton(btnEvents);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayEvent.fxml"));
            Parent eventView = loader.load();

            // IMPORTANT : passer la reference du dashboard au DisplayEvent
            DisplayEvent controller = loader.getController();
            controller.setDashboardController(this);

            contentArea.getChildren().setAll(eventView);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible de charger la page Evenements");
        }
    }

    @FXML
    private void handleExercises() {
        System.out.println("Navigation vers Exercices");
        resetButtonStyles();
        setActiveButton(btnExercises);
        showPlaceholder("Exercices");
    }


    //honest to god i hate my life
    //count of how many times i changed this beacuse i am dump: 17

    @FXML
    private void handleWellness() {
        System.out.println("Navigation vers Bien-etre");
        resetButtonStyles();
        setActiveButton(btnWellness);

        try {
            // Create a VBox to hold both views
            VBox combinedView = new VBox(20);
            combinedView.setStyle("-fx-padding: 20; -fx-background-color: transparent;");

            // Header
            Label headerLabel = new Label("🌿 Espace Bien-être");
            headerLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #3A7D6B;");

            // Load WellbeingTracker view
            FXMLLoader wellbeingLoader = new FXMLLoader(getClass().getResource("/DisplayWellbeingTracker.fxml"));
            Parent wellbeingView = wellbeingLoader.load();

            // Add separator
            Separator separator1 = new Separator();
            separator1.setStyle("-fx-background-color: #3A7D6B; -fx-opacity: 0.3;");

            // Label for Daily Tasks
            Label tasksLabel = new Label("✅ Tâches quotidiennes");
            tasksLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #3A7D6B; -fx-padding: 10 0 0 0;");

            // Load DailyRoutineTask view (once you have the file)
            Parent dailyTasksView = null;
            try {
                FXMLLoader tasksLoader = new FXMLLoader(getClass().getResource("/DisplayDailyRoutineTask.fxml"));
                dailyTasksView = tasksLoader.load();
            } catch (Exception e) {
                // Create placeholder if file doesn't exist
                VBox placeholder = new VBox(10);
                placeholder.setStyle("""
                -fx-background-color: rgba(255,255,255,0.7);
                -fx-background-radius: 15;
                -fx-border-radius: 15;
                -fx-border-color: #3A7D6B;
                -fx-border-width: 1;
                -fx-padding: 20;
                """);
                placeholder.getChildren().add(new Label("📋 Vue des tâches quotidiennes"));
                dailyTasksView = placeholder;
            }

            // Add everything to combined view
            combinedView.getChildren().addAll(
                    headerLabel,
                    wellbeingView,
                    separator1,
                    tasksLabel,
                    dailyTasksView
            );

            // Wrap in ScrollPane for scrolling
            ScrollPane scrollPane = new ScrollPane(combinedView);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

            // Clear and add to content area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(scrollPane);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible de charger la vue Bien-etre");
        }
    }

    @FXML
    private void handleAppointments() {
        System.out.println("Navigation vers Rendez-vous");
        resetButtonStyles();
        setActiveButton(btnAppointments);
        showPlaceholder("Rendez-vous");
    }

    @FXML
    private void handleLogout() {
        System.out.println("Deconnexion - Navigation vers LoginView");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent loginView = loader.load();

            contentArea.getScene().setRoot(loginView);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible de charger la page de connexion");
        } catch (NullPointerException e) {
            System.err.println("Fichier FXML non trouve ! Verifiez le chemin : /LoginView.fxml");
            showError("Fichier LoginView.fxml introuvable !");
        }
    }

    // ================ METHODES POUR LE STYLE DYNAMIQUE ================

    private void resetButtonStyles() {
        btnDashboard.setStyle(INACTIVE_BUTTON_STYLE);
        btnUsers.setStyle(INACTIVE_BUTTON_STYLE);
        btnEvents.setStyle(INACTIVE_BUTTON_STYLE);
        btnExercises.setStyle(INACTIVE_BUTTON_STYLE);
        btnWellness.setStyle(INACTIVE_BUTTON_STYLE);
        btnAppointments.setStyle(INACTIVE_BUTTON_STYLE);
        btnLogout.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-font-size: 15; " +
                "-fx-alignment: CENTER_LEFT; -fx-pref-width: 210; -fx-padding: 12 15 12 15; " +
                "-fx-background-radius: 15; -fx-cursor: hand;");
    }

    private void setActiveButton(Button button) {
        if (button == btnLogout) {
            button.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 15; " +
                    "-fx-font-weight: bold; -fx-alignment: CENTER_LEFT; -fx-pref-width: 210; -fx-padding: 12 15 12 15; " +
                    "-fx-background-radius: 15; -fx-border-color: white; -fx-border-width: 1.5; " +
                    "-fx-border-radius: 15; -fx-cursor: hand;");
        } else {
            button.setStyle(ACTIVE_BUTTON_STYLE);
        }
    }

    // ================ METHODES UTILITAIRES ================

    private void showWelcomeView() {
        VBox welcomeBox = new VBox(30);
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.setStyle("-fx-padding: 50; -fx-background-color: transparent;");

        Label title = new Label("Bienvenue dans l'espace Administration");
        title.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: #3A7D6B;");

        Label subtitle = new Label("Selectionnez une option dans le menu de gauche");
        subtitle.setStyle("-fx-font-size: 20; -fx-text-fill: #5F9B8A;");

        welcomeBox.getChildren().addAll(title, subtitle);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(welcomeBox);
    }

    private void showPlaceholder(String module) {
        VBox placeholder = new VBox(20);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setStyle("-fx-padding: 50; -fx-background-color: transparent;");

        Label title = new Label(module);
        title.setStyle("-fx-font-size: 32; -fx-font-weight: bold; -fx-text-fill: #3A7D6B;");

        Label message = new Label("Cette fonctionnalite sera bientot disponible");
        message.setStyle("-fx-font-size: 18; -fx-text-fill: #5F9B8A;");

        placeholder.getChildren().addAll(title, message);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(placeholder);
    }

    private void showError(String message) {
        VBox errorBox = new VBox(20);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setStyle("-fx-padding: 50; -fx-background-color: transparent;");

        Label errorMessage = new Label(message);
        errorMessage.setStyle("-fx-text-fill: #B85C5C; -fx-font-size: 18; -fx-font-weight: bold;");

        errorBox.getChildren().addAll(errorMessage);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(errorBox);
    }
}
