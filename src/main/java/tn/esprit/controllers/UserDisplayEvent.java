package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import tn.esprit.entities.Event;
import tn.esprit.entities.User;
import tn.esprit.services.ServiceEvent;

import java.io.IOException;
import java.util.List;

public class UserDisplayEvent {

    @FXML private FlowPane cardsContainer;
    @FXML private Label lblCount;
    @FXML private TextField searchField;

    private ServiceEvent serviceEvent = new ServiceEvent();

    private UserDashboardController userDashboard;
    private User currentUser;  // L'UTILISATEUR CONNECTE

    public void setUserDashboardController(UserDashboardController userDashboard) {
        this.userDashboard = userDashboard;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        loadCards();
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
        List<Event> events = serviceEvent.getAll();
        if (!text.isEmpty()) {
            events.removeIf(e -> !e.getName().toLowerCase().contains(text) &&
                    !e.getLocation().toLowerCase().contains(text));
        }
        lblCount.setText(events.size() + " evenement(s)");
        for (Event e : events) {
            cardsContainer.getChildren().add(createCard(e));
        }
    }

    // =====================================================
    // Carte event en LECTURE SEULE + bouton "Reviews"
    // =====================================================
    private VBox createCard(Event e) {
        VBox card = new VBox(12);
        card.setPrefWidth(300);
        card.getStyleClass().add("event-card");

        // Titre
        Label title = new Label(e.getName());
        title.getStyleClass().add("event-title");

        // Badge prix
        Label badge = new Label();
        if (e.getFee() == 0) {
            badge.setText("GRATUIT");
            badge.getStyleClass().add("badge-free");
        } else {
            badge.setText(e.getFee() + " DT");
            badge.getStyleClass().add("badge-paid");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Bouton "Mes Reviews" -- navigue vers UserDisplayReview
        Button btnReviews = new Button("Mes Reviews");
        btnReviews.setStyle(
                "-fx-background-color: #3A7D6B; -fx-text-fill: white; " +
                        "-fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 6 14; " +
                        "-fx-background-radius: 15;"
        );
        btnReviews.setOnAction(ev -> goToReviewsForEvent(e));

        HBox header = new HBox(10, title, spacer, badge, btnReviews);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Infos
        Label dates = new Label("Date: " + e.getStartDate() + " - " + e.getEndDate());
        dates.getStyleClass().add("event-info");

        Label max = new Label("Max participants: " + e.getMaxParticipants());
        max.getStyleClass().add("event-info");

        Label location = new Label("Lieu: " + e.getLocation());
        location.getStyleClass().add("event-info");

        // Description
        Label desc = new Label(e.getDescription());
        desc.setWrapText(true);
        desc.getStyleClass().add("event-description");

        Separator sep = new Separator();
        sep.getStyleClass().add("event-separator");

        card.getChildren().addAll(header, sep, dates, max, location, desc);
        return card;
    }

    // =====================================================
    // Naviguer vers UserDisplayReview -- on passe le user ET l'event
    // =====================================================
    private void goToReviewsForEvent(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDisplayReview.fxml"));
            Parent root = loader.load();

            UserDisplayReview controller = loader.getController();
            controller.setUserDashboardController(userDashboard);
            controller.setCurrentUser(currentUser);  // PASSE LE USER
            controller.setEvent(event);               // FILTRE PAR EVENT + USER

            if (userDashboard != null) {
                userDashboard.setContent(root);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir UserDisplayReview.fxml").show();
        }
    }

    private void loadCards() {
        cardsContainer.getChildren().clear();
        List<Event> events = serviceEvent.getAll();
        lblCount.setText(events.size() + " evenement(s)");
        for (Event e : events) {
            cardsContainer.getChildren().add(createCard(e));
        }
    }
}
