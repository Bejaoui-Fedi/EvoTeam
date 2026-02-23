package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import tn.esprit.entities.User;
import tn.esprit.utils.Session;

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
    @FXML
    private Button btnLogout;

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
            if (lblRole != null) lblRole.setText(role.toUpperCase());
            if (lblUserEmail != null) lblUserEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "user@evolia.com");

            // Load default view (Exercises)
            goToExercises();
        } else {
            // No user logged in - treat as Guest/User for safety
            System.out.println("WARNING: No user in session. Defaulting to Guest privileges.");
            isAdmin = false;
            currentUser = null;
            if (lblRole != null) lblRole.setText("GUEST");
            if (lblUserEmail != null) lblUserEmail.setText("");
            
            goToExercises();
        }
    }

    @FXML
    private void goToExercises() {
        if (lblPageTitle != null) lblPageTitle.setText("Gestion des Exercices");
        if (lblPageSubtitle != null) lblPageSubtitle.setText("Bibliothèque d'exercices de bien-être");

        // Hide search bar and add button for exercises view
        if (txtSearch != null) txtSearch.setVisible(false);
        if (btnAddNew != null) btnAddNew.setVisible(false);

        // Load the objectives view (which shows exercises)
        loadView("AffichageObjectifs.fxml", null);
    }

    @FXML
    private void handleLogout() {
        // Clear session
        Session.currentUser = null;

        // Return to login/role selection
        System.out.println("Logout - returning to login screen");
        try {
             FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeView.fxml"));
             Parent homeView = loader.load();
             if (contentArea != null && contentArea.getScene() != null) {
                 contentArea.getScene().setRoot(homeView);
             }
        } catch (IOException e) {
             e.printStackTrace();
        }
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

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (Exception e) {
            // Log for debugging
            System.err.println("CRITICAL ERROR loading view: " + fxmlFile);
            e.printStackTrace();

            if (contentArea != null) {
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
