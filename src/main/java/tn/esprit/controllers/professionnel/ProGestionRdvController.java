package tn.esprit.controllers.professionnel;

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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import tn.esprit.entities.Appointment;
import tn.esprit.services.AppointmentService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class ProGestionRdvController {

    @FXML
    private DatePicker dateFilterPicker;
    @FXML
    private ChoiceBox<String> statutFilterChoice;

    @FXML
    private FlowPane cardsContainer;

    @FXML
    private Button modifierButton;
    @FXML
    private Button supprimerButton;
    @FXML
    private Button updateStatusButton;
    @FXML
    private ChoiceBox<String> statutUpdateChoice;
    @FXML
    private Label totalLabel;
    @FXML
    private Button consultationsButton;
    @FXML
    private Button smsButton;
    @FXML
    private Button statsButton;
    @FXML
    private Button calendarButton;

    private AppointmentService service = new AppointmentService();
    private tn.esprit.services.SMSService smsService = new tn.esprit.services.SMSService();
    private tn.esprit.services.MailingService mailingService = new tn.esprit.services.MailingService();
    private tn.esprit.services.AIService aiService = new tn.esprit.services.AIService();

    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
    private Appointment selectedAppointment;

    @FXML
    public void initialize() {
        setupFilters();
        setupChoiceBoxes();
        loadAllAppointments();
    }

    private void renderCards(ObservableList<Appointment> appointments) {
        cardsContainer.getChildren().clear();
        for (Appointment app : appointments) {
            javafx.scene.layout.VBox card = createAppointmentCard(app);
            cardsContainer.getChildren().add(card);
        }
    }

    private javafx.scene.layout.VBox createAppointmentCard(Appointment app) {
        javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(10);
        card.getStyleClass().add("item-card");
        card.setPrefWidth(300);

        // Header: ID and Status
        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label idLabel = new Label("#" + app.getId());
        idLabel.getStyleClass().add("card-id");
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label statusBadge = new Label(app.getStatut().toUpperCase());
        statusBadge.getStyleClass().add("card-badge");
        applyStatusStyle(statusBadge, app.getStatut());

        header.getChildren().addAll(idLabel, spacer, statusBadge);

        // Title: Patient ID (could be name if available)
        Label patientLabel = new Label("Patient ID: " + app.getUserId());
        patientLabel.getStyleClass().add("card-title");

        // Info: Date and Time
        HBox dateTimeInfo = new HBox(10);
        dateTimeInfo.getChildren().addAll(
                createField("📅", app.getDateRdv().toString()),
                createField("⏰", app.getHeureRdv().toString()));

        // Motif with IA indicator
        javafx.scene.layout.VBox motifBox = new javafx.scene.layout.VBox(5);
        Label motifLabel = new Label(app.getMotif());
        motifLabel.setWrapText(true);
        motifLabel.getStyleClass().add("card-info-value");
        motifBox.getChildren().addAll(new Label("📝 Motif:"), motifLabel);

        if (aiService.isUrgent(app.getMotif())) {
            Label urgentLabel = new Label("🚨 URGENT DETECTED");
            urgentLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 11;");
            motifBox.getChildren().add(urgentLabel);
            card.setStyle("-fx-border-color: red; -fx-border-width: 2;");
        }

        Region separator = new Region();
        separator.getStyleClass().add("card-separator");
        separator.setPrefHeight(1);

        // Selection handling
        card.setOnMouseClicked(e -> {
            // Unselect others (visual only here as we don't have a selection model for
            // FlowPane)
            for (javafx.scene.Node n : cardsContainer.getChildren()) {
                n.setStyle(aiService.isUrgent(((Appointment) n.getUserData()).getMotif())
                        ? "-fx-border-color: red; -fx-border-width: 2;"
                        : "");
            }
            card.setStyle("-fx-border-color: -primary-color; -fx-border-width: 3;");
            selectedAppointment = app;
            updateSelectionState();
        });

        card.setUserData(app);

        card.getChildren().addAll(header, patientLabel, dateTimeInfo, motifBox, separator);
        return card;
    }

    private javafx.scene.layout.VBox createField(String icon, String value) {
        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(2);
        Label valLabel = new Label(icon + " " + value);
        valLabel.getStyleClass().add("card-info-value");
        box.getChildren().add(valLabel);
        return box;
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

    private void updateSelectionState() {
        if (selectedAppointment != null) {
            statutUpdateChoice.setValue(selectedAppointment.getStatut());
            modifierButton.setDisable(false);
            supprimerButton.setDisable(false);
            updateStatusButton.setDisable(false);
            smsButton.setDisable(false);
        } else {
            modifierButton.setDisable(true);
            supprimerButton.setDisable(true);
            updateStatusButton.setDisable(true);
            smsButton.setDisable(true);
        }
    }

    private void setupFilters() {
        dateFilterPicker.setValue(LocalDate.now());
        dateFilterPicker.valueProperty().addListener((obs, old, newVal) -> applyFilters());

        statutFilterChoice.setItems(FXCollections.observableArrayList(
                "Tous", "en attente", "confirmé", "terminé", "annulé"));
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

        renderCards(filteredAppointments);
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
                total, aujourdHui, enAttente));
    }

    @FXML
    private void handleSendSMS() {
        if (selectedAppointment == null)
            return;

        // On demande le numéro au lieu de le harcoder pour que l'utilisateur puisse
        // tester avec son propre tel
        TextInputDialog dialog = new TextInputDialog("+21626562760");
        dialog.setTitle("Envoi de rappel SMS");
        dialog.setHeaderText("Rappel pour le RDV #" + selectedAppointment.getId());
        dialog.setContentText("Veuillez saisir le numéro du patient (Format: +216XXXXXXXX) :");

        java.util.Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String patientPhone = result.get();
            try {
                // RAPPEL : Pour un compte Twilio gratuit, le numéro DOIT être vérifié sur
                // Twilio Console
                String message = String.format("Rappel EvoTeam: Votre rendez-vous est prévu le %s à %s. Motif: %s",
                        selectedAppointment.getDateRdv(),
                        selectedAppointment.getHeureRdv(),
                        selectedAppointment.getMotif());

                smsService.sendSMS(patientPhone, message);

                showAlert(Alert.AlertType.INFORMATION, "SMS Envoyé",
                        "Un rappel SMS a été envoyé au numéro : " + patientPhone);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'envoyer le SMS : " + e.getMessage());
            }
        }
    }

    @FXML
    private void modifierRendezVous() {

        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner un rendez-vous à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/professionnel/modifier_rdv_pro_dialog.fxml"));

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
        if (selectedAppointment == null)
            return;

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
                selectedAppointment.getMotif()));

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
        if (selectedAppointment == null || statutUpdateChoice.getValue() == null)
            return;

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
                nouveauStatut));

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                selectedAppointment.setStatut(nouveauStatut);
                service.update(selectedAppointment);

                // Envoi d'email si le rendez-vous est confirmé
                if ("confirmé".equals(nouveauStatut)) {
                    String subject = "Confirmation de votre rendez-vous - EvoTeam";
                    String body = String.format(
                            "Bonjour,\n\nVotre rendez-vous du %s à %s a été confirmé par le professionnel.\n\nMotif: %s\n\nCordialement,\nL'équipe EvoTeam Santé",
                            selectedAppointment.getDateRdv(),
                            selectedAppointment.getHeureRdv(),
                            selectedAppointment.getMotif());

                    // Simulation d'email (le destinataire réel viendrait de la table User)
                    mailingService.sendEmail("molka.jbeli25@gmail.com", subject, body);
                }

                loadAllAppointments();
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Statut mis à jour avec succès et notification envoyée.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour : " + e.getMessage());
            }
        }
    }

    @FXML
    private void gererConsultations() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/professionnel/pro_gestion_consultation.fxml"));
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
            stage.setTitle("Tableau de bord des Statistiques");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le tableau de bord : " + e.getMessage());
        }
    }

    @FXML
    private void handleOpenCalendar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/professionnel/calendar.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) calendarButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Calendrier des Rendez-vous - EvoTeam");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le calendrier : " + e.getMessage());
            e.printStackTrace();
        }
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