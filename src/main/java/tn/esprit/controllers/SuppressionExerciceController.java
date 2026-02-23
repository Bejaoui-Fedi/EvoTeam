package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.entities.Exercise;
import tn.esprit.services.ServiceExercise;
import tn.esprit.utils.Session;

import java.sql.SQLException;
import java.util.Optional;

public class SuppressionExerciceController {

    @FXML
    private Label lblTitle;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblType;
    @FXML
    private Label lblDifficulty;
    @FXML
    private Label lblDuration;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnCancel;

    private final ServiceExercise service = new ServiceExercise();
    private Exercise current;

    @FXML
    public void initialize() {
        if (!Session.isAdmin()) {
            showError("Accès refusé", "Seul un administrateur peut supprimer un exercice.");
            backToList();
            return;
        }
    }

    @FXML
    private void handleCancel() {
        backToList();
    }

    @FXML
    private void handleDelete() {
        onDelete();
    }

    public void initData(Exercise ex) {
        this.current = ex;
        if (lblTitle != null) {
            lblTitle.setText(ex.getTitle());
        }
        if (lblDescription != null) {
            lblDescription.setText(ex.getDescription() != null ? ex.getDescription() : "Aucune description");
        }
        if (lblType != null) {
            lblType.setText("Type: " + (ex.getType() != null ? ex.getType() : "N/A"));
        }
        if (lblDifficulty != null) {
            lblDifficulty.setText("Difficulté: " + (ex.getDifficulty() != null ? ex.getDifficulty() : "N/A"));
        }
        if (lblDuration != null) {
            lblDuration.setText("Durée: " + ex.getDurationMinutes() + " min");
        }
    }

    private void onDelete() {
        if (current == null || current.getIdExercise() == null) {
            showError("Erreur", "Aucun exercice chargé.");
            return;
        }

        if (!confirm("Confirmer la suppression",
                "Cette action est irréversible. Supprimer \"" + current.getTitle() + "\" ?"))
            return;

        try {
            int id = current.getIdExercise().intValue();
            service.delete(id);
            showInfo("Supprimé", "Exercice supprimé avec succès.");
            backToList();
        } catch (SQLException ex) {
            showError("Erreur de suppression", ex.getMessage());
        }
    }

    private void backToList() {
        TemplateController.navigate("/AffichageExercicesParObjectif.fxml", null);
    }

    private boolean confirm(String header, String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation");
        a.setHeaderText(header);
        a.setContentText(msg);
        Optional<ButtonType> res = a.showAndWait();
        return res.isPresent() && res.get() == ButtonType.OK;
    }

    private void showError(String header, String msg) {
        alert(Alert.AlertType.ERROR, header, msg);
    }

    private void showInfo(String header, String msg) {
        alert(Alert.AlertType.INFORMATION, header, msg);
    }

    private void alert(Alert.AlertType type, String header, String msg) {
        Alert a = new Alert(type);
        a.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : "Information");
        a.setHeaderText(header);
        a.setContentText(msg);
        a.showAndWait();
    }
}