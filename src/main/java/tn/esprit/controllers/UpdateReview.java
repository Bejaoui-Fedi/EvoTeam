package tn.esprit.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Review;
import tn.esprit.services.ServiceEvent;
import tn.esprit.services.ServiceReview;

import java.time.LocalDate;

public class UpdateReview {

    @FXML private Label lblId;
    @FXML private ComboBox<Integer> cbEventId;
    @FXML private TextField title;
    @FXML private TextField rating;
    @FXML private DatePicker reviewDate;
    @FXML private TextArea comment;

    private Review review;
    private final ServiceReview serviceReview = new ServiceReview();
    private final ServiceEvent serviceEvent = new ServiceEvent();
    private AdminDashboardController dashboard;

    public void setDashboardController(AdminDashboardController dashboard) {
        this.dashboard = dashboard;
    }

    @FXML
    public void initialize() {
        cbEventId.setItems(FXCollections.observableArrayList(serviceEvent.getAllEventIds()));
    }

    public void initData(Review r) {
        this.review = r;

        lblId.setText("ID: " + r.getReviewId());
        cbEventId.setValue(r.getEventId());
        title.setText(r.getTitle());
        rating.setText(String.valueOf(r.getRating()));
        reviewDate.setValue(LocalDate.parse(r.getReviewDate()));
        comment.setText(r.getComment());
    }

    @FXML
    private void handleUpdate() {
        if (review == null) return;

        if (cbEventId.getValue() == null || rating.getText().isEmpty() || reviewDate.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "EventId, Rating et Date sont obligatoires !").show();
            return;
        }

        int r;
        try {
            r = Integer.parseInt(rating.getText());
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Rating doit etre un nombre !").show();
            return;
        }

        if (r < 1 || r > 5) {
            new Alert(Alert.AlertType.ERROR, "Rating doit etre entre 1 et 5").show();
            return;
        }

        review.setEventId(cbEventId.getValue());
        review.setTitle(title.getText());
        review.setRating(r);
        review.setReviewDate(reviewDate.getValue().toString());
        review.setComment(comment.getText());

        serviceReview.update(review);

        new Alert(Alert.AlertType.INFORMATION, "Review modifiee avec succes !").show();

        // Retour au DisplayReview avec sidebar intact
        if (dashboard != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayReview.fxml"));
                Parent root = loader.load();

                DisplayReview controller = loader.getController();
                controller.setDashboardController(dashboard);

                dashboard.setContent(root);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            // Fallback : fermer la fenetre si pas de dashboard (popup mode)
            Stage stage = (Stage) title.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) title.getScene().getWindow();
        stage.close();
    }
}