package tn.esprit.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import tn.esprit.entities.Consultation;
import tn.esprit.services.ConsultationService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AdminSupervisionConsultationController {

    @FXML
    private TextField searchField;
    @FXML
    private DatePicker dateFilterPicker;
    @FXML
    private ChoiceBox<String> statutFilterChoice;

    @FXML
    private FlowPane cardsContainer;
    private Consultation selectedConsultation;

    @FXML
    private Button deleteButton;
    @FXML
    private Button retourButton;
    @FXML
    private Label statsLabel;
    @FXML
    private Label totalLabel;

    private ConsultationService service = new ConsultationService();
    private ObservableList<Consultation> allConsultations = FXCollections.observableArrayList();
    private ObservableList<Consultation> filteredConsultations = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupFilters();
        setupSearch();
        loadAllConsultations();
    }

    private void renderCards(ObservableList<Consultation> consultations) {
        cardsContainer.getChildren().clear();
        for (Consultation cons : consultations) {
            VBox card = createAdminConsultationCard(cons);
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createAdminConsultationCard(Consultation cons) {
        VBox card = new VBox(10);
        card.getStyleClass().add("item-card");
        card.setPrefWidth(300);

        // Header: ID and Status
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label idLabel = new Label("CONS #" + cons.getId());
        idLabel.getStyleClass().add("card-id");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = new Label(cons.getStatutConsultation().toUpperCase());
        statusBadge.getStyleClass().add("card-badge");
        applyStatusStyle(statusBadge, cons.getStatutConsultation());

        header.getChildren().addAll(idLabel, spacer, statusBadge);

        // Content: RDV ID and Diagnostic
        Label rdvLabel = new Label("📅 RDV #" + cons.getAppointmentId());
        rdvLabel.getStyleClass().add("card-title");

        Label diagLabel = new Label("🩺 " + cons.getDiagnostic());
        diagLabel.setWrapText(true);
        diagLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        // Observations and Treatment
        Label obsLabel = new Label("📝 " + cons.getObservation());
        obsLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        obsLabel.setWrapText(true);

        HBox info = new HBox(15);
        info.getChildren().addAll(
                new Label("📅 " + cons.getDateConsultation()),
                new Label("⏱️ " + cons.getDuree() + " min"));
        info.setStyle("-fx-font-size: 12; -fx-text-fill: #555;");

        Region separator = new Region();
        separator.getStyleClass().add("card-separator");
        separator.setPrefHeight(1);

        // Selection handling
        card.setOnMouseClicked(e -> {
            for (javafx.scene.Node n : cardsContainer.getChildren()) {
                n.setStyle("");
            }
            card.setStyle("-fx-border-color: #396f5b; -fx-border-width: 3; -fx-border-radius: 12;");
            selectedConsultation = cons;
            deleteButton.setDisable(false);
        });

        card.getChildren().addAll(header, rdvLabel, diagLabel, obsLabel, info, separator);
        return card;
    }

    private void applyStatusStyle(Label label, String statut) {
        if ("en cours".equals(statut)) {
            label.setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
        } else {
            label.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724;");
        }
    }

    private void setupFilters() {
        dateFilterPicker.valueProperty().addListener((obs, old, newVal) -> applyFilters());

        statutFilterChoice.setItems(FXCollections.observableArrayList(
                "Tous", "en cours", "clôturée"));
        statutFilterChoice.setValue("Tous");
        statutFilterChoice.valueProperty().addListener((obs, old, newVal) -> applyFilters());
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, old, newVal) -> applyFilters());
    }

    private void loadAllConsultations() {
        try {
            allConsultations.clear();
            allConsultations.addAll(service.getAll());
            applyFilters();
            updateStatistics();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger les consultations : " + e.getMessage());
        }
    }

    private void applyFilters() {
        filteredConsultations.clear();
        String searchText = searchField.getText().toLowerCase();
        LocalDate filterDate = dateFilterPicker.getValue();
        String filterStatut = statutFilterChoice.getValue();

        for (Consultation c : allConsultations) {
            boolean matchSearch = searchText.isEmpty()
                    || String.valueOf(c.getId()).contains(searchText)
                    || String.valueOf(c.getAppointmentId()).contains(searchText)
                    || (c.getDiagnostic() != null && c.getDiagnostic().toLowerCase().contains(searchText))
                    || (c.getStatutConsultation() != null
                            && c.getStatutConsultation().toLowerCase().contains(searchText));

            boolean matchDate = (filterDate == null) ||
                    (c.getDateConsultation() != null && c.getDateConsultation().equals(filterDate));
            boolean matchStatut = (filterStatut == null || filterStatut.equals("Tous"))
                    || (c.getStatutConsultation() != null &&
                            c.getStatutConsultation().equals(filterStatut));

            if (matchSearch && matchDate && matchStatut) {
                filteredConsultations.add(c);
            }
        }

        renderCards(filteredConsultations);
        updateStatistics();
    }

    private void updateStatistics() {
        int total = allConsultations.size();
        int affiches = filteredConsultations.size();
        long enCours = allConsultations.stream()
                .filter(c -> "en cours".equals(c.getStatutConsultation()))
                .count();
        long cloturees = allConsultations.stream()
                .filter(c -> "clôturée".equals(c.getStatutConsultation()))
                .count();

        totalLabel.setText(String.format(
                "📊 Total: %d consultations | Affichées: %d",
                total, affiches));

        statsLabel.setText(String.format(
                "⏳ En cours: %d | ✅ Clôturées: %d",
                enCours, cloturees));
    }

    @FXML
    private void deleteConsultation() {
        if (selectedConsultation == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer la consultation ?");
        confirm.setContentText("Cette action est irréversible !");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                service.delete(selectedConsultation);
                loadAllConsultations();
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Consultation #" + selectedConsultation.getId() + " supprimée avec succès");
                selectedConsultation = null;
                deleteButton.setDisable(true);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Échec de la suppression : " + e.getMessage());
            }
        }
    }

    @FXML
    private void retourSupervisionRdv() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/admin_supervision_rdv.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retourButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Supervision des rendez-vous");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de retourner à la supervision des rendez-vous: " + e.getMessage());
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
        loadAllConsultations();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}