package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.entities.Event;
import tn.esprit.services.ServiceEvent;

public class UpdateEvent {

    @FXML private TextField name;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TextField maxParticipants;
    @FXML private TextField description;
    @FXML private TextField fee;
    @FXML private TextField tfLocation;

    private Event event;
    private final ServiceEvent serviceEvent = new ServiceEvent();

    // appelé depuis DisplayEvent (double clic)
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
        event.setName(name.getText());
        event.setStartDate(startDate.getValue().toString());
        event.setEndDate(endDate.getValue().toString());
        event.setMaxParticipants(Integer.parseInt(maxParticipants.getText()));
        event.setDescription(description.getText());
        event.setFee(Integer.parseInt(fee.getText()));
        event.setLocation(tfLocation.getText());

        serviceEvent.update(event);

        new Alert(Alert.AlertType.INFORMATION, "Événement modifié avec succès").show();
    }
}
