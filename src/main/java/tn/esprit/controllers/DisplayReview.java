package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import tn.esprit.services.TraductionService;
import javafx.scene.control.ChoiceDialog;

public class DisplayReview {

    @FXML private FlowPane cardsContainer;
    @FXML private Label lblCount;
    @FXML private TextField searchField;

    private final ServiceReview serviceReview = new ServiceReview();
    private Image imgEdit, imgDelete;

    private AdminDashboardController dashboard;

    public void setDashboardController(AdminDashboardController dashboard) {
        this.dashboard = dashboard;
    }

    @FXML
    public void initialize() {
        imgEdit = new Image(getClass().getResource("/images/edit.png").toExternalForm());
        imgDelete = new Image(getClass().getResource("/images/delete.png").toExternalForm());
        loadCards();
    }

    @FXML
    private void refresh() {
        loadCards();
        searchField.clear();
    }

    // ================== RECHERCHE MODIFIÉE ==================
    @FXML
    private void handleSearch() {
        String text = searchField.getText().toLowerCase().trim();
        cardsContainer.getChildren().clear();
        // Utilise la méthode de recherche avec jointure
        List<Review> reviews = serviceReview.searchWithUserNames(text);
        lblCount.setText(reviews.size() + " review(s)");
        for (Review r : reviews) {
            cardsContainer.getChildren().add(createCard(r));
        }
    }

    @FXML
    private void goToDisplayEvent(ActionEvent event) {
        try {
            if (dashboard != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayEvent.fxml"));
                Parent root = loader.load();

                DisplayEvent controller = loader.getController();
                controller.setDashboardController(dashboard);

                dashboard.setContent(root);
            } else {
                Parent root = FXMLLoader.load(getClass().getResource("/DisplayEvent.fxml"));
                Stage stage = (Stage) cardsContainer.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Events");
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir DisplayEvent.fxml").show();
        }
    }

    // ================== CRÉATION DE CARTE AVEC NOM D'UTILISATEUR ==================
    private VBox createCard(Review r) {
        VBox card = new VBox(12);
        card.setPrefWidth(300);
        card.getStyleClass().add("event-card");

        // Titre
        String titleText = (r.getTitle() == null || r.getTitle().trim().isEmpty()) ? "Review" : r.getTitle();
        Label title = new Label("⭐ " + titleText);
        title.getStyleClass().add("event-title");

        // 👇 AFFICHAGE DU NOM D'UTILISATEUR
        String userDisplay;
        if (r.getUserId() == 0 || r.getUserId() == -1) {
            userDisplay = "👑 Admin";
        } else {
            String name = (r.getUserName() != null && !r.getUserName().isEmpty())
                    ? r.getUserName()
                    : "Utilisateur inconnu";
            userDisplay = "👤 " + name + " (ID: " + r.getUserId() + ")";
        }
        Label userLabel = new Label(userDisplay);
        userLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #3A7D6B; -fx-font-weight: bold; -fx-padding: 0 0 5 0;");

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


        // Bouton Traduire
        Button btnTraduire = new Button("🌐");
        btnTraduire.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 8;");
        btnTraduire.setOnAction(ev -> traduireCommentaire(r));


        // Bouton Delete (rouge)
        Button btnDelete = new Button("🗑");
        btnDelete.setStyle("-fx-background-color: #FF3A3A; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnDelete.setOnAction(ev -> confirmAndDelete(r));

        // Header avec le nom d'utilisateur au-dessus
        HBox header = new HBox(10, title, spacer, badge, btnTraduire, btnEdit, btnDelete);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Infos
        Label dates = new Label("📅 " + r.getReviewDate());
        dates.getStyleClass().add("event-info");

        Label eventInfo = new Label("🎉 Event ID: " + r.getEventId());
        eventInfo.getStyleClass().add("event-info");

        Label ratingLabel = new Label("⭐ Note: " + r.getRating() + "/5");
        ratingLabel.getStyleClass().add("event-info");

        // Commentaire
        Label desc = new Label(r.getComment() == null ? "" : r.getComment());
        desc.setWrapText(true);
        desc.getStyleClass().add("event-description");

        Separator sep = new Separator();
        sep.getStyleClass().add("event-separator");

        // Ajouter tous les éléments (userLabel en premier)
        card.getChildren().addAll(userLabel, header, sep, dates, eventInfo, ratingLabel, desc);
        return card;
    }

    private void confirmAndDelete(Review r) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer la review : " + r.getTitle() + " ?", ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                serviceReview.delete(r.getReviewId());
                loadCards();
                new Alert(Alert.AlertType.INFORMATION, "Review supprimee ✅").show();
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
            Stage stage = new Stage();
            stage.setTitle("Ajouter Review");
            stage.setScene(new Scene(root));
            stage.show();

            stage.setOnHidden(e -> loadCards());
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir AddReview.fxml").show();
        }
    }



    private void traduireCommentaire(Review review) {
        // Choix de la langue SOURCE
        ChoiceDialog<String> sourceDialog = new ChoiceDialog<>("Français",
                "Français", "Anglais", "Espagnol", "Allemand", "Italien", "Arabe");
        sourceDialog.setTitle("Langue source");
        sourceDialog.setHeaderText("Dans quelle langue est le commentaire ?");
        sourceDialog.setContentText("Langue source :");

        sourceDialog.showAndWait().ifPresent(sourceLangue -> {
            // Choix de la langue CIBLE
            ChoiceDialog<String> targetDialog = new ChoiceDialog<>("Anglais",
                    "Français", "Anglais", "Espagnol", "Allemand", "Italien", "Arabe");
            targetDialog.setTitle("Langue cible");
            targetDialog.setHeaderText("Vers quelle langue traduire ?");
            targetDialog.setContentText("Langue cible :");

            targetDialog.showAndWait().ifPresent(targetLangue -> {
                // Convertir les noms en codes
                String sourceCode = getCodeLangue(sourceLangue);
                String targetCode = getCodeLangue(targetLangue);

                // Appeler le service avec la source spécifiée
                String texteOriginal = review.getComment();
                String texteTraduit = TraductionService.traduireAvecSource(texteOriginal, sourceCode, targetCode);

                // Afficher le résultat
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Résultat de la traduction");
                alert.setHeaderText("Commentaire original (" + sourceLangue + ") :\n" + texteOriginal);
                alert.setContentText("Traduit en " + targetLangue + " :\n" + texteTraduit);
                alert.showAndWait();
            });
        });
    }

    private String getCodeLangue(String nom) {
        switch (nom) {
            case "Français": return "fr";
            case "Anglais": return "en";
            case "Espagnol": return "es";
            case "Allemand": return "de";
            case "Italien": return "it";
            case "Arabe": return "ar";
            default: return "en";
        }
    }




    // ================== LOAD CARDS MODIFIÉ ==================
    private void loadCards() {
        cardsContainer.getChildren().clear();
        // Utilise la méthode avec jointure
        List<Review> reviews = serviceReview.getAllWithUserNames();
        lblCount.setText(reviews.size() + " review(s)");
        for (Review r : reviews) {
            cardsContainer.getChildren().add(createCard(r));
        }
    }
}