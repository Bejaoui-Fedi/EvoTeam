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

    // Formulaire de modification
    @FXML private DatePicker modifierDatePicker;
    @FXML private ComboBox<String> modifierHeureCombo;
    @FXML private TextField modifierMotifField;
    @FXML private ChoiceBox<String> modifierTypeChoice;

    @FXML private Button modifierButton;
    @FXML private Button annulerButton;
    @FXML private Button supprimerButton;
    @FXML private Button nouveauRdvButton;
    @FXML private Button validerModificationButton;
    @FXML private Button annulerModificationButton;

    @FXML private Label infoLabel;
    @FXML private Label compteurLabel;
    @FXML private TitledPane modificationPane;

    private AppointmentService service = new AppointmentService();
    private ObservableList<Appointment> rdvList = FXCollections.observableArrayList();
    private int currentUserId = 1; // Simulation
    private Appointment selectedAppointment;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupCellFactories();
        setupModificationForm();
        loadMesRendezVous();
        setupSelectionListener();

        // Initialiser le panneau de modification
        modificationPane.setExpanded(false);
        modificationPane.setVisible(false);

        // ✅ FORCER L'ACTIVATION DES CHAMDS DÈS LE DÉPART
        modifierDatePicker.setDisable(false);
        modifierHeureCombo.setDisable(false);
        modifierMotifField.setDisable(false);
        modifierTypeChoice.setDisable(false);
        validerModificationButton.setDisable(false);

        // Rendre les champs éditables
        modifierDatePicker.setEditable(true);
        modifierHeureCombo.setEditable(true);
        modifierMotifField.setEditable(true);
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
                } else {
                    setText(date.toString());
                    if (date.isBefore(LocalDate.now())) {
                        setStyle("-fx-text-fill: #7f8c8d;");
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
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                            break;
                        case "annulé":
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        case "terminé":
                            setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupModificationForm() {
        // Créneaux disponibles (9h-17h)
        ObservableList<String> creneaux = FXCollections.observableArrayList();
        for (int i = 9; i <= 17; i++) {
            creneaux.add(String.format("%02d:00", i));
            creneaux.add(String.format("%02d:30", i));
        }
        modifierHeureCombo.setItems(creneaux);

        // Type de rendez-vous
        modifierTypeChoice.setItems(FXCollections.observableArrayList(
                "Présentiel", "Téléconsultation"
        ));

        // Date minimale = aujourd'hui
        modifierDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        // S'assurer que les champs sont éditables
        modifierDatePicker.setEditable(true);
        modifierHeureCombo.setEditable(true);
        modifierMotifField.setEditable(true);
    }

    private void loadMesRendezVous() {
        try {
            rdvList.clear();
            for (Appointment a : service.getAll()) {
                if (a.getUserId() == currentUserId) {
                    rdvList.add(a);
                }
            }
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
                .filter(a -> a.getDateRdv().isAfter(LocalDate.now())
                        || (a.getDateRdv().isEqual(LocalDate.now())
                        && a.getHeureRdv().isAfter(LocalTime.now())))
                .count();
        long confirmes = rdvList.stream()
                .filter(a -> "confirmé".equals(a.getStatut()))
                .count();
        long enAttente = rdvList.stream()
                .filter(a -> "en attente".equals(a.getStatut()))
                .count();

        if (total == 0) {
            infoLabel.setText("📋 Vous n'avez aucun rendez-vous.");
            compteurLabel.setText("");
        } else {
            infoLabel.setText(String.format("📋 Vous avez %d rendez-vous", total));
            compteurLabel.setText(String.format("✅ %d confirmés | ⏳ %d en attente | 📅 %d à venir",
                    confirmes, enAttente, aVenir));
        }
    }

    private void setupSelectionListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, old, newSel) -> {
            selectedAppointment = newSel;
            if (newSel != null) {
                String statut = newSel.getStatut();
                LocalDate date = newSel.getDateRdv();
                LocalTime heure = newSel.getHeureRdv();

                // Peut MODIFIER si : statut = "en attente" ET date pas encore passée
                boolean peutModifier = statut.equals("en attente")
                        && (date.isAfter(LocalDate.now())
                        || (date.isEqual(LocalDate.now()) && heure.isAfter(LocalTime.now())));

                // Peut ANNULER si : en attente ou confirmé ET date pas encore passée
                boolean peutAnnuler = (statut.equals("en attente") || statut.equals("confirmé"))
                        && (date.isAfter(LocalDate.now())
                        || (date.isEqual(LocalDate.now()) && heure.isAfter(LocalTime.now())));

                // Peut SUPPRIMER si : déjà annulé OU date passée
                boolean peutSupprimer = statut.equals("annulé") ||
                        date.isBefore(LocalDate.now()) ||
                        (date.isEqual(LocalDate.now()) && heure.isBefore(LocalTime.now()));

                modifierButton.setDisable(!peutModifier);
                annulerButton.setDisable(!peutAnnuler);
                supprimerButton.setDisable(!peutSupprimer);

                // Pré-remplir le formulaire de modification
                fillModificationForm(newSel);

            } else {
                modifierButton.setDisable(true);
                annulerButton.setDisable(true);
                supprimerButton.setDisable(true);
                modificationPane.setExpanded(false);
                modificationPane.setVisible(false);
            }
        });
    }

    private void fillModificationForm(Appointment a) {

        modifierDatePicker.setValue(a.getDateRdv());

        String heure = a.getHeureRdv().toString();

        // 🔥 AJOUT IMPORTANT
        if (!modifierHeureCombo.getItems().contains(heure)) {
            modifierHeureCombo.getItems().add(heure);
        }

        modifierHeureCombo.setValue(heure);

        modifierMotifField.setText(a.getMotif());
        modifierTypeChoice.setValue(a.getTypeRdv());
    }

    @FXML
    private void modifierRendezVous() {
        modificationPane.setDisable(false);
        if (selectedAppointment == null) return;

        // Remplir le formulaire avec les données actuelles
        fillModificationForm(selectedAppointment);

        // ✅ FORCER L'ACTIVATION DES CHAMPS (À AJOUTER !)
        modifierDatePicker.setDisable(false);
        modifierHeureCombo.setDisable(false);
        modifierMotifField.setDisable(false);
        modifierTypeChoice.setDisable(false);
        validerModificationButton.setDisable(false);

        // Rendre les champs éditables
        modifierDatePicker.setEditable(true);
        modifierHeureCombo.setEditable(true);
        modifierMotifField.setEditable(true);
        modifierTypeChoice.setDisable(false);

        // Afficher et développer le panneau
        modificationPane.setVisible(true);
        modificationPane.setExpanded(true);

        // Donner le focus au premier champ
        modifierDatePicker.requestFocus();
    }

    @FXML
    private void validerModification() {
        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Aucun rendez-vous sélectionné");
            return;
        }

        if (!validateModification()) return;

        try {
            // Vérifier que tous les champs sont remplis
            LocalDate newDate = modifierDatePicker.getValue();
            String newHeure = modifierHeureCombo.getValue();
            String newMotif = modifierMotifField.getText().trim();
            String newType = modifierTypeChoice.getValue();

            if (newDate == null || newHeure == null || newMotif.isEmpty() || newType == null) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Tous les champs doivent être remplis");
                return;
            }

            // Mettre à jour le rendez-vous
            selectedAppointment.setDateRdv(newDate);
            selectedAppointment.setHeureRdv(LocalTime.parse(newHeure));
            selectedAppointment.setMotif(newMotif);
            selectedAppointment.setTypeRdv(newType);
            // Le statut reste "en attente"

            service.update(selectedAppointment);

            // Recharger et fermer le panneau
            loadMesRendezVous();
            annulerModification();

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Votre rendez-vous a été modifié avec succès !");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de modifier le rendez-vous : " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Format d'heure invalide. Utilisez HH:MM (ex: 14:30)");
        }
    }

    @FXML
    private void annulerModification() {
        modificationPane.setExpanded(false);
        modificationPane.setVisible(false);
        clearModificationForm();
        tableView.getSelectionModel().clearSelection();
    }

    @FXML
    private void annulerRendezVous() {
        Appointment selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation d'annulation");
        confirm.setHeaderText("Annuler le rendez-vous ?");
        confirm.setContentText(String.format(
                "Voulez-vous vraiment annuler le rendez-vous du %s à %s ?",
                selected.getDateRdv(), selected.getHeureRdv()
        ));

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                selected.setStatut("annulé");
                service.update(selected);
                loadMesRendezVous();
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Votre rendez-vous a été annulé.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Impossible d'annuler le rendez-vous : " + e.getMessage());
            }
        }
    }

    @FXML
    private void supprimerRendezVous() {
        Appointment selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer définitivement le rendez-vous ?");
        confirm.setContentText(String.format(
                "RDV #%d - %s %s\nMotif: %s\n\nCette action est irréversible !",
                selected.getId(),
                selected.getDateRdv(),
                selected.getHeureRdv(),
                selected.getMotif()
        ));

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                service.delete(selected);
                loadMesRendezVous();
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Le rendez-vous a été supprimé définitivement.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Impossible de supprimer : " + e.getMessage());
            }
        }
    }

    @FXML
    private void nouveauRendezVous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/user_prendre_rdv.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nouveauRdvButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Prendre rendez-vous");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir le formulaire de rendez-vous");
        }
    }

    private boolean validateModification() {
        if (modifierDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez choisir une date");
            return false;
        }
        if (modifierHeureCombo.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez choisir une heure");
            return false;
        }
        if (modifierMotifField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez saisir le motif");
            return false;
        }
        if (modifierTypeChoice.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez choisir le type");
            return false;
        }
        return true;
    }

    private void clearModificationForm() {
        modifierDatePicker.setValue(null);
        modifierHeureCombo.setValue(null);
        modifierMotifField.clear();
        modifierTypeChoice.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}