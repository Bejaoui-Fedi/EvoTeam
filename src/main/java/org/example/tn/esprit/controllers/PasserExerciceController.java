package org.example.tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.tn.esprit.entities.Exercise;
import org.example.tn.esprit.entities.Objective;

import java.awt.Desktop;
import java.net.URI;

public class PasserExerciceController {

    @FXML private Label lblTitle;
    @FXML private Label lblType;
    @FXML private Label lblDifficulty;
    @FXML private Label lblDuration;
    @FXML private TextArea taDescription;
    @FXML private Hyperlink linkMedia;
    @FXML private Button btnBack;
    @FXML private Button btnFinish;

    private Exercise currentExercise;
    private Objective currentObjective; // ✅ pour revenir au bon objectif

    @FXML
    public void initialize() {

        btnBack.setOnAction(e -> backToExercises());

        btnFinish.setOnAction(e -> {
            // Ici tu peux aussi enregistrer un historique si tu veux (plus tard)
            new Alert(Alert.AlertType.INFORMATION, "Exercice terminé ✅").showAndWait();
            backToExercises();
        });

        linkMedia.setOnAction(e -> openMedia());
    }

    /**
     * ✅ IMPORTANT : on reçoit aussi l'objectif pour revenir à la liste filtrée
     */
    public void initData(Exercise ex, Objective obj) {
        this.currentExercise = ex;
        this.currentObjective = obj;

        lblTitle.setText(safe(ex.getTitle()));
        lblType.setText("Type : " + safe(ex.getType()));
        lblDifficulty.setText("Difficulté : " + safe(ex.getDifficulty()));
        lblDuration.setText("Durée : " + ex.getDurationMinutes() + " min");
        taDescription.setText(safe(ex.getDescription()));

        String url = safe(ex.getMediaUrl());
        linkMedia.setDisable(url.isBlank());
        linkMedia.setText(url.isBlank() ? "Aucun média" : "Ouvrir le média");
    }

    /**
     * ✅ Retour vers l'écran ExercicesParObjectif avec l'objectif réinjecté
     */
    private void backToExercises() {
        TemplateController.navigate("/AffichageExercicesParObjectif.fxml", c -> {
            if (c instanceof AffichageExercicesParObjectifController a) {
                if (currentObjective != null) {
                    a.initObjective(currentObjective); // ✅ recharge la liste filtrée
                }
            }
        });
    }

    private void openMedia() {
        try {
            if (currentExercise == null) return;

            String url = safe(currentExercise.getMediaUrl());
            if (url.isBlank()) return;

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                new Alert(Alert.AlertType.WARNING, "Desktop non supporté. URL: " + url).showAndWait();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir le média: " + e.getMessage()).showAndWait();
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}