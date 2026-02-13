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

public class UserFormController {

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label messageLabel;
    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ProgressBar passwordStrengthBar;
    @FXML private Label passwordStrengthLabel;

    @FXML private RadioButton patientRadio;
    @FXML private RadioButton coachRadio;
    @FXML private RadioButton psychologueRadio;
    @FXML private ToggleGroup roleGroup;

    @FXML private CheckBox termsCheckBox;
    @FXML private Button saveButton;

    private UserService userService;
    private tn.esprit.controllers.UserListController userListController;
    private User currentUser;
    private boolean isAdminMode = false;

    @FXML
    public void initialize() {
        try {
            userService = new UserService();
        } catch (Exception e) {
            System.err.println("❌ Erreur critique : Impossible d'initialiser UserService dans UserFormController");
            e.printStackTrace();
            if (messageLabel != null) {
                showMessage("❌ Erreur système : Base de données inaccessible", "error");
            }
        }

        if (patientRadio != null) {
            patientRadio.setSelected(true);
        }

        if (passwordField != null) {
            passwordField.textProperty().addListener((obs, old, newVal) -> {
                checkPasswordStrength(newVal);
            });
        }

        if (confirmPasswordField != null) {
            confirmPasswordField.textProperty().addListener((obs, old, newVal) -> {
                validatePasswordMatch();
            });
        }
    }

    // ============ MÉTHODES POUR LA GESTION ADMIN ============

