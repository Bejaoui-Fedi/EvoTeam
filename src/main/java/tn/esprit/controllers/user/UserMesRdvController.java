package tn.esprit.controllers.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Appointment;
import tn.esprit.entities.User;
import tn.esprit.controllers.UserDashboardController;
import tn.esprit.services.AppointmentService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class UserMesRdvController {

    @FXML private TableView<Appointment> tableView;  // ← Attention: dans FXML c'est "tableView"
    @FXML private TableColumn<Appointment, LocalDate> colDate;
    @FXML private TableColumn<Appointment, LocalTime> colHeure;
    @FXML private TableColumn<Appointment, String> colMotif;
    @FXML private TableColumn<Appointment, String> colType;
    @FXML private TableColumn<Appointment, String> colStatut;

    @FXML private Label messageLabel;
    @FXML private Label infoLabel;
    @FXML private Label compteurLabel;
    @FXML private Button modifierButton;
    @FXML private Button annulerButton;
    @FXML private Button supprimerButton;
    @FXML private Button nouveauRdvButton;

    private AppointmentService service = new AppointmentService();
    private int currentUserId;
    private ObservableList<Appointment> rdvList = FXCollections.observableArrayList();
    private Appointment selectedAppointment;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSelectionListener();
    }

    public void setUserId(int userId) {
        this.currentUserId = userId;
        System.out.println("✅ ID utilisateur reçu dans UserMesRdvController: " + userId);
        loadUserAppointments();
    }

    private void setupTableColumns() {
        // ⚠️ On n'utilise PAS colId car il n'existe pas dans le FXML
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateRdv"));
        colHeure.setCellValueFactory(new PropertyValueFactory<>("heureRdv"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeRdv"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

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
                        case "en attente":
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        case "annulé":
                            setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        case "terminé":
                            setStyle("-fx-background-color: #cce5ff; -fx-text-fill: #004085; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                    }
                }
            }
        });
    }

    private void setupSelectionListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, old, newSel) -> {
            selectedAppointment = newSel;
            boolean isSelected = newSel != null;

            if (modifierButton != null) modifierButton.setDisable(!isSelected);
            if (annulerButton != null) annulerButton.setDisable(!isSelected);
            if (supprimerButton != null) supprimerButton.setDisable(!isSelected);
        });
    }

    private void loadUserAppointments() {
        try {
            rdvList.clear();
            for (Appointment a : service.getAll()) {
                if (a.getUserId() == currentUserId) {
                    rdvList.add(a);
                }
            }
            tableView.setItems(rdvList);
            if (compteurLabel != null) {
                compteurLabel.setText("✅ " + rdvList.size() + " rendez-vous trouvés");
            }
        } catch (SQLException e) {
            if (compteurLabel != null) {
                compteurLabel.setText("❌ Erreur de chargement: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    @FXML
    private void refresh() {
        loadUserAppointments();
    }

    @FXML
    private void modifierRendezVous() {
        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un rendez-vous à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/modifier_rdv_dialog.fxml"));
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
                loadUserAppointments();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "✅ Rendez-vous modifié avec succès !");
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annulerRendezVous() {
        if (selectedAppointment == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation d'annulation");
        confirm.setHeaderText("Annuler le rendez-vous ?");
        confirm.setContentText("Voulez-vous vraiment annuler ce rendez-vous ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                selectedAppointment.setStatut("annulé");
                service.update(selectedAppointment);
                loadUserAppointments();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "✅ Rendez-vous annulé avec succès !");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "❌ Impossible d'annuler le rendez-vous: " + e.getMessage());
            }
        }
    }

    @FXML
    private void supprimerRendezVous() {
        if (selectedAppointment == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer le rendez-vous ?");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer ce rendez-vous ?\nCette action est irréversible !");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                service.delete(selectedAppointment);
                loadUserAppointments();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "✅ Rendez-vous supprimé avec succès !");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "❌ Impossible de supprimer le rendez-vous: " + e.getMessage());
            }
        }
    }

    private UserDashboardController dashboardController;

    public void setDashboardController(UserDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    private void nouveauRendezVous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/user_prendre_rdv.fxml"));
            Parent root = loader.load();

            UserPrendreRdvController controller = loader.getController();
            controller.setCurrentUser(new User(currentUserId, "", "", "", "", "", true)); 
            // Better would be to pass the full User object if available, but for now ID is critical.
            // Ideally UserMesRdvController should store the full User object.
            
            controller.setDashboardController(dashboardController);

            if (dashboardController != null) {
                dashboardController.setContent(root);
            } else {
                 Stage stage = (Stage) nouveauRdvButton.getScene().getWindow();
                 stage.setScene(new Scene(root));
                 stage.setTitle("Prendre un rendez-vous");
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}