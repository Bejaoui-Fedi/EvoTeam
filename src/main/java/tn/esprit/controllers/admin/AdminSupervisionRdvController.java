package tn.esprit.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import java.sql.SQLException;
import tn.esprit.entities.Appointment;
import tn.esprit.services.AppointmentService;

import java.io.IOException;
import java.time.LocalDate;

public class AdminSupervisionRdvController {

    @FXML
    private TextField searchField;
    @FXML
    private DatePicker dateFilterPicker;
    @FXML
    private ChoiceBox<String> statutFilterChoice;

    @FXML
    private FlowPane cardsContainer;
    private Appointment selectedAppointment;

    @FXML
    private Button deleteButton;
    @FXML
    private Button consultationsButton;
    @FXML
    private Label statsLabel;
    @FXML
    private Label totalLabel;

    private AppointmentService service = new AppointmentService();
    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupFilters();
        setupSearch();
        loadAllAppointments();
    }

    private void renderCards(ObservableList<Appointment> appointments) {
        cardsContainer.getChildren().clear();
        for (Appointment app : appointments) {
            VBox card = createAdminAppointmentCard(app);
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createAdminAppointmentCard(Appointment app) {
        VBox card = new VBox(10);
        card.getStyleClass().add("item-card");
        card.setPrefWidth(300);

        // Header: ID and Status
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label idLabel = new Label("RDV #" + app.getId());
        idLabel.getStyleClass().add("card-id");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = new Label(app.getStatut().toUpperCase());
        statusBadge.getStyleClass().add("card-badge");
        applyStatusStyle(statusBadge, app.getStatut());

        header.getChildren().addAll(idLabel, spacer, statusBadge);

        // Content: Patient and Motif
        Label patientLabel = new Label("👤 Patient ID: " + app.getUserId());
        patientLabel.getStyleClass().add("card-title");

        Label motifLabel = new Label("📝 " + app.getMotif());
        motifLabel.setWrapText(true);
        motifLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #555;");

        // Info: Date and Time
        HBox info = new HBox(15);
        info.getChildren().addAll(
                new Label("📅 " + app.getDateRdv()),
                new Label("⏰ " + app.getHeureRdv()));

        Region separator = new Region();
        separator.getStyleClass().add("card-separator");
        separator.setPrefHeight(1);

        // Selection handling
        card.setOnMouseClicked(e -> {
            for (javafx.scene.Node n : cardsContainer.getChildren()) {
                n.setStyle("");
            }
            card.setStyle("-fx-border-color: #396f5b; -fx-border-width: 3; -fx-border-radius: 12;");
            selectedAppointment = app;
            deleteButton.setDisable(false);
        });

        card.getChildren().addAll(header, patientLabel, motifLabel, info, separator);
        return card;
    }

    private void applyStatusStyle(Label label, String statut) {
        switch (statut) {
            case "confirmé":
                label.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724;");
                break;
            case "annulé":
                label.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24;");
                break;
            case "terminé":
                label.setStyle("-fx-background-color: #cce5ff; -fx-text-fill: #004085;");
                break;
            default:
                label.setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
        }
    }

    private void setupFilters() {
        dateFilterPicker.valueProperty().addListener((obs, old, newVal) -> applyFilters());

        statutFilterChoice.setItems(FXCollections.observableArrayList(
                "Tous", "en attente", "confirmé", "terminé", "annulé"));
        statutFilterChoice.setValue("Tous");
        statutFilterChoice.valueProperty().addListener((obs, old, newVal) -> applyFilters());
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, old, newVal) -> applyFilters());
    }

    private void loadAllAppointments() {
        try {
            allAppointments.clear();
            allAppointments.addAll(service.getAll());
            applyFilters();
            updateStatistics();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger les rendez-vous : " + e.getMessage());
        }
    }

    private void applyFilters() {
        filteredAppointments.clear();
        String searchText = searchField.getText().toLowerCase();
        LocalDate filterDate = dateFilterPicker.getValue();
        String filterStatut = statutFilterChoice.getValue();

        for (Appointment a : allAppointments) {
            boolean matchSearch = searchText.isEmpty()
                    || String.valueOf(a.getId()).contains(searchText)
                    || a.getMotif().toLowerCase().contains(searchText)
                    || a.getStatut().toLowerCase().contains(searchText)
                    || a.getTypeRdv().toLowerCase().contains(searchText)
                    || String.valueOf(a.getUserId()).contains(searchText);

            boolean matchDate = (filterDate == null) || a.getDateRdv().equals(filterDate);
            boolean matchStatut = (filterStatut == null || filterStatut.equals("Tous"))
                    || a.getStatut().equals(filterStatut);

            if (matchSearch && matchDate && matchStatut) {
                filteredAppointments.add(a);
            }
        }

        renderCards(filteredAppointments);
        updateStatistics();
    }

    private void updateStatistics() {
        int total = allAppointments.size();
        int affiches = filteredAppointments.size();
        long aujourdHui = allAppointments.stream()
                .filter(a -> a.getDateRdv().equals(LocalDate.now()))
                .count();
        long enAttente = allAppointments.stream()
                .filter(a -> "en attente".equals(a.getStatut()))
                .count();
        long annules = allAppointments.stream()
                .filter(a -> "annulé".equals(a.getStatut()))
                .count();

        totalLabel.setText(String.format(
                "📊 Total: %d rendez-vous | Affichés: %d",
                total, affiches));

        statsLabel.setText(String.format(
                "📅 Aujourd'hui: %d | ⏳ En attente: %d | ❌ Annulés: %d",
                aujourdHui, enAttente, annules));
    }

    @FXML
    private void deleteRendezVous() {
        if (selectedAppointment == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer le rendez-vous ?");
        confirm.setContentText("Cette action est irréversible !");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                service.delete(selectedAppointment);
                loadAllAppointments();
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Rendez-vous #" + selectedAppointment.getId() + " supprimé avec succès");
                selectedAppointment = null;
                deleteButton.setDisable(true);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Échec de la suppression : " + e.getMessage());
            }
        }
    }

    @FXML
    private void gererConsultations() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/admin/admin_supervision_consultation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) consultationsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Supervision des consultations");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la supervision des consultations: " + e.getMessage());
        }
    }

    @FXML
    private void showStatistics() {
        try {
            java.net.URL url = getClass().getResource("/fxml/statistics_dashboard.fxml");
            if (url == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Fichier FXML introuvable : /fxml/statistics_dashboard.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Tableau de bord des Statistiques (Admin)");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le tableau de bord : " + e.getMessage());
        }
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        dateFilterPicker.setValue(null);
        statutFilterChoice.setValue("Tous");
    }

    @FXML
    private void refresh() {
        loadAllAppointments();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}