    public void setUserListController(tn.esprit.controllers.UserListController controller) {
        this.userListController = controller;
        this.isAdminMode = true;
        configureForAdminMode();
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            populateFieldsForEdit(user);
        }
    }

    private void configureForAdminMode() {
        if (titleLabel != null) {
            titleLabel.setText(currentUser == null ? "Ajouter un utilisateur" : "Modifier un utilisateur");
        }
        if (subtitleLabel != null) {
            subtitleLabel.setText("Gestion des utilisateurs - Administrateur");
        }
        if (termsCheckBox != null) {
            termsCheckBox.setVisible(false);
            termsCheckBox.setManaged(false);
        }
        if (saveButton != null) {
            saveButton.setText(currentUser == null ? "Créer l'utilisateur" : "Mettre à jour");
        }
    }

    private void populateFieldsForEdit(User user) {
        if (nomField != null) nomField.setText(user.getNom());
        if (emailField != null) {
            emailField.setText(user.getEmail());
            emailField.setDisable(true);
        }
        if (telephoneField != null) telephoneField.setText(user.getTelephone());
        if (passwordField != null) passwordField.clear();
        if (confirmPasswordField != null) confirmPasswordField.clear();

        if (patientRadio != null && coachRadio != null && psychologueRadio != null) {
            String role = user.getRole();
            if ("PATIENT".equals(role)) {
                patientRadio.setSelected(true);
            } else if ("PSY_COACH".equals(role)) {
                coachRadio.setSelected(true);
            }
        }
    }

    // ============ MÉTHODES POUR LOGINCONTROLLER ============

    public void setEspace(String espace) {
        if (titleLabel != null) {
            titleLabel.setText(espace);
        }
        if (subtitleLabel != null) {
            subtitleLabel.setText("Rejoignez " + espace);
        }
    }

    public void setRoleSuggere(String role) {
        if (patientRadio != null && coachRadio != null && psychologueRadio != null) {
            if ("PATIENT".equals(role)) {
                patientRadio.setSelected(true);
            } else if ("PSY_COACH".equals(role)) {
                coachRadio.setSelected(true);
            }
        }
    }

    // ============ VALIDATION ============

    private void checkPasswordStrength(String password) {
        if (passwordStrengthBar == null || passwordStrengthLabel == null) return;

        if (password.isEmpty()) {
            passwordStrengthBar.setProgress(0);
            passwordStrengthLabel.setText("Faible");
            passwordStrengthLabel.setStyle("-fx-text-fill: #E74C3C;");
            return;
        }

        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*\\d.*")) strength++;
        if (password.matches(".*[!@#$%^&*].*")) strength++;

        double progress = strength / 5.0;
        passwordStrengthBar.setProgress(progress);

        if (strength <= 2) {
            passwordStrengthLabel.setText("Faible");
            passwordStrengthLabel.setStyle("-fx-text-fill: #E74C3C;");
        } else if (strength <= 4) {
            passwordStrengthLabel.setText("Moyen");
            passwordStrengthLabel.setStyle("-fx-text-fill: #F39C12;");
        } else {
            passwordStrengthLabel.setText("Fort");
            passwordStrengthLabel.setStyle("-fx-text-fill: #27AE60;");
        }
    }

    private void validatePasswordMatch() {
        if (passwordField == null || confirmPasswordField == null) return;

        String pwd = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (!confirm.isEmpty() && !pwd.equals(confirm)) {
            confirmPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            showMessage("⚠️ Les mots de passe ne correspondent pas", "error");
        } else {
            confirmPasswordField.setStyle("");
            hideMessage();
        }
    }

    private boolean validateInput() {
        if (nomField == null || emailField == null || passwordField == null || confirmPasswordField == null) {
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

        if (currentUser == null || !passwordField.getText().isEmpty()) {
            if (passwordField.getText().isEmpty()) {
                showMessage("❌ Le mot de passe est obligatoire", "error");
                passwordField.requestFocus();
                return false;
            }

            if (passwordField.getText().length() < 6) {
                showMessage("❌ Le mot de passe doit contenir au moins 6 caractères", "error");
                passwordField.requestFocus();
                return false;
            }

            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                showMessage("❌ Les mots de passe ne correspondent pas", "error");
                confirmPasswordField.requestFocus();
                return false;
            }
        }

        return true;
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        if (!isAdminMode && termsCheckBox != null && !termsCheckBox.isSelected()) {
            showMessage("⚠️ Vous devez accepter les conditions d'utilisation", "error");
            return;
        }

        try {
            if (currentUser == null) {
                createNewUser();
            } else {
                updateExistingUser();
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                showMessage("❌ Cet email est déjà utilisé", "error");
            } else {
                showMessage("❌ Erreur : " + e.getMessage(), "error");
            }
        }
    }

    private void createNewUser() throws SQLException {
        User newUser = new User();
        newUser.setNom(nomField.getText().trim());
        newUser.setEmail(emailField.getText().trim());
        newUser.setPassword(passwordField.getText().trim());
        newUser.setTelephone(telephoneField != null ? telephoneField.getText().trim() : "");

        if (patientRadio != null && patientRadio.isSelected()) {
            newUser.setRole("PATIENT");
        } else if (coachRadio != null && coachRadio.isSelected()) {
            newUser.setRole("PSY_COACH");
        } else if (psychologueRadio != null && psychologueRadio.isSelected()) {
            newUser.setRole("PSY_COACH");
        } else {
            newUser.setRole("PATIENT");
        }

        newUser.setActif(true);

        // ✅ Récupérer l'ID généré
        int generatedId = userService.add(newUser);
        newUser.setId(generatedId);

        if (isAdminMode) {
            showSuccessMessage("✅ Utilisateur créé avec succès !");
            if (userListController != null) {
                userListController.refreshTable();
            }
            closeWindow();
        } else {
            showSuccessMessage("✅ Compte créé avec succès !");
            redirectToDashboard(newUser); // ✅ Redirection vers le Dashboard
        }
    }

    private void updateExistingUser() throws SQLException {
        currentUser.setNom(nomField.getText().trim());
        if (telephoneField != null) {
            currentUser.setTelephone(telephoneField.getText().trim());
        }

        if (passwordField != null && !passwordField.getText().isEmpty()) {
            currentUser.setPassword(passwordField.getText().trim());
            userService.updateWithPassword(currentUser);
        } else {
            userService.update(currentUser);
        }

        if (patientRadio != null && patientRadio.isSelected()) {
            currentUser.setRole("PATIENT");
        } else if (coachRadio != null && coachRadio.isSelected()) {
            currentUser.setRole("PSY_COACH");
        } else if (psychologueRadio != null && psychologueRadio.isSelected()) {
            currentUser.setRole("PSY_COACH");
        }

        showSuccessMessage("✅ Utilisateur modifié avec succès !");
        if (userListController != null) {
            userListController.refreshTable();
        }
        closeWindow();
    }

    // ✅ Redirection vers le Dashboard
    private void redirectToDashboard(User user) {
        try {
            System.out.println("🚀 Redirection vers le Dashboard pour: " + user.getEmail());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDashboard.fxml"));
            if (loader.getLocation() == null) {
                showMessage("❌ Erreur: Fichier UserDashboard.fxml introuvable", "error");
                return;
            }

            Parent root = loader.load();
            
            // ✅ Utiliser le bon contrôleur
            UserDashboardController controller = loader.getController();
            if (controller != null) {
                controller.setCurrentUser(user);
                System.out.println("✅ User passé au UserDashboardController");
            } else {
                System.err.println("⚠️ Le contrôleur du Dashboard est null !");
            }

            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 800)); // Taille adaptée au Dashboard
            stage.setTitle("Mon Espace - " + user.getNom());
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showMessage("❌ Impossible d'ouvrir le tableau de bord: " + e.getMessage(), "error");
        }
    }

    private void closeWindow() {
        if (nomField != null) {
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleLoginLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();

            if (nomField != null) {
                Stage stage = (Stage) nomField.getScene().getWindow();
                stage.setScene(new Scene(root, 450, 550));
                stage.setTitle("Connexion");
                stage.centerOnScreen();
                stage.show();
            }

        } catch (IOException e) {
            showMessage("❌ Impossible d'ouvrir la page de connexion", "error");
        }
    }

    @FXML
    private void handleTermsLink() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Conditions d'utilisation");
        alert.setHeaderText("Conditions d'utilisation - PsyCoach Pro");
        alert.setContentText("En utilisant PsyCoach Pro, vous acceptez de respecter nos conditions d'utilisation...");
        alert.showAndWait();
    }

    @FXML
    private void handlePrivacyLink() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Politique de confidentialité");
        alert.setHeaderText("Politique de confidentialité");
        alert.setContentText("Nous protégeons vos données personnelles conformément au RGPD...");
        alert.showAndWait();
    }

    private void showMessage(String text, String type) {
        if (messageLabel == null) return;
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

    private void hideMessage() {
        if (messageLabel != null) {
            messageLabel.setVisible(false);
            messageLabel.setManaged(false);
        }
    }

    private void showSuccessMessage(String text) {
        showMessage(text, "success");
        if (saveButton != null) {
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
}