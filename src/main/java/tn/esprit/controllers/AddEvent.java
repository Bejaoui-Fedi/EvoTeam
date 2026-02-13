package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import tn.esprit.entities.Event;
import tn.esprit.services.ServiceEvent;

import java.io.IOException;
import java.time.LocalDate;

public class AddEvent {

    @FXML private TextField name;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TextField maxParticipants;
    @FXML private TextArea description;
    @FXML private TextField fee;
    @FXML private TextField tfLocation;

    private AdminDashboardController dashboard; // 🔹 Référence dashboard

    public void setDashboardController(AdminDashboardController dashboard) {
        this.dashboard = dashboard;
    }

    @FXML
    void handleAddEvent(ActionEvent event) {

        if (name.getText().isEmpty()
                || startDate.getValue() == null
                || endDate.getValue() == null
                || maxParticipants.getText().isEmpty()
                || description.getText().isEmpty()
                || fee.getText().isEmpty()
                || tfLocation.getText().isEmpty()) {

            new Alert(Alert.AlertType.ERROR, "Tous les champs doivent être remplis !").show();
            return;
        }

        if (!startDate.getValue().isAfter(LocalDate.now())) {
            new Alert(Alert.AlertType.ERROR, "La date de début doit être future !").show();
            return;
        }

        if (endDate.getValue().isBefore(startDate.getValue())) {
            new Alert(Alert.AlertType.ERROR, "La date de fin doit être après la date de début !").show();
            return;
        }

        try {
            Event e = new Event(
                    name.getText(),
                    startDate.getValue().toString(),
                    endDate.getValue().toString(),
                    Integer.parseInt(maxParticipants.getText()),
                    description.getText(),
                    Integer.parseInt(fee.getText()),
                    tfLocation.getText()
            );

            ServiceEvent service = new ServiceEvent();
            service.ajouter(e);

            Alert ok = new Alert(Alert.AlertType.INFORMATION,
                    "Événement ajouté avec succès !\nVoulez-vous afficher la liste ?",
                    ButtonType.YES, ButtonType.NO);
            ok.setTitle("Succès");
            ok.setHeaderText(null);

            ok.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    goToDisplay();
                } else {
                    resetForm();
                }
            });

        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Participants ou prix invalide !").show();
        }
    }

    @FXML
    private void goToDisplay() {
        try {
            if (dashboard != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayEvent.fxml"));
                Parent root = loader.load();

                DisplayEvent controller = loader.getController();
                controller.setDashboardController(dashboard);

                dashboard.setContent(root); // 🔹 navigation via dashboard
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir DisplayEvent.fxml").show();
        }
    }

    private void resetForm() {
        name.clear();
        startDate.setValue(null);
        endDate.setValue(null);
        maxParticipants.clear();
        description.clear();
        fee.clear();
        tfLocation.clear();
    }
}
