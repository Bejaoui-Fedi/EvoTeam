package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import tn.esprit.entities.Event;
import tn.esprit.services.ServiceEvent;

public class DeleteEvent {

    private final ServiceEvent serviceEvent = new ServiceEvent();
    private Event event;

    // appelé depuis DisplayEvent
    public void setEvent(Event event) {
        this.event = event;
    }

    @FXML
    private void confirmDelete() {
        if (event == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Suppression");
        confirm.setContentText("Supprimer l'événement : " + event.getName() + " ?");

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                serviceEvent.delete(event.getEventId());
                new Alert(Alert.AlertType.INFORMATION, "Événement supprimé").show();
            }
        });
    }
}
