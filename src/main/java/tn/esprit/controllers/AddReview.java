package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Review;
import tn.esprit.services.ServiceEvent;
import tn.esprit.services.ServiceReview;

import java.io.IOException;
import java.time.LocalDate;

public class AddReview {

    @FXML private ComboBox<Integer> cbEventId;
    @FXML private TextField title;
    @FXML private TextField rating;
    @FXML private DatePicker reviewDate;
    @FXML private TextArea comment;

    private int prefilledUserId = 0;
    private int prefilledEventId = 0;
    private final ServiceReview serviceReview = new ServiceReview();
    private final ServiceEvent serviceEvent = new ServiceEvent();

    @FXML
    public void initialize() {
        // Charger tous les eventId pour la jointure
        cbEventId.setItems(FXCollections.observableArrayList(serviceEvent.getAllEventIds()));
        // Par défaut, aujourd'hui
        reviewDate.setValue(LocalDate.now());
    }

    // =====================================================
    // POUR L'UTILISATEUR (eventId pré-rempli et désactivé)
    // =====================================================
    public void setForUser(int userId, int eventId) {
        this.prefilledUserId = userId;
        this.prefilledEventId = eventId;

        if (cbEventId != null) {
            cbEventId.setValue(eventId);
            cbEventId.setDisable(true);  // Désactive la sélection
            cbEventId.setStyle("-fx-opacity: 0.8; -fx-background-color: #f0f0f0;");
        }
    }

    // =====================================================
    // POUR L'ADMIN (eventId modifiable)
    // =====================================================
    public void setForAdmin() {
        if (cbEventId != null) {
            cbEventId.setDisable(false);
            cbEventId.setStyle("");  // Style normal
        }
    }

    // =====================================================
    // POUR LA COMPATIBILITÉ (ancienne méthode)
    // =====================================================
    public void setUserId(int userId) {
        this.prefilledUserId = userId;
    }

    public void setEventId(int eventId) {
        this.prefilledEventId = eventId;
        if (cbEventId != null) {
            cbEventId.setValue(eventId);
            cbEventId.setDisable(true);
            cbEventId.setStyle("-fx-opacity: 0.8; -fx-background-color: #f0f0f0;");
        }
    }

    @FXML
    private void handleAddReview(ActionEvent event) {
        // Vérifier si l'eventId est sélectionné (soit par l'utilisateur, soit pré-rempli)
        Integer selectedEventId = cbEventId.getValue();
        if (selectedEventId == null) {
            new Alert(Alert.AlertType.ERROR, "Veuillez sélectionner un événement !").show();
            return;
        }

        // Champs obligatoires
        if (rating.getText().isEmpty() || reviewDate.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Rating et Date sont obligatoires !").show();
            return;
        }

        int r;
        try {
            r = Integer.parseInt(rating.getText());
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Rating doit être un nombre !").show();
            return;
        }

        if (r < 1 || r > 5) {
            new Alert(Alert.AlertType.ERROR, "Rating doit être entre 1 et 5.").show();
            return;
        }

        // Créer la review
        Review review = new Review(
                r,
                comment.getText(),
                reviewDate.getValue().toString(),
                title.getText(),
                selectedEventId,  // Utilise l'eventId sélectionné
                prefilledUserId
        );

        // Insertion en BDD
        serviceReview.ajouter(review);

        // Message de succès
        Alert ok = new Alert(Alert.AlertType.INFORMATION, "Review ajoutée avec succès !", ButtonType.OK);
        ok.setHeaderText(null);
        ok.showAndWait();

        // Fermer la fenêtre
        Stage stage = (Stage) cbEventId.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void resetForm(ActionEvent event) {
        cbEventId.getSelectionModel().clearSelection();
        title.clear();
        rating.clear();
        comment.clear();
        reviewDate.setValue(LocalDate.now());
    }

    @FXML
    private void goToDisplayReviews(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/DisplayReview.fxml"));
            Stage stage = (Stage) cbEventId.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Reviews - CardView");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir DisplayReview.fxml").show();
        }
    }
}