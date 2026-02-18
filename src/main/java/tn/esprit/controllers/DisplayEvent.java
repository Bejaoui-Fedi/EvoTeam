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
import tn.esprit.entities.Event;
import tn.esprit.services.ServiceEvent;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import tn.esprit.controllers.QRCodeEventController;
public class DisplayEvent {

    @FXML private FlowPane cardsContainer;
    @FXML private Label lblCount;
    @FXML private TextField searchField;

    // Nouveaux éléments pour statistiques et tri
    @FXML private Label lblTotalEvents;
    @FXML private Label lblMaxParticipants;
    @FXML private Label lblAvgFee;
    @FXML private Label lblMostExpensive;
    @FXML private ComboBox<String> cbSortBy;

    private ServiceEvent serviceEvent = new ServiceEvent();
    private Image imgEdit, imgDelete;
    private List<Event> allEvents; // Pour garder la liste complète

    private AdminDashboardController dashboard;

    public void setDashboardController(AdminDashboardController dashboard) {
        this.dashboard = dashboard;
    }

    @FXML
    public void initialize() {
        imgEdit = new Image(getClass().getResource("/images/edit.png").toExternalForm());
        imgDelete = new Image(getClass().getResource("/images/delete.png").toExternalForm());

        // Initialiser le ComboBox de tri
        cbSortBy.getItems().addAll(
                "Nom (A-Z)", "Nom (Z-A)",
                "Date (récent)", "Date (ancien)",
                "Prix (croissant)", "Prix (décroissant)",
                "Participants (max)", "Participants (min)"
        );
        cbSortBy.setValue("Nom (A-Z)"); // Valeur par défaut

        loadCards();
    }

    @FXML
    private void refresh() {
        loadCards();
        searchField.clear();
    }

    // =====================================================
    // Méthodes de statistiques
    // =====================================================
    private void updateStatistics(List<Event> events) {
        if (events.isEmpty()) {
            lblTotalEvents.setText("Total: 0");
            lblMaxParticipants.setText("Max: -");
            lblAvgFee.setText("Moyenne: -");
            lblMostExpensive.setText("Plus cher: -");
            return;
        }

        // Nombre total
        lblTotalEvents.setText("Total: " + events.size());

        // Max participants
        int maxParticipants = events.stream()
                .mapToInt(Event::getMaxParticipants)
                .max()
                .orElse(0);
        lblMaxParticipants.setText("Max: " + maxParticipants);

        // Prix moyen
        double avgFee = events.stream()
                .mapToDouble(Event::getFee)
                .average()
                .orElse(0);
        lblAvgFee.setText(String.format("Moyenne: %.2f DT", avgFee));

        // Événement le plus cher
        Event mostExpensive = events.stream()
                .max(Comparator.comparing(Event::getFee))
                .orElse(null);
        if (mostExpensive != null) {
            lblMostExpensive.setText("Plus cher: " + mostExpensive.getName() +
                    " (" + mostExpensive.getFee() + " DT)");
        }
    }

    // =====================================================
    // Méthode de tri
    // =====================================================
    @FXML
    private void handleSort() {
        if (allEvents == null || allEvents.isEmpty()) return;

        String selectedSort = cbSortBy.getValue();
        if (selectedSort == null) return;

        List<Event> sortedList = new ArrayList<>(allEvents);

        switch (selectedSort) {
            case "Nom (A-Z)":
                sortedList.sort(Comparator.comparing(Event::getName));
                break;
            case "Nom (Z-A)":
                sortedList.sort(Comparator.comparing(Event::getName).reversed());
                break;
            case "Date (récent)":
                sortedList.sort(Comparator.comparing(Event::getStartDate).reversed());
                break;
            case "Date (ancien)":
                sortedList.sort(Comparator.comparing(Event::getStartDate));
                break;
            case "Prix (croissant)":
                sortedList.sort(Comparator.comparing(Event::getFee));
                break;
            case "Prix (décroissant)":
                sortedList.sort(Comparator.comparing(Event::getFee).reversed());
                break;
            case "Participants (max)":
                sortedList.sort(Comparator.comparing(Event::getMaxParticipants).reversed());
                break;
            case "Participants (min)":
                sortedList.sort(Comparator.comparing(Event::getMaxParticipants));
                break;
        }

        displayEvents(sortedList);
    }

