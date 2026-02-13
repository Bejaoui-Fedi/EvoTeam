package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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
import java.time.LocalDate;

public class AdminFormController {

    @FXML private Label messageLabel;
    @FXML private PasswordField adminCodeField;
    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private CheckBox termsCheckBox;
    @FXML private Button saveButton;

    private UserService userService;
    private static final String ADMIN_SECRET_CODE = "ADMIN2026";

    @FXML
    public void initialize() {
        try {
            userService = new UserService();
        } catch (Exception e) {
            System.err.println("❌ Erreur critique : Impossible d'initialiser UserService dans AdminFormController");
            e.printStackTrace();
            if (messageLabel != null) {
                showMessage("❌ Erreur système : Base de données inaccessible", "error");
            }
        }
    }

    private boolean validateInput() {
        // Vérifier le code secret
        if (!ADMIN_SECRET_CODE.equals(adminCodeField.getText())) {
            showMessage("❌ Code secret incorrect", "error");
            adminCodeField.requestFocus();
            return false;
        }

        if (nomField.getText().trim().isEmpty()) {
            showMessage("❌ Le nom est obligatoire", "error");
            nomField.requestFocus();
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showMessage("❌ L'email est obligatoire", "error");
            emailField.requestFocus();
            return false;
        }

        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showMessage("❌ Format d'email invalide", "error");
            emailField.requestFocus();
            return false;
        }

        if (passwordField.getText().isEmpty()) {
            showMessage("❌ Le mot de passe est obligatoire", "error");
            passwordField.requestFocus();
            return false;
        }

        if (passwordField.getText().length() < 8) {
            showMessage("❌ Le mot de passe doit contenir au moins 8 caractères", "error");
            passwordField.requestFocus();
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showMessage("❌ Les mots de passe ne correspondent pas", "error");
            confirmPasswordField.requestFocus();
            return false;
        }

        return true;
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        if (!termsCheckBox.isSelected()) {
            showMessage("⚠️ Vous devez accepter les conditions d'utilisation", "error");
            return;
        }

        try {
            User newAdmin = new User();
            newAdmin.setNom(nomField.getText().trim());
            newAdmin.setEmail(emailField.getText().trim());
            newAdmin.setPassword(passwordField.getText().trim());
            newAdmin.setTelephone(telephoneField.getText().trim());
            newAdmin.setRole("ADMIN");
            newAdmin.setActif(true);

            userService.add(newAdmin);

            showSuccessMessage("✅ Compte administrateur créé !");

            // ✅ REDIRIGER VERS LA PAGE DE CONNEXION ADMIN
            handleLoginLink();

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                showMessage("❌ Cet email est déjà utilisé", "error");
            } else {
                showMessage("❌ Erreur : " + e.getMessage(), "error");
            }
        }
    }

    @FXML
    private void handleLoginLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();

            // ✅ PRÉPARER LA PAGE DE CONNEXION POUR ADMIN
            LoginController loginController = loader.getController();
            loginController.setEspace("Espace Administrateur");
            loginController.setRoleSuggere("ADMIN");

            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root, 450, 550));
            stage.setTitle("Connexion Administrateur");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            showMessage("❌ Impossible d'ouvrir la page de connexion", "error");
        }
    }

    @FXML
    private void handleTermsLink() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Conditions d'utilisation");
        alert.setHeaderText("Conditions d'utilisation - Administrateur");
        alert.setContentText("En tant qu'administrateur, vous êtes responsable de la plateforme...");
        alert.showAndWait();
    }

    @FXML
    private void handlePrivacyLink() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Politique de confidentialité");
        alert.setHeaderText("Politique de confidentialité");
        alert.setContentText("Les administrateurs ont accès aux données utilisateurs...");
        alert.showAndWait();
    }

    private void showMessage(String text, String type) {
        if (messageLabel == null) return;
        messageLabel.setText(text);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);

        switch (type) {
            case "success":
                messageLabel.setStyle("-fx-background-color: #D4EDDA; -fx-text-fill: #155724; -fx-border-color: #C3E6CB;");
                break;
            case "error":
                messageLabel.setStyle("-fx-background-color: #F8D7DA; -fx-text-fill: #721C24; -fx-border-color: #F5C6CB;");
                break;
            default:
                messageLabel.setStyle("-fx-background-color: #FFF3CD; -fx-text-fill: #856404; -fx-border-color: #FFEEBA;");
                break;
        }
    }

    private void showSuccessMessage(String text) {
        showMessage(text, "success");
        String originalText = saveButton.getText();
        saveButton.setText("✅ Succès !");

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                javafx.application.Platform.runLater(() -> {
                    saveButton.setText(originalText);
                });
            } catch (InterruptedException e) {}
        }).start();
    }


}