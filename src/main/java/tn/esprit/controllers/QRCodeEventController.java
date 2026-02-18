package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class QRCodeEventController {

    @FXML private ImageView qrImageView;
    @FXML private Label eventInfoLabel;
    @FXML private VBox qrContainer;

    public void setEventData(Event event) {
        try {
            // Construire l'URL avec tous les paramètres
            String baseUrl = "http://localhost:8080/event_detail.html"; // Change selon ton hébergement

            String url = baseUrl + "?"
                    + "id=" + event.getEventId()
                    + "&name=" + URLEncoder.encode(event.getName(), StandardCharsets.UTF_8)
                    + "&start=" + event.getStartDate()
                    + "&end=" + event.getEndDate()
                    + "&location=" + URLEncoder.encode(event.getLocation(), StandardCharsets.UTF_8)
                    + "&max=" + event.getMaxParticipants()
                    + "&fee=" + event.getFee()
                    + "&desc=" + URLEncoder.encode(event.getDescription(), StandardCharsets.UTF_8);

            // Afficher un message dans le label
            if (eventInfoLabel != null) {
                eventInfoLabel.setText("Scannez pour voir la page web de: " + event.getName());
            }

            // Générer le QR code avec l'URL
            Image qrCode = generateQRCode(url);
            qrImageView.setImage(qrCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Image generateQRCode(String data) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 250;
        int height = 250;
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
            WritableImage image = new WritableImage(width, height);
            PixelWriter pixelWriter = image.getPixelWriter();

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    pixelWriter.setArgb(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return image;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) qrImageView.getScene().getWindow();
        stage.close();
    }
}