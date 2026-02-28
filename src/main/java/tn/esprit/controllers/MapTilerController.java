package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.entities.Event;

import java.net.URL;
import java.util.ResourceBundle;

public class MapTilerController implements Initializable {

    @FXML
    private ImageView mapImageView;

    private Event event;

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charge une carte par défaut au démarrage
        loadDefaultMap();
    }

    private void loadDefaultMap() {
        // Tunis par défaut
        String mapUrl = "https://staticmap.openstreetmap.de/staticmap.php?center=36.8065,10.1815&zoom=14&size=800x600&markers=36.8065,10.1815";

        System.out.println("🌍 Chargement : " + mapUrl);

        Image mapImage = new Image(mapUrl, true);
        mapImageView.setImage(mapImage);
    }

    public void loadMapWithCoords(double lat, double lon) {
        // Carte centrée sur les coordonnées données
        String mapUrl = "https://staticmap.openstreetmap.de/staticmap.php?center="
                + lat + "," + lon
                + "&zoom=14&size=800x600&markers=" + lat + "," + lon;

        System.out.println("🌍 Chargement : " + mapUrl);

        Image mapImage = new Image(mapUrl, true);
        mapImageView.setImage(mapImage);
    }
}