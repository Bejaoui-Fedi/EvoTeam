package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Objective;
import tn.esprit.utils.Session;

import java.time.format.DateTimeFormatter;

public class DetailObjectifController {

    @FXML
    private Label lblTitle;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblLevel;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblUser;
    @FXML
    private Label lblDates;

    @FXML
    private Button btnBack;

    // Optional: if we want to add edit/delete here too
    @FXML
    private VBox boxAdminActions;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    private Objective currentObjective;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        btnBack.setOnAction(e -> TemplateController.navigate("/AffichageObjectifs.fxml", null));

        // Setup admin actions if needed
        if (boxAdminActions != null) {
            boolean isAdmin = Session.isAdmin();
            boxAdminActions.setVisible(isAdmin);
            boxAdminActions.setManaged(isAdmin);

            if (btnEdit != null) {
                btnEdit.setOnAction(e -> TemplateController.navigate("/ModificationObjectif.fxml",
                        c -> ((ModificationObjectifController) c).initData(currentObjective)));
            }
            if (btnDelete != null) {
                // Potentially implement delete or navigate to delete confirmation
                // For now, simple navigation if we had a dedicated delete page that takes an
                // ID, or just reuse the existing one logic
                // But existing SuppressionObjectifController might need initData too.
                // Simple route: just redirect to main list or implement simple delete here.
                // For now, let's just stick to "View Details".
            }
        }
    }

    public void initData(Objective obj) {
        this.currentObjective = obj;
        if (obj == null)
            return;

        lblTitle.setText(obj.getTitle());
        lblDescription.setText(obj.getDescription());
        lblLevel.setText("Niveau: " + obj.getLevel());

        String status = (obj.getPublished() == 1) ? "Publié" : "Brouillon";
        lblStatus.setText("Statut: " + status);

        lblUser.setText("Créé par utilisateur ID: " + obj.getUserId());

        String created = (obj.getCreatedAt() != null) ? dtf.format(obj.getCreatedAt()) : "-";
        String updated = (obj.getUpdatedAt() != null) ? dtf.format(obj.getUpdatedAt()) : "-";
        lblDates.setText("Créé le: " + created + " | Modifié le: " + updated);

    }
}
