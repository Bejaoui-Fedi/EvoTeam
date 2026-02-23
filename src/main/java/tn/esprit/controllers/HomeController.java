package tn.esprit.controllers;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class HomeController {

    @FXML
    private Button userButton;

    @FXML
    private Button adminButton;

    @FXML
    private VBox adminCard;

    @FXML
    private VBox userCard;

    @FXML
    private void handleUserAccess() {
        redirectToLogin("Espace Utilisateur", "USER");
    }

    @FXML
    private void handleAdminAccess() {
        redirectToLogin("Espace Administrateur", "ADMIN");
    }

    @FXML
    private void onAdminCardEntered() {
        animateCard(adminCard, true);
    }

    @FXML
    private void onAdminCardExited() {
        animateCard(adminCard, false);
    }

    @FXML
    private void onUserCardEntered() {
        animateCard(userCard, true);
    }

    @FXML
    private void onUserCardExited() {
        animateCard(userCard, false);
    }

    private void animateCard(VBox card, boolean enter) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
        TranslateTransition translate = new TranslateTransition(Duration.millis(200), card);

        if (enter) {
            scale.setToX(1.03);
            scale.setToY(1.03);
            translate.setToY(-5);
        } else {
            scale.setToX(1.0);
            scale.setToY(1.0);
            translate.setToY(0);
        }

        scale.play();
        translate.play();
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
