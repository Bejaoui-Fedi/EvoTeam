package tn.esprit.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.entities.Objective;
import tn.esprit.services.ServiceObjective;
import tn.esprit.utils.Session;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public class AjoutObjectifController {

    @FXML
    private TextField tfUserId;
    @FXML
    private TextField tfTitle;
    @FXML
    private TextArea taDescription;
    @FXML
    private ChoiceBox<String> cbLevel;
    @FXML
    private CheckBox chkPublished;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    private final ServiceObjective service = new ServiceObjective();

    @FXML
    public void initialize() {
        // ADMIN uniquement
        if (!Session.isAdmin()) {
            showError("Accès refusé", "Seul un administrateur peut ajouter un objectif.");
            Platform.runLater(this::backToList);
            return;
        }

        // ✅ Remplir la ChoiceBox
        cbLevel.getItems().setAll(Arrays.asList("debutant", "moyen", "avance", "global"));
        cbLevel.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleCancel() {
        backToList();
    }

    @FXML
    private void handleSave() {
        onSave();
    }

    private void onSave() {
        // Validation userId
        Integer userId = parseIntOrNull(tfUserId.getText());
        if (userId == null || userId <= 0) {
            showError("Validation", "User ID invalide (entier > 0).");
            return;
        }

        // Validation title
        String title = safe(tfTitle.getText());
        if (title.isBlank()) {
            showError("Validation", "Le titre est requis.");
            return;
        }

        // ✅ Récupérer level depuis ChoiceBox
        String level = cbLevel.getValue();
        if (level == null || level.isBlank()) {
            showError("Validation", "Le niveau est requis.");
            return;
        }

        if (!confirm("Confirmer l'ajout", "Voulez-vous vraiment ajouter cet objectif ?"))
            return;

        Objective obj = new Objective();
        obj.setUserId(userId);
        obj.setTitle(title);
        obj.setDescription(safe(taDescription.getText()));
        obj.setLevel(level); // ✅ correct
        obj.setPublished(chkPublished.isSelected() ? 1 : 0);

        try {
            service.insert(obj);
            showInfo("Succès", "Objectif ajouté avec succès.");
            backToList();
        } catch (SQLException ex) {
            showError("Erreur d'ajout", ex.getMessage());
        }
    }

    private void backToList() {
        TemplateController.navigate("/AffichageObjectifs.fxml", null);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private Integer parseIntOrNull(String s) {
        try {
            if (s == null)
                return null;
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
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