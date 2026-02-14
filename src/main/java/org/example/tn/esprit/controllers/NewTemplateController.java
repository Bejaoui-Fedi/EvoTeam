package org.example.tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import org.example.tn.esprit.entities.User;
import org.example.tn.esprit.utils.Session;

import java.io.IOException;

public class NewTemplateController {

    @FXML
    private Label lblRole;
    @FXML
    private Label lblUserEmail;
    @FXML
    private Label lblPageTitle;
    @FXML
    private Label lblPageSubtitle;
    @FXML
    private TextField txtSearch;
    @FXML
    private StackPane contentArea;
    @FXML
    private Button btnAddNew;

    // Menu buttons
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnUsers;
    @FXML
    private Button btnEvents;
    @FXML
    private Button btnExercises;
    @FXML
    private Button btnWellbeing;
    @FXML
    private Button btnAppointments;

    private User currentUser;
    private boolean isAdmin;

    // Singleton instance for compatibility with existing controllers
    private static NewTemplateController instance;

    @FXML
    public void initialize() {
        // Set singleton instance for compatibility
        instance = this;

        // INTEGRATION: This detects if the teammate's module has already set the
        // session.
        // If Session.currentUser exists, we use it (Choice 1: Soudure).
        // Otherwise, we create a mock for local development.
        currentUser = Session.currentUser;

        if (currentUser != null) {
            String role = currentUser.getRole();
            isAdmin = "ADMIN".equalsIgnoreCase(role);

            // Update UI based on role
            lblRole.setText(role.toUpperCase());
            lblUserEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "user@evolia.com");

            // Configure menu visibility based on role
            configureMenuForRole();

            // Load default view (Exercises)
            goToExercises();
        } else {
            // No user logged in - for testing, create a mock admin
            System.out.println("WARNING: No user in session, creating mock admin for testing");
            createMockUser();
        }
    }

    private void createMockUser() {
        // Create a mock admin user for testing
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setNom("Admin Test");
        mockUser.setEmail("admin@evolia.com");
        mockUser.setRole("ADMIN");
        Session.currentUser = mockUser;

        currentUser = mockUser;
        isAdmin = true;
        lblRole.setText("ADMIN");
        lblUserEmail.setText("admin@evolia.com");

        configureMenuForRole();
        goToExercises();
    }

    private void configureMenuForRole() {
        if (!isAdmin) {
            // Hide admin-only menu items for regular users
            btnUsers.setVisible(false);
            btnUsers.setManaged(false);
            btnEvents.setVisible(false);
            btnEvents.setManaged(false);
        }
    }

    @FXML
    private void goToDashboard() {
        setActiveMenuItem(btnDashboard);
        lblPageTitle.setText("Tableau de bord");
        lblPageSubtitle.setText("Vue d'ensemble de votre activité");
        loadView("Dashboard.fxml", "Tableau de bord non encore implémenté");
    }

    @FXML
    private void goToUsers() {
        if (!isAdmin)
            return;

        setActiveMenuItem(btnUsers);
        lblPageTitle.setText("Gestion des Utilisateurs");
        lblPageSubtitle.setText("Gérez votre communauté avec bienveillance");
        loadView("Users.fxml", "Gestion des utilisateurs non encore implémentée");
    }

    @FXML
    private void goToEvents() {
        if (!isAdmin)
            return;

        setActiveMenuItem(btnEvents);
        lblPageTitle.setText("Événements");
        lblPageSubtitle.setText("Organisez et gérez vos événements");
        loadView("Events.fxml", "Gestion des événements non encore implémentée");
    }

    @FXML
    private void goToExercises() {
        setActiveMenuItem(btnExercises);
        lblPageTitle.setText("Gestion des Exercices");
        lblPageSubtitle.setText("Bibliothèque d'exercices de bien-être");

        // Hide search bar and add button for exercises view
        txtSearch.setVisible(false);
        btnAddNew.setVisible(false);

        // Load the objectives view (which shows exercises)
        loadView("AffichageObjectifs.fxml", null);
    }

    @FXML
    private void goToWellbeing() {
        setActiveMenuItem(btnWellbeing);
        lblPageTitle.setText("Bien-être");
        lblPageSubtitle.setText("Ressources pour votre équilibre");
        loadView("Wellbeing.fxml", "Module bien-être non encore implémenté");
    }

    @FXML
    private void goToAppointments() {
        setActiveMenuItem(btnAppointments);
        lblPageTitle.setText("Rendez-vous");
        lblPageSubtitle.setText("Gérez vos consultations");
        loadView("Appointments.fxml", "Gestion des rendez-vous non encore implémentée");
    }

    @FXML
    private void handleLogout() {
        // Clear session
        Session.currentUser = null;

        // Return to login/role selection
        System.out.println("Logout - returning to login screen");
        // TODO: Implement navigation to login screen
    }

    private void setActiveMenuItem(Button activeButton) {
        // Reset all menu items
        resetMenuItemStyle(btnDashboard);
        resetMenuItemStyle(btnUsers);
        resetMenuItemStyle(btnEvents);
        resetMenuItemStyle(btnExercises);
        resetMenuItemStyle(btnWellbeing);
        resetMenuItemStyle(btnAppointments);

        // Set active style
        activeButton.setStyle(
                "-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-font-size: 15; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 20; -fx-background-radius: 8; -fx-cursor: hand;");
    }

    private void resetMenuItemStyle(Button button) {
        button.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 15; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 20; -fx-background-radius: 8; -fx-cursor: hand;");
    }

    private void loadView(String fxmlFile, String fallbackMessage) {
        loadView(fxmlFile, fallbackMessage, null);
    }

    private void loadView(String fxmlFile, String fallbackMessage,
            java.util.function.Consumer<Object> controllerConsumer) {
        try {
            String path = fxmlFile.startsWith("/") ? fxmlFile : "/" + fxmlFile;
            java.net.URL resource = getClass().getResource(path);
            if (resource == null) {
                throw new IOException("FXML file not found: " + path);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();

            // Call controller consumer if provided
            Object controller = loader.getController();
            if (controllerConsumer != null && controller != null) {
                controllerConsumer.accept(controller);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            // Log for debugging
            System.err.println("CRITICAL ERROR loading view: " + fxmlFile);
            e.printStackTrace();

            contentArea.getChildren().clear();

            // Show error message in UI
            String errorMsg = (fallbackMessage != null) ? fallbackMessage
                    : "Erreur technique : " + e.getClass().getSimpleName();
            if (e.getMessage() != null && !e.getMessage().isBlank()) {
                errorMsg += "\n" + e.getMessage();
            }

            Label placeholder = new Label(errorMsg);
            placeholder.setStyle(
                    "-fx-font-size: 16; -fx-text-fill: #E74C3C; -fx-padding: 20; -fx-text-alignment: center;");
            placeholder.setWrapText(true);
            contentArea.getChildren().add(placeholder);
        }
    }

    // Static navigate method for compatibility with existing controllers
    public static void navigate(String fxmlPath, java.util.function.Consumer<Object> controllerConsumer) {
        if (instance == null) {
            System.err.println("ERROR: NewTemplateController.instance is null!");
            return;
        }
        instance.loadView(fxmlPath, null, controllerConsumer);
    }
}
