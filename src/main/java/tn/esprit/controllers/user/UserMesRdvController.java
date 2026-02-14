package tn.esprit.controllers.user;

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

public class UserMesRdvController {

    @FXML private TableView<Appointment> tableView;
    @FXML private TableColumn<Appointment, LocalDate> colDate;
    @FXML private TableColumn<Appointment, LocalTime> colHeure;
    @FXML private TableColumn<Appointment, String> colMotif;
    @FXML private TableColumn<Appointment, String> colType;
    @FXML private TableColumn<Appointment, String> colStatut;

    @FXML private Button modifierButton;
    @FXML private Button annulerButton;
    @FXML private Button supprimerButton;
    @FXML private Button nouveauRdvButton;

    @FXML private Label infoLabel;
    @FXML private Label compteurLabel;

    private AppointmentService service = new AppointmentService();
    private ObservableList<Appointment> rdvList = FXCollections.observableArrayList();
    private int currentUserId = 1;
    private Appointment selectedAppointment;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupCellFactories();
        loadMesRendezVous();
        setupSelectionListener();
    }

    private void setupTableColumns() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateRdv"));
        colHeure.setCellValueFactory(new PropertyValueFactory<>("heureRdv"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeRdv"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    private void setupCellFactories() {
        colDate.setCellFactory(column -> new TableCell<Appointment, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(date.toString());
                    if (date.isBefore(LocalDate.now())) {
                        setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
                    } else if (date.isEqual(LocalDate.now())) {
                        setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #2c3e50;");
                    }
                }
            }
        });

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

    private void loadMesRendezVous() {
        try {
            rdvList.clear();
            rdvList.addAll(service.getByUserId(currentUserId));
            tableView.setItems(rdvList);
            updateStatistics();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger vos rendez-vous : " + e.getMessage());
        }
    }

    private void updateStatistics() {
        int total = rdvList.size();

        long aVenir = rdvList.stream()
                .filter(a -> a.getDateRdv().isAfter(LocalDate.now()) ||
                        (a.getDateRdv().isEqual(LocalDate.now()) &&
                                a.getHeureRdv().isAfter(LocalTime.now())))
                .count();

        long confirmes = rdvList.stream()
                .filter(a -> "confirmé".equals(a.getStatut()))
                .count();

        long enAttente = rdvList.stream()
                .filter(a -> "en attente".equals(a.getStatut()))
                .count();

        long termines = rdvList.stream()
                .filter(a -> "terminé".equals(a.getStatut()))
                .count();

        long annules = rdvList.stream()
                .filter(a -> "annulé".equals(a.getStatut()))
                .count();

        if (total == 0) {
            infoLabel.setText("📋 Vous n'avez aucun rendez-vous.");
            compteurLabel.setText("👋 Prenez votre premier rendez-vous !");
        } else {
            infoLabel.setText(String.format("📋 Vous avez %d rendez-vous", total));
            compteurLabel.setText(String.format(
                    "✅ Confirmés: %d | ⏳ En attente: %d | ✅ Terminés: %d | ❌ Annulés: %d | 📅 À venir: %d",
                    confirmes, enAttente, termines, annules, aVenir));
        }
    }

    private void setupSelectionListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, old, newSel) -> {
            selectedAppointment = newSel;

            if (newSel != null) {
                updateButtonStates(newSel);
            } else {
                disableAllButtons();
            }
        });
    }

    private void updateButtonStates(Appointment a) {
        String statut = a.getStatut();
        LocalDate date = a.getDateRdv();
        LocalTime heure = a.getHeureRdv();
        LocalDate aujourdHui = LocalDate.now();
        LocalTime maintenant = LocalTime.now();

        boolean peutModifier = "en attente".equals(statut) &&
                (date.isAfter(aujourdHui) || (date.isEqual(aujourdHui) && heure.isAfter(maintenant)));

        boolean peutAnnuler = ("en attente".equals(statut) || "confirmé".equals(statut)) &&
                (date.isAfter(aujourdHui) || (date.isEqual(aujourdHui) && heure.isAfter(maintenant)));

        boolean peutSupprimer = "annulé".equals(statut) || "terminé".equals(statut) ||
                date.isBefore(aujourdHui) || (date.isEqual(aujourdHui) && heure.isBefore(maintenant));

        modifierButton.setDisable(!peutModifier);
        annulerButton.setDisable(!peutAnnuler);
        supprimerButton.setDisable(!peutSupprimer);

        if (!peutModifier) {
            Tooltip.install(modifierButton, new Tooltip(
                    "Modification impossible :\n- Le rendez-vous doit être en attente\n- La date ne doit pas être passée"));
        } else {
            Tooltip.uninstall(modifierButton, null);
        }

        if (!peutAnnuler) {
            Tooltip.install(annulerButton, new Tooltip(
                    "Annulation impossible :\n- Le rendez-vous doit être en attente ou confirmé\n- La date ne doit pas être passée"));
        } else {
            Tooltip.uninstall(annulerButton, null);
        }
    }

    private void disableAllButtons() {
        modifierButton.setDisable(true);
        annulerButton.setDisable(true);
        supprimerButton.setDisable(true);
    }

    @FXML
    private void modifierRendezVous() {
        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner un rendez-vous à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/modifier_rdv_dialog.fxml"));

            if (loader.getLocation() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Fichier FXML introuvable : /fxml/user/modifier_rdv_dialog.fxml");
                return;
            }

            Parent root = loader.load();

            ModifierRdvDialogController controller = loader.getController();
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
                loadMesRendezVous();
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur technique",
                    "Impossible d'ouvrir la fenêtre de modification.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annulerRendezVous() {
        if (selectedAppointment == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation d'annulation");
        confirm.setHeaderText("Annuler le rendez-vous ?");
        confirm.setContentText(String.format(
                "Voulez-vous vraiment annuler le rendez-vous suivant ?\n\n" +
                        "📅 Date : %s\n" +
                        "⏰ Heure : %s\n" +
                        "📝 Motif : %s\n" +
                        "🖥️ Type : %s\n\n" +
                        "⚠️ Cette action est réversible.",
                selectedAppointment.getDateRdv(),
                selectedAppointment.getHeureRdv(),
                selectedAppointment.getMotif(),
                selectedAppointment.getTypeRdv()
        ));

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                int userId = selectedAppointment.getUserId();
                selectedAppointment.setStatut("annulé");
                selectedAppointment.setUserId(userId);

                service.update(selectedAppointment);
                loadMesRendezVous();

                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "✅ Votre rendez-vous a été annulé avec succès.");

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Impossible d'annuler le rendez-vous : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void supprimerRendezVous() {
        if (selectedAppointment == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer définitivement le rendez-vous ?");
        confirm.setContentText(String.format(
                "‼️ ATTENTION ‼️\n\n" +
                        "Vous êtes sur le point de supprimer DÉFINITIVEMENT ce rendez-vous :\n\n" +
                        "📅 Date : %s %s\n" +
                        "📝 Motif : %s\n" +
                        "📊 Statut : %s\n\n" +
                        "⚠️ Cette action est IRREVERSIBLE !",
                selectedAppointment.getDateRdv(),
                selectedAppointment.getHeureRdv(),
                selectedAppointment.getMotif(),
                selectedAppointment.getStatut()
        ));

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                service.delete(selectedAppointment);
                loadMesRendezVous();

                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "🗑️ Le rendez-vous a été supprimé définitivement.");

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Impossible de supprimer le rendez-vous : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void nouveauRendezVous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/user_prendre_rdv.fxml"));
            Parent root = loader.load();

            UserPrendreRdvController controller = loader.getController();
            controller.setUserId(currentUserId);

            Stage stage = (Stage) nouveauRdvButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Prendre un rendez-vous");

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir le formulaire de rendez-vous");
            e.printStackTrace();
        }
    }

    @FXML
    private void refresh() {
        loadMesRendezVous();
        showAlert(Alert.AlertType.INFORMATION, "Actualisation",
                "✅ La liste des rendez-vous a été actualisée.");
    }

    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadMesRendezVous();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}