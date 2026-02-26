package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class LoginAdminController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox rememberCheckBox;
    @FXML
    private Label messageLabel;

    private UserService userService;

    @FXML
    public void initialize() {
        userService = new UserService();
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

            // Vérifier que c'est bien un admin
            if (!"ADMIN".equals(user.getRole())) {
                showMessage("❌ Accès refusé ! Cet espace est réservé aux administrateurs.", "error");
                return;
            }

            // Connexion réussie
            tn.esprit.utils.Session.currentUser = user;
            showMessage("✅ Connexion réussie ! Redirection...", "success");

            // Rediriger vers AdminDashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 800));
            stage.setTitle("Evolia - Administration");
            stage.centerOnScreen();
            stage.show();

        } catch (SQLException | IOException e) {
            showMessage("❌ Erreur: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminFormView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 650));
            stage.setTitle("Evolia - Inscription Administrateur");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            showMessage("❌ Erreur: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mot de passe oublié");
        alert.setHeaderText("Réinitialisation du mot de passe");
        alert.setContentText("Veuillez contacter le super administrateur.");
        alert.showAndWait();
    }

    @FXML
    private void handleBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 800));
            stage.setTitle("Evolia - Accueil");
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

        if ("success".equals(type)) {
            messageLabel.setStyle("-fx-background-color: #D4EDDA; -fx-text-fill: #155724; -fx-border-color: #C3E6CB; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10;");
        } else {
            messageLabel.setStyle("-fx-background-color: #F8D7DA; -fx-text-fill: #721C24; -fx-border-color: #F5C6CB; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10;");
        }
    }
}