    // =====================================================
    // CORRIGE : Navigation vers DisplayReview via le dashboard
    // =====================================================
    @FXML
    private void goToDisplayReview(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayReview.fxml"));
            Parent root = loader.load();

            DisplayReview controller = loader.getController();
            controller.setDashboardController(dashboard);

            if (dashboard != null) {
                dashboard.setContent(root);
            } else {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Reviews");
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir DisplayReview.fxml").show();
        }
    }

    @FXML
    private void handleSearch() {
        String text = searchField.getText().toLowerCase().trim();

        if (text.isEmpty()) {
            displayEvents(allEvents);
            return;
        }

        List<Event> filtered = allEvents.stream()
                .filter(e -> e.getName().toLowerCase().contains(text) ||
                        e.getLocation().toLowerCase().contains(text))
                .collect(Collectors.toList());

        displayEvents(filtered);
    }

    private VBox createCard(Event e) {
        VBox card = new VBox(12);
        card.setPrefWidth(300);
        card.getStyleClass().add("event-card");

        Label title = new Label("🎉 [" + e.getEventId() + "] " + e.getName());
        title.getStyleClass().add("event-title");

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

        // ✅ Bouton QR code
        Button btnQR = new Button("📱");
        btnQR.setStyle("-fx-background-color: #6A4E9B; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 8;");
        btnQR.setOnAction(ev -> showQRCode(e));

        Button btnEdit = new Button("✏️");
        btnEdit.setStyle("-fx-background-color: #3A7DFF; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 8;");
        btnEdit.setOnAction(ev -> openUpdateWindow(e));

        Button btnDelete = new Button("🗑");
        btnDelete.setStyle("-fx-background-color: #FF3A3A; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 8;");
        btnDelete.setOnAction(ev -> confirmAndDelete(e));

        HBox header = new HBox(10, title, spacer, badge, btnQR, btnEdit, btnDelete);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label dates = new Label("📅 " + e.getStartDate() + " → " + e.getEndDate());
        dates.getStyleClass().add("event-info");

        Label max = new Label("👥 Max participants: " + e.getMaxParticipants());
        max.getStyleClass().add("event-info");

        Label location = new Label("📍 " + e.getLocation());
        location.getStyleClass().add("event-info");

        Label desc = new Label(e.getDescription());
        desc.setWrapText(true);
        desc.getStyleClass().add("event-description");

        Separator sep = new Separator();
        sep.getStyleClass().add("event-separator");

        card.getChildren().addAll(header, sep, dates, max, location, desc);
        return card;
    }

    // Nouvelle méthode pour afficher le QR code
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
    private void confirmAndDelete(Event e) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer l'evenement : " + e.getName() + " ?", ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                serviceEvent.delete(e.getEventId());
                loadCards();
                new Alert(Alert.AlertType.INFORMATION, "Supprime").show();
            }
        });
    }

    private void openUpdateWindow(Event selected) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateEvent.fxml"));
            Parent root = loader.load();

            UpdateEvent controller = loader.getController();
            controller.initData(selected);
            controller.setDashboardController(dashboard);

            Stage stage = new Stage();
            stage.setTitle("Modifier Event");
            stage.setScene(new Scene(root));
            stage.show();

            stage.setOnHidden(ev -> loadCards());
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir UpdateEvent.fxml").show();
        }
    }

    @FXML
    private void goToAddEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddEvent.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof AddEvent) {
                ((AddEvent) controller).setDashboardController(dashboard);
            }

            Stage stage = new Stage();
            stage.setTitle("Ajouter un evenement");
            stage.setScene(new Scene(root));
            stage.show();

            stage.setOnHidden(e -> loadCards());

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir AddEvent.fxml").show();
        }
    }

    private void loadCards() {
        allEvents = serviceEvent.getAll();
        updateStatistics(allEvents);
        displayEvents(allEvents);
    }

    private void displayEvents(List<Event> events) {
        cardsContainer.getChildren().clear();
        lblCount.setText(events.size() + " evenement(s)");
        for (Event e : events) {
            cardsContainer.getChildren().add(createCard(e));
        }
    }
}