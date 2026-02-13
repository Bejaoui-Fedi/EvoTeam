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

public class DisplayEvent {

    @FXML private FlowPane cardsContainer;
    @FXML private Label lblCount;
    @FXML private TextField searchField;

    private ServiceEvent serviceEvent = new ServiceEvent();
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

    // =====================================================
    // CORRIGE : Navigation vers DisplayReview via le dashboard
    // au lieu de remplacer toute la scene
    // =====================================================
    @FXML
    private void goToDisplayReview(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayReview.fxml"));
            Parent root = loader.load();

            // Passer la reference du dashboard au DisplayReview
            DisplayReview controller = loader.getController();
            controller.setDashboardController(dashboard);

            if (dashboard != null) {
                // Charger dans le contentArea du dashboard (sidebar reste visible)
                dashboard.setContent(root);
            } else {
                // Fallback si pas de dashboard (ouverture standalone)
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

    private VBox createCard(Event e) {
        VBox card = new VBox(12);
        card.setPrefWidth(300);
        card.getStyleClass().add("event-card");

        Label title = new Label("🎉 " + e.getName());
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

        Button btnEdit = new Button("✏️");
        btnEdit.setStyle("-fx-background-color: #3A7DFF; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnEdit.setOnAction(ev -> openUpdateWindow(e));

        Button btnDelete = new Button("🗑");
        btnDelete.setStyle("-fx-background-color: #FF3A3A; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnDelete.setOnAction(ev -> confirmAndDelete(e));

        HBox header = new HBox(10, title, spacer, badge, btnEdit, btnDelete);
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

    private Button iconButton(Image img) {
        ImageView iv = new ImageView(img);
        iv.setFitWidth(18);
        iv.setFitHeight(18);
        Button b = new Button();
        b.setGraphic(iv);
        b.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        return b;
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
        cardsContainer.getChildren().clear();
        List<Event> events = serviceEvent.getAll();
        lblCount.setText(events.size() + " evenement(s)");
        for (Event e : events) {
            cardsContainer.getChildren().add(createCard(e));
        }
    }
}
