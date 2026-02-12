package tn.esprit.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.entities.Appointment;
import tn.esprit.services.AppointmentService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class AdminSupervisionRdvController {

    @FXML private TextField searchField;
    @FXML private DatePicker dateFilterPicker;
    @FXML private ChoiceBox<String> statutFilterChoice;

    @FXML private TableView<Appointment> tableView;
    @FXML private TableColumn<Appointment, Integer> colId;
    @FXML private TableColumn<Appointment, LocalDate> colDate;
    @FXML private TableColumn<Appointment, LocalTime> colHeure;
    @FXML private TableColumn<Appointment, String> colStatut;
    @FXML private TableColumn<Appointment, String> colMotif;
    @FXML private TableColumn<Appointment, String> colType;
    @FXML private TableColumn<Appointment, Integer> colUserId;

    @FXML private Button deleteButton;
    @FXML private Button consultationsButton;
    @FXML private Label statsLabel;
    @FXML private Label totalLabel;

    private AppointmentService service = new AppointmentService();
    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilters();
        setupSearch();
        loadAllAppointments();
        setupSelectionListener();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateRdv"));
        colHeure.setCellValueFactory(new PropertyValueFactory<>("heureRdv"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeRdv"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));

        // Colorer le statut
        colStatut.setCellFactory(column -> new TableCell<Appointment, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statut);
                    switch (statut) {
                        case "confirmé":
                            setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724;");
                            break;
                        case "annulé":
                            setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24;");
                            break;
                        case "terminé":
                            setStyle("-fx-background-color: #cce5ff; -fx-text-fill: #004085;");
                            break;
                        default:
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
                    }
                }
            }
        });
    }

    private void setupFilters() {
        dateFilterPicker.valueProperty().addListener((obs, old, newVal) -> applyFilters());

        statutFilterChoice.setItems(FXCollections.observableArrayList(
                "Tous", "en attente", "confirmé", "terminé", "annulé"
        ));
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

        tableView.setItems(filteredAppointments);
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
                total, affiches
        ));

        statsLabel.setText(String.format(
                "📅 Aujourd'hui: %d | ⏳ En attente: %d | ❌ Annulés: %d",
                aujourdHui, enAttente, annules
        ));
    }

    private void setupSelectionListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, old, newSel) -> {
            deleteButton.setDisable(newSel == null);
        });
    }

    // ================ CRUD OPERATIONS ================

    @FXML
    private void deleteRendezVous() {
        Appointment selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer le rendez-vous ?");
        confirm.setContentText(String.format(
                "ID: %d\nDate: %s %s\nPatient ID: %d\nMotif: %s\n\nCette action est irréversible !",
                selected.getId(),
                selected.getDateRdv(),
                selected.getHeureRdv(),
                selected.getUserId(),
                selected.getMotif()
        ));

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                service.delete(selected);
                loadAllAppointments();
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Rendez-vous #" + selected.getId() + " supprimé avec succès");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Échec de la suppression : " + e.getMessage());
            }
        }
    }

    @FXML
    private void gererConsultations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/admin_supervision_consultation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) consultationsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Supervision des consultations");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la supervision des consultations");
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