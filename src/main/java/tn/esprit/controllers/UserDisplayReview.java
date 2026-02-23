package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.entities.Review;
import tn.esprit.services.ServiceReview;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class UserDisplayReview {

    @FXML private FlowPane cardsContainer;
    @FXML private Label lblCount;
    @FXML private Label lblHeaderTitle;
    @FXML private Label lblHeaderSubtitle;
    @FXML private TextField searchField;

    private final ServiceReview serviceReview = new ServiceReview();

    // Reference vers le UserDashboardController
    private UserDashboardController userDashboard;

    // L'event pour lequel on affiche les reviews
    private Event currentEvent;

    public void setUserDashboardController(UserDashboardController userDashboard) {
        this.userDashboard = userDashboard;
    }

    /**
     * Definir l'event et charger ses reviews
     */
    public void setEvent(Event event) {
        this.currentEvent = event;
        if (event != null) {
            lblHeaderTitle.setText("Reviews - " + event.getName());
            lblHeaderSubtitle.setText("Avis pour l'evenement : " + event.getName());
        }
        loadCards();
    }

    @FXML
    public void initialize() {
        // Les cards seront chargees apres setEvent()
    }

    @FXML
    private void refresh() {
        loadCards();
        searchField.clear();
    }

    @FXML
    private void handleSearch() {
        String text = searchField.getText().toLowerCase().trim();
        cardsContainer.getChildren().clear();
        List<Review> reviews = getFilteredReviews();
        if (!text.isEmpty()) {
            reviews.removeIf(r ->
                    (r.getTitle() == null || !r.getTitle().toLowerCase().contains(text)) &&
                            (r.getComment() == null || !r.getComment().toLowerCase().contains(text))
            );
        }
        lblCount.setText(reviews.size() + " review(s)");
        for (Review r : reviews) {
            cardsContainer.getChildren().add(createCard(r));
        }
    }

    // =====================================================
    // Retour vers la liste des evenements (dans le sidebar)
    // =====================================================
    @FXML
    private void goBackToEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDisplayEvent.fxml"));
            Parent root = loader.load();

            UserDisplayEvent controller = loader.getController();
            controller.setUserDashboardController(userDashboard);

            if (userDashboard != null) {
                userDashboard.setContent(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir UserDisplayEvent.fxml").show();
        }
    }

    // =====================================================
    // Ouvrir le formulaire d'ajout de review (popup)
    // =====================================================
    @FXML
    private void goToAddReview() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddReview.fxml"));
            Parent root = loader.load();

            // Si ton AddReview a un champ eventId, on le pre-remplit
            Object controller = loader.getController();
            if (controller instanceof AddReview) {
                // Si tu as une methode setEventId dans AddReview, decommente :
                // ((AddReview) controller).setEventId(currentEvent.getEventId());
            }

            Stage stage = new Stage();
            stage.setTitle("Ajouter une Review");
            stage.setScene(new Scene(root));
            stage.show();

            // Quand le popup se ferme, on recharge les cards
            stage.setOnHidden(e -> loadCards());
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir AddReview.fxml").show();
        }
    }

    // =====================================================
    // Cree une carte review (avec edit + delete pour l'utilisateur)
    // =====================================================
    private VBox createCard(Review r) {
        VBox card = new VBox(12);
        card.setPrefWidth(300);
        card.getStyleClass().add("event-card");

        // Titre
        String titleText = (r.getTitle() == null || r.getTitle().trim().isEmpty()) ? "Review" : r.getTitle();
        Label title = new Label("⭐ " + titleText);
        title.getStyleClass().add("event-title");

        // Badge rating
        Label badge = new Label();
        badge.setText(r.getRating() + "/5");
        if (r.getRating() >= 4) {
            badge.getStyleClass().add("badge-free");
        } else {
            badge.getStyleClass().add("badge-paid");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Bouton Edit (bleu)
        Button btnEdit = new Button("✏️");
        btnEdit.setStyle("-fx-background-color: #3A7DFF; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnEdit.setOnAction(ev -> openUpdateWindow(r));

        // Bouton Delete (rouge)
        Button btnDelete = new Button("🗑");
        btnDelete.setStyle("-fx-background-color: #FF3A3A; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnDelete.setOnAction(ev -> confirmAndDelete(r));

        HBox header = new HBox(10, title, spacer, badge, btnEdit, btnDelete);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Infos
        Label dates = new Label("📅 " + r.getReviewDate());
        dates.getStyleClass().add("event-info");

        Label ratingLabel = new Label("⭐ Note: " + r.getRating() + "/5");
        ratingLabel.getStyleClass().add("event-info");

        // Commentaire
        Label desc = new Label(r.getComment() == null ? "" : r.getComment());
        desc.setWrapText(true);
        desc.getStyleClass().add("event-description");

        Separator sep = new Separator();
        sep.getStyleClass().add("event-separator");

        card.getChildren().addAll(header, sep, dates, ratingLabel, desc);
        return card;
    }

    // =====================================================
    // Suppression avec confirmation
    // =====================================================
    private void confirmAndDelete(Review r) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer la review : " + r.getTitle() + " ?", ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                serviceReview.delete(r.getReviewId());
                loadCards();
                new Alert(Alert.AlertType.INFORMATION, "Review supprimee !").show();
            }
        });
    }

    // =====================================================
    // Ouvrir le formulaire de modification (popup)
    // =====================================================
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

            stage.setOnHidden(ev -> loadCards());
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir UpdateReview.fxml").show();
        }
    }

    // =====================================================
    // Charger les reviews filtrees par event
    // =====================================================
    private List<Review> getFilteredReviews() {
        List<Review> allReviews = serviceReview.getAll();
        if (currentEvent != null) {
            return allReviews.stream()
                    .filter(r -> r.getEventId() == currentEvent.getEventId())
                    .collect(Collectors.toList());
        }
        return allReviews;
    }

    private void loadCards() {
        cardsContainer.getChildren().clear();
        List<Review> reviews = getFilteredReviews();
        lblCount.setText(reviews.size() + " review(s)");
        for (Review r : reviews) {
            cardsContainer.getChildren().add(createCard(r));
        }
    }
}
