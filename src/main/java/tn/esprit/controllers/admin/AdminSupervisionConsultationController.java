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
import tn.esprit.entities.Consultation;
import tn.esprit.services.ConsultationService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AdminSupervisionConsultationController {

    @FXML private TextField searchField;
    @FXML private DatePicker dateFilterPicker;
    @FXML private ChoiceBox<String> statutFilterChoice;

    @FXML private TableView<Consultation> tableView;
    @FXML private TableColumn<Consultation, Integer> colId;
    @FXML private TableColumn<Consultation, Integer> colAppointmentId;
    @FXML private TableColumn<Consultation, LocalDate> colDate;
    @FXML private TableColumn<Consultation, String> colDiagnostic;
    @FXML private TableColumn<Consultation, String> colObservation;
    @FXML private TableColumn<Consultation, String> colTraitement;
    @FXML private TableColumn<Consultation, String> colOrdonnance;
    @FXML private TableColumn<Consultation, Integer> colDuree;
    @FXML private TableColumn<Consultation, String> colStatut;

    @FXML private Button deleteButton;
    @FXML private Button retourButton;
    @FXML private Label statsLabel;
    @FXML private Label totalLabel;

    private ConsultationService service = new ConsultationService();
    private ObservableList<Consultation> allConsultations = FXCollections.observableArrayList();
    private ObservableList<Consultation> filteredConsultations = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilters();
        setupSearch();
        loadAllConsultations();
        setupSelectionListener();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAppointmentId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateConsultation"));
        colDiagnostic.setCellValueFactory(new PropertyValueFactory<>("diagnostic"));
        colObservation.setCellValueFactory(new PropertyValueFactory<>("observation"));
        colTraitement.setCellValueFactory(new PropertyValueFactory<>("traitement"));
        colOrdonnance.setCellValueFactory(new PropertyValueFactory<>("ordonnance"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("duree"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statutConsultation"));

        colDate.setCellFactory(column -> new TableCell<Consultation, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.toString());
                }
            }
        });

        colStatut.setCellFactory(column -> new TableCell<Consultation, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statut);
                    if ("en cours".equals(statut)) {
                        setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
                    } else {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724;");
                    }
                }
            }
        });
    }

    private void setupFilters() {
        dateFilterPicker.valueProperty().addListener((obs, old, newVal) -> applyFilters());

        statutFilterChoice.setItems(FXCollections.observableArrayList(
                "Tous", "en cours", "clôturée"
        ));
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
                    || (c.getStatutConsultation() != null && c.getStatutConsultation().toLowerCase().contains(searchText));

            boolean matchDate = (filterDate == null) ||
                    (c.getDateConsultation() != null && c.getDateConsultation().equals(filterDate));
            boolean matchStatut = (filterStatut == null || filterStatut.equals("Tous"))
                    || (c.getStatutConsultation() != null &&
                    c.getStatutConsultation().equals(filterStatut));

            if (matchSearch && matchDate && matchStatut) {
                filteredConsultations.add(c);
            }
        }

        tableView.setItems(filteredConsultations);
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
                total, affiches
        ));

        statsLabel.setText(String.format(
                "⏳ En cours: %d | ✅ Clôturées: %d",
                enCours, cloturees
        ));
    }

    private void setupSelectionListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, old, newSel) -> {
            deleteButton.setDisable(newSel == null);
        });
    }

    @FXML
    private void deleteConsultation() {
        Consultation selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer la consultation ?");
        confirm.setContentText("Cette action est irréversible !");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                service.delete(selected);
                loadAllConsultations();
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Consultation #" + selected.getId() + " supprimée avec succès");
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