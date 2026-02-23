package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.entities.User;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardContentController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label dateLabel;
    @FXML private ImageView profileAvatar;
    @FXML private GridPane videoGrid;
    @FXML private HBox imageGallery;

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ DashboardContentController initialisé");

        // Mettre à jour la date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        dateLabel.setText(today.format(formatter).toUpperCase());

        loadVideos();
        loadImages();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null && welcomeLabel != null) {
            welcomeLabel.setText("Bonjour, " + user.getNom() + " 👋");
        }
    }

    private void loadVideos() {
        String[][] videos = {
                {"dQw4w9WgXcQ", "Méditation guidée", "15 min • Débutant"},
                {"aJOTlE1K90k", "Yoga doux", "20 min • Intermédiaire"},
                {"inpok4MKVLM", "Respiration profonde", "10 min • Tous niveaux"},
                {"ZToicYcHIOU", "Relaxation matinale", "12 min • Débutant"}
        };

        videoGrid.getChildren().clear();
        for (int i = 0; i < videos.length; i++) {
            VBox videoCard = createVideoCard(videos[i][0], videos[i][1], videos[i][2]);
            videoGrid.add(videoCard, i % 2, i / 2);
        }
    }

    private VBox createVideoCard(String videoId, String title, String duration) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("video-card");

        // Miniature
        ImageView thumbnail = new ImageView();
        try {
            thumbnail.setImage(new Image("https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg"));
        } catch (Exception e) {
            thumbnail.setImage(new Image("https://via.placeholder.com/320x180?text=Video"));
        }
        thumbnail.setFitWidth(300);
        thumbnail.setFitHeight(170);
        thumbnail.setPreserveRatio(true);
        thumbnail.getStyleClass().add("video-thumbnail");
        thumbnail.setStyle("-fx-cursor: hand;");

        // Titre
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        // Durée
        Label durationLabel = new Label(duration);
        durationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #5F9B8A;");

        // Bouton play
        Button playButton = new Button("▶ Regarder");
        playButton.getStyleClass().add("play-button");
        playButton.setOnAction(e -> openVideo(videoId));
        thumbnail.setOnMouseClicked(e -> openVideo(videoId));

        card.getChildren().addAll(thumbnail, titleLabel, durationLabel, playButton);
        return card;
    }

    private void loadImages() {
        String[][] images = {
                {"https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=300&h=200&fit=crop", "Méditation"},
                {"https://images.unsplash.com/photo-1518611012118-696072aa579a?w=300&h=200&fit=crop", "Yoga"},
                {"https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=300&h=200&fit=crop", "Relaxation"},
                {"https://images.unsplash.com/photo-1501004318641-b39e6451bec6?w=300&h=200&fit=crop", "Nature"}
        };

        imageGallery.getChildren().clear();
        for (String[] img : images) {
            VBox card = createImageCard(img[0], img[1]);
            imageGallery.getChildren().add(card);
        }
    }

    private VBox createImageCard(String imageUrl, String caption) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("image-card");

        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(imageUrl, true));
        } catch (Exception e) {
            imageView.setImage(new Image("https://via.placeholder.com/250x180?text=Image"));
        }
        imageView.setFitWidth(230);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-background-radius: 12;");

        Label captionLabel = new Label(caption);
        captionLabel.getStyleClass().add("image-caption");

        card.getChildren().addAll(imageView, captionLabel);
        return card;
    }

    private void openVideo(String videoId) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI("https://www.youtube.com/watch?v=" + videoId));
        } catch (Exception ex) {
            System.err.println("Erreur ouverture vidéo: " + ex.getMessage());
        }
    }
}