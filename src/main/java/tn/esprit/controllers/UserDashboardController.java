package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserDashboardController implements Initializable {

    // ==================== FXML INJECTIONS ====================
    @FXML private StackPane contentArea;
    @FXML private Button btnDashboard;
    @FXML private Button btnEvents;
    @FXML private Button btnExercises;
    @FXML private Button btnWellness;
    @FXML private Button btnAppointments;
    @FXML private Button btnProfile;
    @FXML private Button btnNotifications;
    @FXML private Button btnLogout;
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;

    // ==================== VARIABLES ====================
    private User currentUser;
    private static final String LOGIN_VIEW_PATH = "/LoginView.fxml";
    private static final String USER_PROFILE_PATH = "/UserProfileView.fxml";

    // ✅ STYLE UNIQUE pour TOUS les boutons !
    private static final String ACTIVE_BUTTON_STYLE =
            "-fx-background-color: rgba(255,255,255,0.2);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 16;" +
                    "-fx-font-weight: bold;" +
                    "-fx-alignment: CENTER_LEFT;" +
                    "-fx-pref-width: 230;" +
                    "-fx-pref-height: 45;" +
                    "-fx-padding: 0 0 0 20;" +
                    "-fx-background-radius: 25;" +
                    "-fx-border-color: white;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 25;" +
                    "-fx-cursor: hand;";

    private static final String INACTIVE_BUTTON_STYLE =
            "-fx-background-color: rgba(255,255,255,0.05);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 16;" +
                    "-fx-alignment: CENTER_LEFT;" +
                    "-fx-pref-width: 230;" +
                    "-fx-pref-height: 45;" +
                    "-fx-padding: 0 0 0 20;" +
                    "-fx-background-radius: 25;" +
                    "-fx-cursor: hand;";

    // ==================== INITIALIZATION ====================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ UserDashboardController initialisé");
        resetButtonStyles();
        setActiveButton(btnDashboard);
        showDashboard();
    }

    // ==================== USER DATA METHODS ====================
    public void setCurrentUser(User user) {
        if (user == null) {
            System.err.println("⚠️ Tentative de setCurrentUser avec null");
            return;
        }
        this.currentUser = user;
        // IMPORTANT: Set the global session so other controllers (like NewTemplate) can access it
        tn.esprit.utils.Session.currentUser = user;

        updateUserInfo();
        System.out.println("👤 Utilisateur connecté: " + user.getNom() + " (" + user.getRole() + ")");
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    private void updateUserInfo() {
        if (currentUser != null) {
            if (userNameLabel != null) userNameLabel.setText(currentUser.getNom());
            if (userEmailLabel != null) userEmailLabel.setText(currentUser.getEmail());
        }
    }

    // ==================== NAVIGATION METHODS ====================
    @FXML
    private void handleDashboard() {
        System.out.println("📊 Navigation vers Dashboard");
        resetButtonStyles();
        setActiveButton(btnDashboard);
        showDashboard();
    }

    private void showDashboard() {
        VBox dashboard = createDashboardView();
        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboard);
    }

    private VBox createDashboardView() {
        VBox dashboard = new VBox(30);
        dashboard.setAlignment(Pos.CENTER);
        dashboard.setStyle("-fx-padding: 50; -fx-background-color: transparent;");

        Label welcome = new Label("💙 Bienvenue dans votre espace personnel");
        welcome.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: #3A7D6B;");
        Label subtitle = new Label("Sélectionnez une option dans le menu de gauche");
        subtitle.setStyle("-fx-font-size: 20; -fx-text-fill: #5F9B8A;");

        HBox icons = new HBox(20);
        icons.setAlignment(Pos.CENTER);
        String[] iconList = {"📅", "🧘", "📝", "📆", "👤"};
        for (String icon : iconList) {
            Label iconLabel = new Label(icon);
            iconLabel.setStyle("-fx-font-size: 48;");
            icons.getChildren().add(iconLabel);
        }

        dashboard.getChildren().addAll(welcome, subtitle, icons);
        return dashboard;
    }

    // =====================================================
    // TES MODIFICATIONS : handleEvents() avec passage du currentUser
    // =====================================================
    @FXML
    private void handleEvents() {
        System.out.println("Navigation vers Evenements (mode utilisateur)");
        resetButtonStyles();
        setActiveButton(btnEvents);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDisplayEvent.fxml"));
            Parent root = loader.load();

            // Passer la reference du dashboard ET le currentUser au controller
            UserDisplayEvent controller = loader.getController();
            controller.setUserDashboardController(this);
            controller.setCurrentUser(currentUser);  // ✅ AJOUTÉ : passage du currentUser

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);

        } catch (IOException e) {
            System.err.println("Erreur chargement UserDisplayEvent.fxml: " + e.getMessage());
            e.printStackTrace();
            showError("Page Evenements non trouvee");
        }
    }

    @FXML
    private void handleExercises() {
        System.out.println("Navigation vers Exercices");
        resetButtonStyles();
        setActiveButton(btnExercises);

        try {
            // Charger le NewTemplateController qui contient la gestion des exercices
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewTemplate.fxml"));
            Parent exerciseView = loader.load();

            // Remplacer le contenu
            contentArea.getChildren().clear();
            contentArea.getChildren().add(exerciseView);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible de charger la page Exercices");
        } catch (NullPointerException e) {
            System.err.println("❌ Fichier FXML non trouvé ! Vérifiez le chemin : /NewTemplate.fxml");
            showError("Fichier NewTemplate.fxml introuvable !");
        }
    }

    @FXML
    private void handleWellness() {
        System.out.println("📝 Navigation vers Journal bien-être");
        resetButtonStyles();
        setActiveButton(btnWellness);
        showPlaceholder("Journal bien-être");
    }

    @FXML
    private void handleAppointments() {
        System.out.println("📆 Navigation vers Mes rendez-vous");
        resetButtonStyles();
        setActiveButton(btnAppointments);
        showPlaceholder("Mes rendez-vous");
    }

    @FXML
    private void handleNotifications() {
        System.out.println("🔔 Navigation vers Notifications");
        resetButtonStyles();
        setActiveButton(btnNotifications);
        showPlaceholder("Notifications");
    }

    @FXML
    private void handleProfile() {
        System.out.println("👤 Navigation vers Mon profil");
        resetButtonStyles();
        setActiveButton(btnProfile);

        if (currentUser == null) {
            showError("Aucun utilisateur connecté");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(USER_PROFILE_PATH));
            if (loader.getLocation() == null) {
                throw new IOException("FXML file not found: " + USER_PROFILE_PATH);
            }

            Parent profilView = loader.load();
            UserProfileController profileController = loader.getController();

            if (profileController != null) {
                profileController.setUser(currentUser);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(profilView);

        } catch (IOException e) {
            System.err.println("❌ Erreur chargement UserProfileView.fxml: " + e.getMessage());
            showError("Page de profil non trouvée");
        }
    }

    @FXML
    private void handleLogout() {
        System.out.println("🚪 Déconnexion - Navigation vers l'accueil");

        try {
            // ✅ CHARGER LA PAGE D'ACCUEIL
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeView.fxml"));
            Parent homeView = loader.load();

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(homeView, 1400, 800));
            stage.setTitle("Evolia - Accueil");
            stage.centerOnScreen();
            stage.show();
            System.out.println("✅ Redirection vers l'accueil réussie");

        } catch (IOException e) {
            System.err.println("❌ Erreur chargement HomeView.fxml: " + e.getMessage());
            showError("Page d'accueil non trouvée");
        }
    }

    // ==================== UTILITY METHODS ====================
    private void resetButtonStyles() {
        btnDashboard.setStyle(INACTIVE_BUTTON_STYLE);
        btnEvents.setStyle(INACTIVE_BUTTON_STYLE);
        btnExercises.setStyle(INACTIVE_BUTTON_STYLE);
        btnWellness.setStyle(INACTIVE_BUTTON_STYLE);
        btnAppointments.setStyle(INACTIVE_BUTTON_STYLE);
        btnProfile.setStyle(INACTIVE_BUTTON_STYLE);
        btnNotifications.setStyle(INACTIVE_BUTTON_STYLE);
        btnLogout.setStyle(INACTIVE_BUTTON_STYLE);
    }

    private void setActiveButton(Button button) {
        button.setStyle(ACTIVE_BUTTON_STYLE);
    }

    @FXML
    private void onButtonHover(javafx.scene.input.MouseEvent event) {
        Button button = (Button) event.getSource();
        if (!button.getStyle().contains("rgba(255,255,255,0.2)")) {
            button.setStyle("-fx-background-color: rgba(255,255,255,0.15); " +
                    "-fx-text-fill: white; -fx-font-size: 16; " +
                    "-fx-alignment: CENTER_LEFT; -fx-pref-width: 230; " +
                    "-fx-pref-height: 45; -fx-padding: 0 0 0 20; " +
                    "-fx-background-radius: 25; -fx-cursor: hand;");
        }
    }

    @FXML
    private void onButtonExit(javafx.scene.input.MouseEvent event) {
        Button button = (Button) event.getSource();
        if (!button.getStyle().contains("rgba(255,255,255,0.2)")) {
            button.setStyle(INACTIVE_BUTTON_STYLE);
        }
    }

    private void showPlaceholder(String module) {
        VBox placeholder = new VBox(20);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setStyle("-fx-padding: 50; -fx-background-color: transparent;");
        Label icon = new Label("🔄");
        icon.setStyle("-fx-font-size: 64;");
        Label title = new Label("📋 " + module);
        title.setStyle("-fx-font-size: 32; -fx-font-weight: bold; -fx-text-fill: #3A7D6B;");
        Label message = new Label("Cette fonctionnalité sera bientôt disponible");
        message.setStyle("-fx-font-size: 18; -fx-text-fill: #5F9B8A;");
        placeholder.getChildren().addAll(icon, title, message);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(placeholder);
    }

    private void showError(String message) {
        VBox errorBox = new VBox(20);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setStyle("-fx-padding: 50; -fx-background-color: transparent;");
        Label errorIcon = new Label("❌");
        errorIcon.setStyle("-fx-font-size: 64; -fx-text-fill: #B85C5C;");
        Label errorMessage = new Label(message);
        errorMessage.setStyle("-fx-text-fill: #B85C5C; -fx-font-size: 18; -fx-font-weight: bold;");
        errorBox.getChildren().addAll(errorIcon, errorMessage);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(errorBox);
    }

    /**
     * Sets the content of the main content area
     * @param content The Parent node to display in the content area
     */
    public void setContent(Parent content) {
        if (contentArea != null) {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } else {
            System.err.println("⚠️ contentArea is null in UserDashboardController");
        }
    }
}