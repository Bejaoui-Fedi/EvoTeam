package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.services.ServiceEvent;

import java.io.IOException;
import java.time.LocalDate;

public class AddEvent {

    @FXML private TextField name;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TextField maxParticipants;
    @FXML private TextField description;
    @FXML private TextField fee;

    // ✅ OK
    @FXML private TextField tfLocation;

    @FXML
    void handleAddEvent(ActionEvent event) {

        // Validation simple
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

            // ✅ Message + navigation
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

    // ✅ Bouton "Afficher les événements" (si tu l’ajoutes dans FXML)
    @FXML
    private void goToDisplay() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/DisplayEvent.fxml"));
            Stage stage = (Stage) name.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Events - CardView");
            stage.show();
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
