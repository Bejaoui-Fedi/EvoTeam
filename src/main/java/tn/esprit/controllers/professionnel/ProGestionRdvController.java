package tn.esprit.controllers.professionnel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Modality;
import tn.esprit.entities.Appointment;
import tn.esprit.services.AppointmentService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ProGestionRdvController {

    @FXML private DatePicker dateFilterPicker;
    @FXML private ChoiceBox<String> statutFilterChoice;

    @FXML private TableView<Appointment> tableView;
    @FXML private TableColumn<Appointment, Integer> colId;
    @FXML private TableColumn<Appointment, LocalDate> colDate;
    @FXML private TableColumn<Appointment, LocalTime> colHeure;
    @FXML private TableColumn<Appointment, String> colMotif;
    @FXML private TableColumn<Appointment, String> colType;
    @FXML private TableColumn<Appointment, String> colStatut;
    @FXML private TableColumn<Appointment, Integer> colUserId;

    @FXML private Button modifierButton;
    @FXML private Button supprimerButton;
    @FXML private Button updateStatusButton;
    @FXML private Button consultationsButton;
    @FXML private ChoiceBox<String> statutUpdateChoice;
    @FXML private Label totalLabel;

    private AppointmentService service = new AppointmentService();
    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
    private Appointment selectedAppointment;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilters();
        setupChoiceBoxes();
        loadAllAppointments();
        setupSelectionListener();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateRdv"));
        colHeure.setCellValueFactory(new PropertyValueFactory<>("heureRdv"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeRdv"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
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
                            setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        case "annulé":
                            setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        case "terminé":
                            setStyle("-fx-background-color: #cce5ff; -fx-text-fill: #004085; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        default:
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    }
                }
            }
        });
    }

    private void setupFilters() {
        dateFilterPicker.setValue(LocalDate.now());
        dateFilterPicker.valueProperty().addListener((obs, old, newVal) -> applyFilters());

        statutFilterChoice.setItems(FXCollections.observableArrayList(
                "Tous", "en attente", "confirmé", "terminé", "annulé"
        ));
        statutFilterChoice.setValue("Tous");
        statutFilterChoice.valueProperty().addListener((obs, old, newVal) -> applyFilters());
    }

    private void setupChoiceBoxes() {
        statutUpdateChoice.setItems(FXCollections.observableArrayList("confirmé", "terminé", "annulé"));
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
        LocalDate filterDate = dateFilterPicker.getValue();
        String filterStatut = statutFilterChoice.getValue();

        for (Appointment a : allAppointments) {
            boolean matchDate = (filterDate == null) || a.getDateRdv().equals(filterDate);
            boolean matchStatut = (filterStatut == null || filterStatut.equals("Tous"))
                    || a.getStatut().equals(filterStatut);

            if (matchDate && matchStatut) {
                filteredAppointments.add(a);
            }
        }

        tableView.setItems(filteredAppointments);
    }

    private void updateStatistics() {
        int total = allAppointments.size();
        long aujourdHui = allAppointments.stream()
                .filter(a -> a.getDateRdv().equals(LocalDate.now()))
                .count();
        long enAttente = allAppointments.stream()
                .filter(a -> "en attente".equals(a.getStatut()))
                .count();

        totalLabel.setText(String.format(
                "📊 Total: %d RDV | Aujourd'hui: %d | En attente: %d",
                total, aujourdHui, enAttente
        ));
    }

    private void setupSelectionListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, old, newSel) -> {
            selectedAppointment = newSel;
            if (newSel != null) {
                statutUpdateChoice.setValue(newSel.getStatut());
                modifierButton.setDisable(false);
                supprimerButton.setDisable(false);
                updateStatusButton.setDisable(false);
            } else {
                modifierButton.setDisable(true);
                supprimerButton.setDisable(true);
                updateStatusButton.setDisable(true);
            }
        });
    }

    @FXML
    private void modifierRendezVous() {
        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner un rendez-vous à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/professionnel/modifier_rdv_pro_dialog.fxml"));

            if (loader.getLocation() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Fichier FXML introuvable : /fxml/professionnel/modifier_rdv_pro_dialog.fxml");
                return;
            }

            Parent root = loader.load();

            ModifierRdvProDialogController controller = loader.getController();
            controller.setAppointment(selectedAppointment);

            Stage stage = new Stage();
            controller.setStage(stage);

            stage.setTitle("Modifier le rendez-vous");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(modifierButton.getScene().getWindow());
            stage.setResizable(false);

            stage.showAndWait();

            if (controller.isModificationReussie()) {
                loadAllAppointments();
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur technique",
                    "Impossible d'ouvrir la fenêtre de modification.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerRendezVous() {
        if (selectedAppointment == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer le rendez-vous ?");
        confirm.setContentText(String.format(
                "‼️ ATTENTION ‼️\n\n" +
                        "Vous êtes sur le point de supprimer DÉFINITIVEMENT ce rendez-vous :\n\n" +
                        "📋 RDV #%d\n" +
                        "👤 Patient ID: %d\n" +
                        "📅 Date : %s %s\n" +
                        "📝 Motif : %s\n\n" +
                        "⚠️ Cette action est IRREVERSIBLE !",
                selectedAppointment.getId(),
                selectedAppointment.getUserId(),
                selectedAppointment.getDateRdv(),
                selectedAppointment.getHeureRdv(),
                selectedAppointment.getMotif()
        ));

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                service.delete(selectedAppointment);
                loadAllAppointments();
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "🗑️ Rendez-vous supprimé avec succès");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Échec de la suppression : " + e.getMessage());
            }
        }
    }

    @FXML
    private void updateStatut() {
        if (selectedAppointment == null || statutUpdateChoice.getValue() == null) return;

        String nouveauStatut = statutUpdateChoice.getValue();
        String ancienStatut = selectedAppointment.getStatut();

        if (nouveauStatut.equals(ancienStatut)) {
            showAlert(Alert.AlertType.INFORMATION, "Information",
                    "Le statut est déjà '" + nouveauStatut + "'");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Modifier le statut du rendez-vous ?");
        confirm.setContentText(String.format(
                "RDV #%d: %s → %s",
                selectedAppointment.getId(),
                ancienStatut,
                nouveauStatut
        ));

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                selectedAppointment.setStatut(nouveauStatut);
                service.update(selectedAppointment);
                loadAllAppointments();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Statut mis à jour avec succès");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour : " + e.getMessage());
            }
        }
    }

    @FXML
    private void gererConsultations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/professionnel/pro_gestion_consultation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) consultationsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des consultations");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la gestion des consultations: " + e.getMessage());
        }
    }

    @FXML
    private void resetFilters() {
        dateFilterPicker.setValue(LocalDate.now());
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