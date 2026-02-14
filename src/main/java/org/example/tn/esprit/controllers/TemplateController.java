package org.example.tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public class TemplateController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        navigate("/AffichageObjectifs.fxml", null);
    }

    public static void navigate(String fxmlPath, Consumer<Object> controllerConsumer) {
        // Redirect to NewTemplateController for compatibility
        System.out.println("TemplateController.navigate() called - redirecting to NewTemplateController");
        NewTemplateController.navigate(fxmlPath, controllerConsumer);
    }

    @FXML
    private void goObjectives() {
        navigate("/AffichageObjectifs.fxml", null);
    }

    @FXML
    private void goExercises() {
        // Ton flow est Objectifs -> clic -> Exercices par objectif
        new Alert(Alert.AlertType.INFORMATION, "Sélectionne un objectif puis clique 'Voir Exercices'.").showAndWait();
        navigate("/AffichageObjectifs.fxml", null);
    }
}
