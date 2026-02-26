package tn.esprit.controllers.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Appointment;
import tn.esprit.entities.User;
<<<<<<< HEAD
import tn.esprit.controllers.UserDashboardController;
=======
>>>>>>> 7b6b857156e4ffce3799f4dd4112591c8c5bf0bb
import tn.esprit.services.AppointmentService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class UserPrendreRdvController {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> heureCombo;
    @FXML private TextField motifField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private Button prendreRdvButton;
    @FXML private Button voirMesRdvButton;
    @FXML private Label messageLabel;

    private AppointmentService service = new AppointmentService();
    private int currentUserId;
    private User currentUser;

    @FXML
    public void initialize() {
        setupHeures();
        setupTypeChoice();
        setupDatePicker();
        messageLabel.setText("");
    }

    // ✅ Méthode pour recevoir l'utilisateur connecté
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            this.currentUserId = user.getId();
            System.out.println("✅ Utilisateur connecté dans UserPrendreRdvController: " + user.getNom());
        }
    }

    private void setupHeures() {
        ObservableList<String> creneaux = FXCollections.observableArrayList();
        for (int i = 8; i <= 18; i++) {
            creneaux.add(String.format("%02d:00", i));
            creneaux.add(String.format("%02d:30", i));
        }
        heureCombo.setItems(creneaux);
        heureCombo.setEditable(true);
    }

    private void setupTypeChoice() {
        typeChoice.setItems(FXCollections.observableArrayList("Présentiel", "Téléconsultation"));
        typeChoice.setValue("Présentiel");
    }

    private void setupDatePicker() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    private void prendreRendezVous() {
        if (!validateInputs()) return;

        try {
            Appointment rdv = new Appointment(
                    datePicker.getValue(),
                    LocalTime.parse(heureCombo.getValue()),
                    "en attente",
                    motifField.getText().trim(),
                    typeChoice.getValue()
            );
            rdv.setUserId(currentUserId);

            service.add(rdv);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "✅ Votre rendez-vous a été pris avec succès !");
            clearForm();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "❌ Impossible de prendre le rendez-vous : " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "❌ Format d'heure invalide. Utilisez HH:MM");
        }
    }

<<<<<<< HEAD
    private UserDashboardController dashboardController;

    public void setDashboardController(UserDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

=======
>>>>>>> 7b6b857156e4ffce3799f4dd4112591c8c5bf0bb
    @FXML
    private void voirMesRendezVous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/user_mes_rdv.fxml"));
            Parent root = loader.load();

            UserMesRdvController controller = loader.getController();
<<<<<<< HEAD
            controller.setUserId(currentUserId);
            controller.setDashboardController(dashboardController); // Pass dashboard controller

            if (dashboardController != null) {
                dashboardController.setContent(root);
            } else {
                // Fallback behavior if dashboard is not set (should not happen in normal flow)
                Stage stage = (Stage) voirMesRdvButton.getScene().getWindow();
                stage.setScene(new Scene(root));
            }
=======
            controller.setUserId(currentUserId); // ✅ Appel direct sans réflexion

            Stage stage = (Stage) voirMesRdvButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mes rendez-vous");
>>>>>>> 7b6b857156e4ffce3799f4dd4112591c8c5bf0bb

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page de vos rendez-vous: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        if (datePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez choisir une date");
            return false;
        }
        if (heureCombo.getValue() == null || heureCombo.getValue().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez choisir une heure");
            return false;
        }
        if (motifField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez saisir le motif");
            return false;
        }
        if (typeChoice.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez choisir le type");
            return false;
        }
        return true;
    }

    private void clearForm() {
        datePicker.setValue(LocalDate.now());
        heureCombo.setValue(null);
        motifField.clear();
        typeChoice.setValue("Présentiel");
        messageLabel.setText("");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}