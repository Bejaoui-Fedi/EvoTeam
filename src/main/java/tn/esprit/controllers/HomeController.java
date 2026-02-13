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
        // ✅ REDIRECTION VERS LOGIN USER (BLEU)
        redirectToLogin("/LoginUserView.fxml", "Espace Utilisateur", 900, 600);
    }

    @FXML
    private void handleAdminAccess() {
        // ✅ REDIRECTION VERS LOGIN ADMIN (VERT)
        redirectToLogin("/LoginAdminView.fxml", "Espace Administrateur", 900, 600);
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

    /**
     * Méthode générique pour rediriger vers une page de login spécifique
     * @param fxmlPath Chemin du fichier FXML
     * @param title Titre de la fenêtre
     * @param width Largeur de la scène
     * @param height Hauteur de la scène
     */
    private void redirectToLogin(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) userButton.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
            stage.setTitle("Evolia - " + title);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la page de connexion",
                    "Fichier introuvable: " + fxmlPath + "\n" + e.getMessage());
            e.printStackTrace();
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