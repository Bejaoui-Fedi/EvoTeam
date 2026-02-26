package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.entities.Objective;
import tn.esprit.services.ServiceObjective;
import tn.esprit.utils.Session;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public class ModificationObjectifController {

    @FXML
    private TextField tfId;
    @FXML
    private TextField tfUserId;
    @FXML
    private TextField tfTitle;
    @FXML
    private TextArea taDescription;

    @FXML
    private ChoiceBox<String> cbLevel; // ✅ changé

    @FXML
    private CheckBox chkPublished;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    private final ServiceObjective service = new ServiceObjective();
    private Objective current;

    @FXML
    public void initialize() {
        if (!Session.isAdmin()) {
            showError("Accès refusé", "Seul un administrateur peut modifier un objectif.");
            backToList();
            return;
        }

        cbLevel.getItems().setAll(Arrays.asList("debutant", "moyen", "avance", "global"));
    }

    @FXML
    private void handleCancel() {
        backToList();
    }

    @FXML
    private void handleSave() {
        onSave();
    }

    /** Appelée depuis AffichageObjectifsController via navigate(...) */
    public void initData(Objective obj) {
        if (obj == null) {
            showError("Erreur", "Objectif est null");
            return;
        }
        this.current = obj;

        if (tfId != null)
            tfId.setText(obj.getIdObjective() == null ? "" : String.valueOf(obj.getIdObjective()));
        if (tfUserId != null)
            tfUserId.setText(String.valueOf(obj.getUserId()));
        if (tfTitle != null)
            tfTitle.setText(obj.getTitle());
        if (taDescription != null)
            taDescription.setText(obj.getDescription());

        if (cbLevel != null) {
            String lvl = obj.getLevel();
            if (lvl != null) {
                if (cbLevel.getItems().contains(lvl)) {
                    cbLevel.setValue(lvl);
                } else {
                    // try case insensitive
                    for (String item : cbLevel.getItems()) {
                        if (item.equalsIgnoreCase(lvl)) {
                            cbLevel.setValue(item);
                            break;
                        }
                    }
                }
            }
        }
        if (chkPublished != null)
            chkPublished.setSelected(obj.getPublished() == 1);
    }

    private void onSave() {
        if (current == null || current.getIdObjective() == null) {
            showError("Erreur", "Aucun objectif chargé.");
            return;
        }

        Integer userId = parseIntOrNull(tfUserId.getText());
        if (userId == null || userId <= 0) {
            showError("Validation", "User ID invalide (entier > 0).");
            return;
        }

        String title = safe(tfTitle.getText());
        if (title.isBlank()) {
            showError("Validation", "Le titre est requis.");
            return;
        }

        String level = cbLevel.getValue(); // ✅ level
        if (level == null || level.isBlank()) {
            showError("Validation", "Le niveau est requis.");
            return;
        }

        if (!confirm("Confirmer la modification", "Voulez-vous enregistrer les modifications ?"))
            return;

        current.setUserId(userId);
        current.setTitle(title);
        current.setDescription(safe(taDescription.getText()));
        current.setLevel(level); // ✅ correct
        current.setPublished(chkPublished.isSelected() ? 1 : 0);

        try {
            service.update(current);
            showInfo("Succès", "Objectif modifié avec succès.");
            backToList();
        } catch (SQLException ex) {
            showError("Erreur de modification", ex.getMessage());
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