package org.example.tn.esprit.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.tn.esprit.entities.Exercise;
import org.example.tn.esprit.entities.Objective;
import org.example.tn.esprit.services.ServiceExercise;
import org.example.tn.esprit.services.ServiceObjective;
import org.example.tn.esprit.utils.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ModificationExerciceController {

    @FXML
    private ChoiceBox<String> cbObjective;
    @FXML
    private TextField tfTitle;
    @FXML
    private TextArea taDescription;
    @FXML
    private ChoiceBox<String> cbType;
    @FXML
    private ChoiceBox<String> cbDifficulty;
    @FXML
    private TextField tfDuration;
    @FXML
    private TextField tfMediaUrl;
    @FXML
    private TextArea taSteps;
    @FXML
    private CheckBox chkPublished;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    private final ServiceExercise service = new ServiceExercise();
    private final ServiceObjective objectiveService = new ServiceObjective();
    private Exercise current;

    @FXML
    public void initialize() {
        if (!Session.isAdmin()) {
            new Alert(Alert.AlertType.ERROR, "Accès refusé : Admin uniquement").showAndWait();
            Platform.runLater(() -> TemplateController.navigate("/AffichageExercicesParObjectif.fxml", null));
            return;
        }

        // Load objectives
        loadObjectives();

        // Populate type choices
        cbType.setItems(FXCollections.observableArrayList(
                "respiration", "journaling", "meditation", "cbt", "challenge", "relaxation"));

        // Populate difficulty choices
        cbDifficulty.setItems(FXCollections.observableArrayList(
                "debutant", "moyen", "avance"));
    }

    @FXML
    private void handleCancel() {
        TemplateController.navigate("/AffichageExercicesParObjectif.fxml", null);
    }

    @FXML
    private void handleSave() {
        onSave();
    }

    private void loadObjectives() {
        try {
            List<Objective> objectives = objectiveService.getAll();
            cbObjective.setItems(FXCollections.observableArrayList(
                    objectives.stream()
                            .map(obj -> obj.getIdObjective() + " - " + obj.getTitle())
                            .toList()));
        } catch (SQLException e) {
            error("Erreur de chargement des objectifs: " + e.getMessage());
        }
    }

    public void initData(Exercise ex) {
        if (ex == null) {
            new Alert(Alert.AlertType.ERROR, "Exercice invalide (null)").showAndWait();
            return;
        }
        this.current = ex;

        // Pre-fill fields
        if (tfTitle != null)
            tfTitle.setText(ex.getTitle());
        if (taDescription != null)
            taDescription.setText(ex.getDescription());
        if (tfDuration != null)
            tfDuration.setText(String.valueOf(ex.getDurationMinutes()));
        if (tfMediaUrl != null)
            tfMediaUrl.setText(ex.getMediaUrl());
        if (taSteps != null)
            taSteps.setText(ex.getSteps());
        if (chkPublished != null)
            chkPublished.setSelected(ex.isPublished());

        // Set objective
        if (cbObjective != null && ex.getObjectiveId() != null) {
            try {
                Objective obj = objectiveService.getById(ex.getObjectiveId());
                if (obj != null) {
                    String value = obj.getIdObjective() + " - " + obj.getTitle();
                    cbObjective.setValue(value);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Set type
        if (cbType != null && ex.getType() != null) {
            cbType.setValue(ex.getType());
        }

        // Set difficulty
        if (cbDifficulty != null && ex.getDifficulty() != null) {
            cbDifficulty.setValue(ex.getDifficulty());
        }
    }

    private void onSave() {
        if (current == null || current.getIdExercise() == null) {
            error("Aucun exercice chargé");
            return;
        }

        // Get objective ID from selection
        String selectedObj = cbObjective.getValue();
        if (selectedObj == null || selectedObj.isEmpty()) {
            error("Veuillez sélectionner un objectif");
            return;
        }

        Integer objectiveId = null;
        try {
            objectiveId = Integer.parseInt(selectedObj.split(" - ")[0]);
        } catch (Exception e) {
            error("Objective ID invalide");
            return;
        }

        String title = safe(tfTitle.getText());
        if (title.isBlank()) {
            error("Titre requis");
            return;
        }

        String type = cbType.getValue();
        if (type == null || type.isBlank()) {
            error("Type requis");
            return;
        }

        String difficulty = cbDifficulty.getValue();
        if (difficulty == null || difficulty.isBlank()) {
            error("Difficulté requise");
            return;
        }

        Integer duration = parseInt(tfDuration.getText());
        if (duration == null || duration < 0) {
            duration = 0;
        }

        if (!confirm("Confirmer", "Enregistrer les modifications ?"))
            return;

        current.setObjectiveId(objectiveId);
        current.setTitle(title);
        current.setDescription(safe(taDescription.getText()));
        current.setType(type);
        current.setDurationMinutes(duration);
        current.setDifficulty(difficulty);
        current.setMediaUrl(safe(tfMediaUrl.getText()));
        current.setSteps(safe(taSteps.getText()));
        current.setPublished(chkPublished.isSelected());

        try {
            service.update(current);
            info("Exercice modifié avec succès");
            TemplateController.navigate("/AffichageExercicesParObjectif.fxml", null);
        } catch (SQLException e) {
            e.printStackTrace();
            error("Erreur de modification: " + e.getMessage());
        }
    }

    private Integer parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean confirm(String header, String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation");
        a.setHeaderText(header);
        a.setContentText(msg);
        Optional<ButtonType> r = a.showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }

    private void error(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private void info(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}
