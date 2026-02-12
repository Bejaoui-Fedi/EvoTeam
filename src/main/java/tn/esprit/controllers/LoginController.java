package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox rememberCheckBox;
    @FXML
    private Label messageLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private VBox headerContainer;

    private UserService userService;
    private String espace = "Connexion";
    private String roleRequis = null; // "USER" ou "ADMIN"

    @FXML
    public void initialize() {
        userService = new UserService();
    }

    public void setEspace(String espace) {
        this.espace = espace;
        if (titleLabel != null) {
            titleLabel.setText(espace);
            subtitleLabel.setText("Connectez-vous à " + espace);
        }
    }

    public void setRoleSuggere(String role) {
        this.roleRequis = role;
        if ("ADMIN".equals(role)) {
            emailField.setPromptText("admin@psycoach.com");
        } else {
            emailField.setPromptText("patient@email.com");
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("❌ Email et mot de passe obligatoires", "error");
            return;
        }

        try {
            User user = userService.getByEmail(email);

            if (user == null) {
                showMessage("❌ Aucun compte trouvé avec cet email", "error");
                return;
            }

            if (!user.getPassword().equals(password)) {
                showMessage("❌ Mot de passe incorrect", "error");
                return;
            }

            if (!user.isActif()) {
                showMessage("❌ Compte désactivé. Contactez l'administrateur.", "error");
                return;
            }

            // ============ VALIDATION DES RÔLES ============

            // Espace UTILISATEUR : PATIENT et PSY_COACH seulement
            if ("USER".equals(roleRequis)) {
                if ("ADMIN".equals(user.getRole())) {
                    showMessage("❌ Accès refusé ! Veuillez utiliser l'espace administrateur.", "error");
                    return;
                }
                // ✅ PATIENT et PSY_COACH acceptés
            }

            // Espace ADMIN : ADMIN seulement
            else if ("ADMIN".equals(roleRequis)) {
                if (!"ADMIN".equals(user.getRole())) {
                    showMessage("❌ Accès refusé ! Cet espace est réservé aux administrateurs.", "error");
                    return;
                }
            }

            // Connexion réussie
            showMessage("✅ Connexion réussie ! Redirection...", "success");
            redirectBasedOnRole(user);

        } catch (SQLException e) {
            showMessage("❌ Erreur de connexion à la base de données", "error");
            e.printStackTrace();
        }
    }

    private void redirectBasedOnRole(User user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root;
            String title;

            if ("ADMIN".equals(user.getRole())) {
                // ✅ ADMIN → UserListView (Gestion des utilisateurs)
                loader = new FXMLLoader(getClass().getResource("/UserListView.fxml"));
                root = loader.load();
                title = "Gestion des Utilisateurs - Administration";
            } else {
                // ✅ PATIENT ou PSY_COACH → Profil utilisateur
                loader = new FXMLLoader(getClass().getResource("/UserProfileView.fxml"));
                root = loader.load();

                // Passer l'utilisateur au contrôleur du profil
                UserProfileController profileController = loader.getController();
                profileController.setUser(user);

                title = "Profil de " + user.getNom();
            }

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 700)); // Taille pour le profil
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            showMessage("❌ Erreur de redirection: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterLink() {
        try {
            FXMLLoader loader;
            Parent root;
            Stage stage = (Stage) emailField.getScene().getWindow();

            // ✅ CHOIX DU BON FORMULAIRE SELON L'ESPACE
            if ("ADMIN".equals(roleRequis)) {
                // ========== ESPACE ADMIN ==========
                loader = new FXMLLoader(getClass().getResource("/AdminFormView.fxml"));
                root = loader.load();

                stage.setScene(new Scene(root, 600, 650));
                stage.setTitle("Inscription Administrateur - " + espace);

            } else {
                // ========== ESPACE UTILISATEUR ==========
                loader = new FXMLLoader(getClass().getResource("/UserFormView.fxml"));
                root = loader.load();

                UserFormController userFormController = loader.getController();

                if (roleRequis != null) {
                    userFormController.setRoleSuggere(roleRequis);
                }
                userFormController.setEspace(espace);

                stage.setScene(new Scene(root, 600, 750));
                stage.setTitle("Création de compte - " + espace);
            }

            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            showMessage("❌ Impossible d'ouvrir le formulaire d'inscription", "error");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mot de passe oublié");
        alert.setHeaderText("Réinitialisation du mot de passe");

        if ("ADMIN".equals(roleRequis)) {
            alert.setContentText("Veuillez contacter le super administrateur.");
        } else {
            alert.setContentText("Veuillez contacter l'administrateur à admin@psycoach.com");
        }

        alert.showAndWait();
    }

    @FXML
    private void handleBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 600));
            stage.setTitle("PsyCoach Pro - Accueil");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            showMessage("❌ Impossible de retourner à l'accueil", "error");
            e.printStackTrace();
        }
    }

    private void showMessage(String text, String type) {
        messageLabel.setText(text);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);

        switch (type) {
            case "success":
                messageLabel.setStyle("-fx-background-color: #D4EDDA; -fx-text-fill: #155724; -fx-border-color: #C3E6CB; -fx-border-radius: 5; -fx-background-radius: 5;");
                break;
            case "error":
                messageLabel.setStyle("-fx-background-color: #F8D7DA; -fx-text-fill: #721C24; -fx-border-color: #F5C6CB; -fx-border-radius: 5; -fx-background-radius: 5;");
                break;
            default:
                messageLabel.setStyle("-fx-background-color: #FFF3CD; -fx-text-fill: #856404; -fx-border-color: #FFEEBA; -fx-border-radius: 5; -fx-background-radius: 5;");
                break;
        }
    }
}