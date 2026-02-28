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
import tn.esprit.services.GoogleCalendarService;  // ← AJOUTE CET IMPORT
import tn.esprit.controllers.QRCodeEventController;
import javafx.stage.Stage;
import javafx.scene.Scene;

import tn.esprit.controllers.MapTilerController;

import tn.esprit.services.CurrencyService;
import javafx.stage.FileChooser;
import java.io.File;
import tn.esprit.services.ExcelExportService;
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
        card.setPrefWidth(400);
        card.getStyleClass().add("event-card");

        // Titre
        Label title = new Label(e.getName());
        title.getStyleClass().add("event-title");

        // Bouton Google Calendar
        Button btnCalendar = new Button("📅");
        btnCalendar.setStyle("-fx-background-color: #4285F4; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 6 10; -fx-background-radius: 15;");
        btnCalendar.setOnAction(ev -> addToGoogleCalendar(e));

        // Bouton Carte
        Button btnMap = new Button("🗺️");
        btnMap.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 8;");
        btnMap.setOnAction(ev -> openMap(e));

        // Bouton QR code
        Button btnQR = new Button("📱");
        btnQR.setStyle("-fx-background-color: #6A4E9B; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 8;");
        btnQR.setOnAction(ev -> showQRCode(e));

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

        // Bouton "Mes Reviews"
        Button btnReviews = new Button("Mes Reviews");
        btnReviews.setStyle(
                "-fx-background-color: #3A7D6B; -fx-text-fill: white; " +
                        "-fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 6 14; " +
                        "-fx-background-radius: 15;"
        );
        btnReviews.setOnAction(ev -> goToReviewsForEvent(e));

        HBox header = new HBox(10, title, spacer, badge, btnMap, btnQR, btnCalendar, btnReviews);
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

        // ✅ CONVERSIONS DINAR → EURO / DOLLAR
        Label conversionLabel = new Label();
        if (e.getFee() > 0) {
            String conversions = String.format("💶 %s | 💵 %s",
                    CurrencyService.formatEur(e.getFee()),
                    CurrencyService.formatUsd(e.getFee()));
            conversionLabel.setText(conversions);
            conversionLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #3A7D6B; -fx-font-weight: bold; -fx-padding: 5 0 0 0;");
        } else {
            conversionLabel.setText("💶 Gratuit | 💵 Free");
            conversionLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #4CAF50; -fx-padding: 5 0 0 0;");
        }

        Separator sep = new Separator();
        sep.getStyleClass().add("event-separator");

        // ✅ Ajout du label de conversion DANS la carte
        card.getChildren().addAll(header, sep, dates, max, location, desc, conversionLabel);
        return card;
    }


    // ✅ NOUVELLE MÉTHODE POUR GOOGLE CALENDAR
    // =====================================================
    private void addToGoogleCalendar(Event event) {
        try {
            String eventLink = GoogleCalendarService.addEventToGoogleCalendar(event);

            if (eventLink != null) {
                // Formater la date (2026-02-19 → 20260219)
                String dateStr = event.getStartDate().replace("-", "");

                // URL SIMPLE et SANS caractères spéciaux
                String calendarUrl = "https://calendar.google.com/calendar/r/month?tab=mc";

                java.awt.Desktop.getDesktop().browse(new java.net.URI(calendarUrl));

                new Alert(Alert.AlertType.INFORMATION,
                        "Événement ajouté à Google Calendar !\nRegardez à la date du " + event.getStartDate()).show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout à Google Calendar").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur: " + e.getMessage()).show();
        }
    }


    private void showQRCode(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/QRCodeEvent.fxml"));
            Parent root = loader.load();

            QRCodeEventController controller = loader.getController();
            controller.setEventData(event);

            Stage stage = new Stage();
            stage.setTitle("QR Code - " + event.getName());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir la fenêtre QR code").show();
        }
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



    private void openMap(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MapTilerView.fxml"));
            Parent root = loader.load();

            MapTilerController controller = loader.getController();
            controller.setEvent(event);

            // Pour l'instant, on utilise des coordonnées fixes (Tunis)
            controller.loadMapWithCoords(36.8065, 10.1815);

            Stage stage = new Stage();
            stage.setTitle("Carte - " + event.getName());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir la carte").show();
        }
    }



    @FXML
    private void openHistorique() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HistoriqueView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Historique");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir l'historique").show();
        }
    }



    @FXML
    private void exportToExcel() {
        try {
            // Boîte de dialogue pour choisir l'emplacement
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter les événements");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx")
            );
            fileChooser.setInitialFileName("mes_evenements.xlsx");

            File file = fileChooser.showSaveDialog(cardsContainer.getScene().getWindow());

            if (file != null) {
                // Récupérer tous les événements
                List<Event> events = serviceEvent.getAll();

                // Exporter
                ExcelExportService.exporterEvenements(events, file.getAbsolutePath());

                new Alert(Alert.AlertType.INFORMATION,
                        "✅ Export réussi !\nFichier : " + file.getName()).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Erreur : " + e.getMessage()).show();
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