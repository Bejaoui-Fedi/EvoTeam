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
import tn.esprit.entities.Event;
import tn.esprit.services.ServiceEvent;

import java.io.IOException;
import java.util.List;

public class DisplayEvent {

    @FXML private FlowPane cardsContainer;
    @FXML private Label lblCount;

    private final ServiceEvent serviceEvent = new ServiceEvent();

    private Image imgEdit;
    private Image imgDelete;

    @FXML
    public void initialize() {
        // charger les images une fois
        imgEdit = new Image(getClass().getResource("/images/edit.png").toExternalForm());
        imgDelete = new Image(getClass().getResource("/images/delete.png").toExternalForm());

        loadCards();
    }

    @FXML
    private void refresh() {
        loadCards();
    }

    private void loadCards() {
        cardsContainer.getChildren().clear();

        List<Event> events = serviceEvent.getAll();
        lblCount.setText(events.size() + " événement(s)");

        for (Event e : events) {
            cardsContainer.getChildren().add(createCard(e));
        }
    }

    private VBox createCard(Event e) {

        // ====== Conteneur carte
        VBox card = new VBox(8);
        card.setPrefWidth(280);
        card.setStyle("""
                -fx-background-color: white;
                -fx-padding: 14;
                -fx-border-color: #E2E2E2;
                -fx-border-radius: 12;
                -fx-background-radius: 12;
                """);

        // ====== Ligne top : titre + actions
        Label title = new Label(e.getName());
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Button btnEdit = iconButton(imgEdit);
        Button btnDelete = iconButton(imgDelete);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox top = new HBox(10, title, spacer, btnEdit, btnDelete);
        top.setStyle("-fx-alignment: center-left;");

        // ====== infos
        Label dates = new Label("📅 " + e.getStartDate() + "  →  " + e.getEndDate());
        dates.setStyle("-fx-text-fill: #555555;");

        Label max = new Label("👥 Max: " + e.getMaxParticipants());
        max.setStyle("-fx-text-fill: #555555;");

        Label fee = new Label("💰 Fee: " + e.getFee());
        fee.setStyle("-fx-text-fill: #555555;");

        Label location = new Label("📍 " + e.getLocation());
        location.setStyle("-fx-text-fill: #555555;");

        Label desc = new Label(e.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #333333;");

        Separator sep = new Separator();

        // ====== Actions
        btnDelete.setOnAction(ev -> confirmAndDelete(e));
        btnEdit.setOnAction(ev -> openUpdateWindow(e));

        card.getChildren().addAll(top, sep, dates, max, fee, location, desc);
        return card;
    }


    @FXML
    private void goToDisplayReview() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/DisplayReview.fxml"));
            Stage stage = (Stage) /* n'importe quel node de la page */ cardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Reviews");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir DisplayReview.fxml").show();
        }
    }



    private Button iconButton(Image img) {
        ImageView iv = new ImageView(img);
        iv.setFitWidth(18);
        iv.setFitHeight(18);
        iv.setPreserveRatio(true);

        Button b = new Button();
        b.setGraphic(iv);
        b.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        return b;
    }

    private void confirmAndDelete(Event e) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Suppression");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer l'événement : " + e.getName() + " ?");

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                // IMPORTANT: ton service est delete(int)
                serviceEvent.delete(e.getEventId());
                loadCards();
                new Alert(Alert.AlertType.INFORMATION, "Supprimé ✅").show();
            }
        });
    }

    private void openUpdateWindow(Event selected) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateEvent.fxml"));
            Parent root = loader.load();

            UpdateEvent controller = loader.getController();
            controller.initData(selected);

            Stage stage = new Stage();
            stage.setTitle("Modifier Event");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d’ouvrir UpdateEvent.fxml").show();
        }
    }

    @FXML
    private void goToAddEvent() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AddEvent.fxml"));
            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Event");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d’ouvrir AddEvent.fxml").show();
        }
    }
}

