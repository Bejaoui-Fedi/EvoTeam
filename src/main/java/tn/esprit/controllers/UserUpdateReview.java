package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Review;
import tn.esprit.services.ServiceReview;

import java.time.LocalDate;

public class UserUpdateReview {

    @FXML private Label lblId;
    @FXML private ComboBox<Integer> cbEventId;
    @FXML private TextField title;
    @FXML private TextField rating;
    @FXML private DatePicker reviewDate;
    @FXML private TextArea comment;

    private Review review;
    private final ServiceReview serviceReview = new ServiceReview();
    private UserDisplayReview parentController;  // Pour revenir à la liste

    @FXML
    public void initialize() {
        // Rien à initialiser ici
    }

    public void setParentController(UserDisplayReview controller) {
        this.parentController = controller;
    }

    public void initData(Review r) {
        this.review = r;

        lblId.setText("ID: " + r.getReviewId());

        // ✅ Pré-remplir et DÉSACTIVER l'eventId
        cbEventId.setValue(r.getEventId());
        cbEventId.setDisable(true);
        cbEventId.setStyle("-fx-opacity: 0.8; -fx-background-color: #f0f0f0;");

        title.setText(r.getTitle());
        rating.setText(String.valueOf(r.getRating()));
        reviewDate.setValue(LocalDate.parse(r.getReviewDate()));
        comment.setText(r.getComment());
    }

    @FXML
    private void handleUpdate() {
        if (review == null) return;

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
            new Alert(Alert.AlertType.ERROR, "Rating doit être entre 1 et 5").show();
            return;
        }

        // ✅ On garde l'eventId original (pas modifiable)
        review.setTitle(title.getText());
        review.setRating(r);
        review.setReviewDate(reviewDate.getValue().toString());
        review.setComment(comment.getText());

        serviceReview.update(review);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Review modifiée avec succès !", ButtonType.OK);
        alert.showAndWait();

        // Fermer la fenêtre
        Stage stage = (Stage) title.getScene().getWindow();
        stage.close();

        // Recharger la liste dans la fenêtre parente
        if (parentController != null) {
            parentController.loadCards();
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) title.getScene().getWindow();
        stage.close();
    }
}