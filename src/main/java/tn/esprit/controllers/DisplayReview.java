package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.entities.Review;
import tn.esprit.services.ServiceReview;

import java.io.IOException;
import java.util.List;

public class DisplayReview {

    @FXML private FlowPane cardsContainer;
    @FXML private Label lblCount;

    private final ServiceReview serviceReview = new ServiceReview();

    @FXML
    public void initialize() {
        loadCards();
    }

    @FXML
    private void refresh() {
        loadCards();
    }

    private void loadCards() {
        cardsContainer.getChildren().clear();

        List<Review> reviews = serviceReview.getAll();
        lblCount.setText(reviews.size() + " review(s)");

        for (Review r : reviews) {
            cardsContainer.getChildren().add(createCard(r));
        }
    }

    private VBox createCard(Review r) {

        VBox card = new VBox(8);
        card.setPrefWidth(320);
        card.setStyle("""
                -fx-background-color: white;
                -fx-padding: 14;
                -fx-border-color: #E2E2E2;
                -fx-border-radius: 12;
                -fx-background-radius: 12;
                """);

        Label lblTitle = new Label(
                (r.getTitle() == null || r.getTitle().trim().isEmpty()) ? "Review" : r.getTitle()
        );
        lblTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Button btnEdit = iconButton("/images/edit.png");
        Button btnDelete = iconButton("/images/delete.png");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox top = new HBox(10, lblTitle, spacer, btnEdit, btnDelete);

        Label info = new Label("⭐ " + r.getRating() + "/5  |  📅 " + r.getReviewDate() + "  |  EventId: " + r.getEventId());
        info.setStyle("-fx-text-fill:#555555;");

        Label lblComment = new Label(r.getComment() == null ? "" : r.getComment());
        lblComment.setWrapText(true);

        btnDelete.setOnAction(e -> confirmAndDelete(r));
        btnEdit.setOnAction(e -> openUpdateWindow(r));

        card.getChildren().addAll(top, new Separator(), info, lblComment);
        return card;
    }

    private Button iconButton(String resourcePath) {
        Image img = new Image(getClass().getResource(resourcePath).toExternalForm());
        ImageView iv = new ImageView(img);
        iv.setFitWidth(18);
        iv.setFitHeight(18);
        iv.setPreserveRatio(true);

        Button b = new Button();
        b.setGraphic(iv);
        b.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        return b;
    }


    @FXML
    private void goToDisplayEvent() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/DisplayEvent.fxml"));
            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Events");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir DisplayEvent.fxml").show();
        }
    }



    private void confirmAndDelete(Review r) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Suppression");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer cette review (id=" + r.getReviewId() + ") ?");

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                serviceReview.delete(r.getReviewId());
                loadCards();
                new Alert(Alert.AlertType.INFORMATION, "Review supprimée ✅").show();
            }
        });
    }

    private void openUpdateWindow(Review r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateReview.fxml"));
            Parent root = loader.load();

            UpdateReview controller = loader.getController();
            controller.initData(r);

            Stage stage = new Stage();
            stage.setTitle("Modifier Review");
            stage.setScene(new Scene(root));
            stage.show();

            // refresh après fermeture de la fenêtre update
            stage.setOnHidden(ev -> loadCards());

        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir UpdateReview.fxml").show();
        }
    }

    @FXML
    private void goToAddReview() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AddReview.fxml"));
            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Review");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir AddReview.fxml").show();
        }
    }
}
