package org.example.tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.tn.esprit.entities.Objective;
import org.example.tn.esprit.services.ServiceObjective;
import org.example.tn.esprit.utils.Session;

import java.sql.SQLException;
import java.util.Optional;

public class SuppressionObjectifController {

    @FXML
    private Label lblTitle;
    @FXML
    private Label lblDescription;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnCancel;

    private final ServiceObjective service = new ServiceObjective();
    private Objective current;

    @FXML
    public void initialize() {
        if (!Session.isAdmin()) {
            showError("Accès refusé", "Seul un administrateur peut supprimer un objectif.");
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

    /** Appelée depuis AffichageObjectifsController via navigate(...) */
    public void initData(Objective obj) {
        this.current = obj;
        if (lblTitle != null) {
            lblTitle.setText(obj.getTitle());
        }
        if (lblDescription != null) {
            lblDescription.setText(obj.getDescription() != null ? obj.getDescription() : "Aucune description");
        }
    }

    private void onDelete() {
        if (current == null || current.getIdObjective() == null) {
            showError("Erreur", "Aucun objectif chargé.");
            return;
        }

        if (!confirm("Confirmer la suppression",
                "Cette action est irréversible. Supprimer \"" + current.getTitle() + "\" ?"))
            return;

        try {
            int id = current.getIdObjective().intValue();
            service.delete(id);
            showInfo("Supprimé", "Objectif supprimé avec succès.");
            backToList();
        } catch (SQLException ex) {
            showError("Erreur de suppression", ex.getMessage());
        }
    }

    private void backToList() {
        TemplateController.navigate("/AffichageObjectifs.fxml", null);
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