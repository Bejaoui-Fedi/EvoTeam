package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class HomeController {

    @FXML
    private Button userButton;

    @FXML
    private Button adminButton;

    @FXML
    private void handleUserAccess() {
        redirectToLogin("Espace Utilisateur", "USER");
    }

    @FXML
    private void handleAdminAccess() {
        redirectToLogin("Espace Administrateur", "ADMIN");
    }

    private void redirectToLogin(String espace, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setEspace(espace);
            loginController.setRoleSuggere(role);

            Stage stage = (Stage) userButton.getScene().getWindow();
            stage.setScene(new Scene(root, 450, 500));
            stage.setTitle("Connexion - " + espace);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la page de connexion", e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}