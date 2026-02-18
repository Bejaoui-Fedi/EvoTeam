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
import tn.esprit.entities.User;
import tn.esprit.services.ServiceReview;

import java.io.IOException;
import java.util.List;

public class UserDisplayReview {

    @FXML private FlowPane cardsContainer;
    @FXML private Label lblCount;
    @FXML private Label lblHeaderTitle;
    @FXML private Label lblHeaderSubtitle;
    @FXML private TextField searchField;

    private final ServiceReview serviceReview = new ServiceReview();

    private UserDashboardController userDashboard;
    private Event currentEvent;
    private User currentUser;  // L'UTILISATEUR CONNECTE

    public void setUserDashboardController(UserDashboardController userDashboard) {
        this.userDashboard = userDashboard;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Definir l'event et charger SEULEMENT les reviews de CET user pour CET event
     */
    public void setEvent(Event event) {
        this.currentEvent = event;
        if (event != null) {
            lblHeaderTitle.setText("Mes Reviews - " + event.getName());
            lblHeaderSubtitle.setText("Vos avis pour : " + event.getName());
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
        List<Review> reviews = getMyReviews();
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
    // Retour vers la liste des evenements
    // =====================================================
    @FXML
    private void goBackToEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDisplayEvent.fxml"));
            Parent root = loader.load();

            UserDisplayEvent controller = loader.getController();
            controller.setUserDashboardController(userDashboard);
            controller.setCurrentUser(currentUser);  // ON REPASSE LE USER

            if (userDashboard != null) {
                userDashboard.setContent(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir UserDisplayEvent.fxml").show();
        }
    }

    // =====================================================
    // Ajouter une review -- VERIFIE SI DEJA EXISTANTE
    // =====================================================
    @FXML
    private void goToAddReview() {
        // ====== VERIFICATION : l'user a-t-il deja une review pour cet event ? ======
        if (currentUser != null && currentEvent != null) {
            boolean exists = serviceReview.existsReviewForUserAndEvent(
                    currentUser.getId(), currentEvent.getEventId()
            );
            if (exists) {
                new Alert(Alert.AlertType.WARNING,
                        "Vous avez deja une review pour cet evenement !\nVous pouvez la modifier ou la supprimer."
                ).show();
                return;  // BLOQUE L'AJOUT
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddReview.fxml"));
            Parent root = loader.load();

            // Recuperer le controller AddReview pour pre-remplir eventId et userId
            Object controller = loader.getController();
            if (controller instanceof AddReview) {
                AddReview addReviewCtrl = (AddReview) controller;

                // ✅ UTILISATION DE LA NOUVELLE METHODE setForUser()
                if (currentUser != null && currentEvent != null) {
                    addReviewCtrl.setForUser(currentUser.getId(), currentEvent.getEventId());
                }
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
    // Carte review (CRUD pour l'utilisateur)
    // =====================================================
    private VBox createCard(Review r) {
        VBox card = new VBox(12);
        card.setPrefWidth(300);
        card.getStyleClass().add("event-card");

        // Titre
        String titleText = (r.getTitle() == null || r.getTitle().trim().isEmpty()) ? "Review" : r.getTitle();
        Label title = new Label(titleText);
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

        // Bouton Edit
        Button btnEdit = new Button("Modifier");
        btnEdit.setStyle("-fx-background-color: #3A7DFF; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 10;");
        btnEdit.setOnAction(ev -> openUpdateWindow(r));

        // Bouton Delete
        Button btnDelete = new Button("Supprimer");
        btnDelete.setStyle("-fx-background-color: #FF3A3A; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 10;");
        btnDelete.setOnAction(ev -> confirmAndDelete(r));

        HBox header = new HBox(10, title, spacer, badge, btnEdit, btnDelete);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Infos
        Label dates = new Label("Date: " + r.getReviewDate());
        dates.getStyleClass().add("event-info");

        Label ratingLabel = new Label("Note: " + r.getRating() + "/5");
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
                "Supprimer la review : " + r.getTitle() + " ?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                serviceReview.delete(r.getReviewId());
                loadCards();
                new Alert(Alert.AlertType.INFORMATION, "Review supprimée !").show();
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
    // FILTRAGE : reviews de CET event faites par CE user uniquement
    // =====================================================
    private List<Review> getMyReviews() {
        if (currentEvent != null && currentUser != null) {
            // Retourne SEULEMENT les reviews de cet event par cet utilisateur
            return serviceReview.getByEventIdAndUserId(
                    currentEvent.getEventId(), currentUser.getId()
            );
        } else if (currentEvent != null) {
            // Fallback : toutes les reviews de l'event (si pas de user -- ne devrait pas arriver)
            return serviceReview.getByEventId(currentEvent.getEventId());
        }
        return serviceReview.getAll();
    }

    public void loadCards() {
        cardsContainer.getChildren().clear();
        List<Review> reviews = getMyReviews();
        lblCount.setText(reviews.size() + " review(s)");
        for (Review r : reviews) {
            cardsContainer.getChildren().add(createCard(r));
        }
    }
}