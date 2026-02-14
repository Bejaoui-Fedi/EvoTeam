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

public class AjoutExerciceController {

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
    private Objective selectedObjective;

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
        cbType.getSelectionModel().selectFirst();

        // Populate difficulty choices
        cbDifficulty.setItems(FXCollections.observableArrayList(
                "debutant", "moyen", "avance"));
        cbDifficulty.getSelectionModel().selectFirst();
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
            List<Objective> objectives = objectiveService.show();
            cbObjective.getItems().clear();
            for (Objective obj : objectives) {
                cbObjective.getItems().add(obj.getIdObjective() + " - " + obj.getTitle());
            }

            // If selectedObjective was set via initObjective, pre-fill it
            if (selectedObjective != null) {
                String value = selectedObjective.getIdObjective() + " - " + selectedObjective.getTitle();
                cbObjective.setValue(value);
            }
        } catch (SQLException e) {
            error("Erreur de chargement des objectifs: " + e.getMessage());
        }
    }

    private void onSave() {
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
            duration = 0; // Default to 0 if not specified
        }

        if (!confirm("Confirmer", "Ajouter cet exercice ?"))
            return;

        Exercise ex = new Exercise();
        ex.setUserId(Session.currentUser.getId());
        ex.setObjectiveId(objectiveId);
        ex.setTitle(title);
        ex.setDescription(safe(taDescription.getText()));
        ex.setType(type);
        ex.setDurationMinutes(duration);
        ex.setDifficulty(difficulty);
        ex.setMediaUrl(safe(tfMediaUrl.getText()));
        // ex.setSteps(safe(taSteps.getText())); // Uncomment if setSteps exists in
        // Exercise
        ex.setIsPublished(chkPublished.isSelected() ? 1 : 0);

        try {
            service.insert(ex);
            info("Exercice ajouté avec succès");
            TemplateController.navigate("/AffichageExercicesParObjectif.fxml", null);
        } catch (SQLException e) {
            e.printStackTrace();
            error("Erreur d'ajout: " + e.getMessage());
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

    public void initObjective(Objective obj) {
        this.selectedObjective = obj;
        if (obj != null && cbObjective != null) {
            String value = obj.getIdObjective() + " - " + obj.getTitle();
            cbObjective.setValue(value);
        }
    }
}