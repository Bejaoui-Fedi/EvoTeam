package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import tn.esprit.entities.User;

import java.io.IOException;

public class UserProfileController {

    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label telephoneLabel;
    @FXML private Label roleLabel;
    @FXML private Label statusLabel;

    private User currentUser;

    @FXML
    public void initialize() {
        System.out.println("✅ UserProfileController initialisé");
    }

    // ✅ Méthode setUser - ESSENTIELLE
    public void setUser(User user) {
        System.out.println("✅ setUser appelé avec: " + (user != null ? user.getEmail() : "null"));
        this.currentUser = user;
        loadProfile(user);
    }

    // ✅ Charger le profil
    public void loadProfile(User user) {
        if (user == null) {
            System.out.println("❌ Erreur: user est null");
            return;
        }

        try {
            // Mise à jour des labels
            if (usernameLabel != null) {
                usernameLabel.setText(user.getNom());
                System.out.println("✅ Nom mis à jour: " + user.getNom());
            }

            if (emailLabel != null) {
                emailLabel.setText(user.getEmail());
                System.out.println("✅ Email mis à jour: " + user.getEmail());
            }

            if (telephoneLabel != null) {
                telephoneLabel.setText(user.getTelephone() != null ? user.getTelephone() : "Non renseigné");
            }

            // Affichage du rôle
            if (roleLabel != null) {
                String role = user.getRole();
                switch (role) {
                    case "ADMIN":
                        roleLabel.setText("Administrateur");
                        roleLabel.setStyle("-fx-text-fill: #8E44AD; -fx-font-weight: bold;");
                        break;
                    case "PSY_COACH":
                        roleLabel.setText("Psychologue/Coach");
                        roleLabel.setStyle("-fx-text-fill: #3498DB; -fx-font-weight: bold;");
                        break;
                    case "PATIENT":
                        roleLabel.setText("Patient");
                        roleLabel.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                        break;
                    default:
                        roleLabel.setText(role);
                        break;
                }
            }

            // Affichage du statut
            if (statusLabel != null) {
                if (user.isActif()) {
                    statusLabel.setText("Actif");
                    statusLabel.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                } else {
                    statusLabel.setText("Inactif");
                    statusLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                }
            }

            System.out.println("✅ Profil chargé avec succès pour: " + user.getEmail());

        } catch (Exception e) {
            System.out.println("❌ Erreur dans loadProfile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ Retour à l'accueil
    @FXML
    private void handleBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 600));
            stage.setTitle("PsyCoach Pro - Accueil");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à l'accueil");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}