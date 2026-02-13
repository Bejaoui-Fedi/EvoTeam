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

        // créer review
        Review review = new Review(
                r,
                comment.getText(),
                reviewDate.getValue().toString(),
                title.getText(),
                cbEventId.getValue()
        );

        // insert DB
        serviceReview.ajouter(review);

        Alert ok = new Alert(Alert.AlertType.INFORMATION,
                "Review ajoutée ✅. Voulez-vous afficher la liste ?",
                ButtonType.YES, ButtonType.NO);
        ok.setHeaderText(null);
        ok.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) goToDisplayReviews(null);
            else resetForm(null);
        });
    }

    @FXML
    private void resetForm(ActionEvent event) {
        cbEventId.getSelectionModel().clearSelection();
        title.clear();
        rating.clear();
        comment.clear();
        reviewDate.setValue(LocalDate.now());
    }

    // bouton "Afficher les reviews"
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