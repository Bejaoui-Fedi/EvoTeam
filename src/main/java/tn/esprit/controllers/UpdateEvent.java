package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import tn.esprit.entities.Event;
import tn.esprit.services.ServiceEvent;

public class UpdateEvent {

    @FXML private TextField name;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TextField maxParticipants;
    @FXML private TextArea description;
    @FXML private TextField fee;
    @FXML private TextField tfLocation;

    private Event event;
    private final ServiceEvent serviceEvent = new ServiceEvent();

    // 🔹 Référence dashboard pour navigation si besoin
    private AdminDashboardController dashboard;

    public void setDashboardController(AdminDashboardController dashboard) {
        this.dashboard = dashboard;
    }

    // appelé depuis DisplayEvent pour pré-remplir le formulaire
    public void initData(Event e) {
        this.event = e;
        name.setText(e.getName());
        startDate.setValue(java.time.LocalDate.parse(e.getStartDate()));
        endDate.setValue(java.time.LocalDate.parse(e.getEndDate()));
        maxParticipants.setText(String.valueOf(e.getMaxParticipants()));
        description.setText(e.getDescription());
        fee.setText(String.valueOf(e.getFee()));
        tfLocation.setText(e.getLocation());
    }

    @FXML
    private void handleUpdate() {
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

        try {
            event.setName(name.getText());
            event.setStartDate(startDate.getValue().toString());
            event.setEndDate(endDate.getValue().toString());
            event.setMaxParticipants(Integer.parseInt(maxParticipants.getText()));
            event.setDescription(description.getText());
            event.setFee(Integer.parseInt(fee.getText()));
            event.setLocation(tfLocation.getText());

            serviceEvent.update(event);

            new Alert(Alert.AlertType.INFORMATION, "Événement modifié avec succès ✅").show();

            // Optionnel : retour vers DisplayEvent via dashboard
            /*if (dashboard != null) {
                dashboard.goToDisplayEvent(); // créer cette méthode si nécessaire
            }*/

        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Participants ou prix invalide !").show();
        }
    }

    @FXML
    private void resetForm() {
        if (event != null) {
            initData(event); // recharge les valeurs originales
        }
    }
}
