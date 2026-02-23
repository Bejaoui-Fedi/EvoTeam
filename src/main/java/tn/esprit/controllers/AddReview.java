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
        // charger tous les eventId pour la jointure
        cbEventId.setItems(FXCollections.observableArrayList(serviceEvent.getAllEventIds()));

        // par défaut, aujourd’hui
        reviewDate.setValue(LocalDate.now());
    }

    @FXML
    private void handleAddReview(ActionEvent event) {

        // champs obligatoires
        if (cbEventId.getValue() == null || rating.getText().isEmpty() || reviewDate.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "EventId, Rating et Date sont obligatoires !").show();
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

        // créer review (avec le userId de l'utilisateur connecte)
        Review review = new Review(
                r,
                comment.getText(),
                reviewDate.getValue().toString(),
                title.getText(),
                cbEventId.getValue(),
                prefilledUserId
        );

        // insert DB
        serviceReview.ajouter(review);

        // ✅ MODIFICATION ICI : Message simple sans proposition d'afficher la liste
        Alert ok = new Alert(Alert.AlertType.INFORMATION, "Review ajoutée avec succès !", ButtonType.OK);
        ok.setHeaderText(null);
        ok.showAndWait();

        // ✅ Fermer la fenêtre actuelle
        Stage stage = (Stage) cbEventId.getScene().getWindow();
        stage.close();

        // La page UserDisplayReview se recharge automatiquement grâce à stage.setOnHidden dans UserDisplayReview
    }


    /**
     * Appele par UserDisplayReview pour pre-remplir le userId de l'utilisateur connecte.
     * La review sera associee a cet utilisateur.
     */
    public void setUserId(int userId) {
        this.prefilledUserId = userId;
    }

    @FXML
    private void resetForm(ActionEvent event) {
        cbEventId.getSelectionModel().clearSelection();
        title.clear();
        rating.clear();
        comment.clear();
        reviewDate.setValue(LocalDate.now());
    }


    /**
     * Appele par UserDisplayReview pour pre-remplir l'eventId.
     * Si tu as un ComboBox cbEventId, on le pre-selectionne et on le desactive
     * pour que l'user ne puisse pas changer l'event.
     */
    public void setEventId(int eventId) {
        this.prefilledEventId = eventId;
        // Si tu as un ComboBox pour eventId, pre-selectionne-le :
        if (cbEventId != null) {
            cbEventId.setValue(eventId);
            cbEventId.setDisable(true);  // Empeche l'utilisateur de changer l'event
        }
    }

    // bouton "Afficher les reviews" - GARDÉ AU CAS OÙ, MAIS PLUS UTILISÉ
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