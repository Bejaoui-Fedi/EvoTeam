package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import tn.esprit.services.HistoriqueService;

import java.util.List;

public class HistoriqueController {

    @FXML
    private ListView<String> historiqueListView;

    @FXML
    private Label lblCount;

    @FXML
    private TextField searchField;

    private List<String> allEntries;

    @FXML
    public void initialize() {
        // Personnalisation de l'affichage des cellules
        historiqueListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Colorer selon le type d'action
                    String style = "-fx-padding: 8; -fx-background-radius: 5; -fx-margin: 2;";
                    if (item.contains("AJOUT")) {
                        style += "-fx-background-color: #d4edda; -fx-text-fill: #155724;";
                    } else if (item.contains("MODIFICATION")) {
                        style += "-fx-background-color: #fff3cd; -fx-text-fill: #856404;";
                    } else if (item.contains("SUPPRESSION")) {
                        style += "-fx-background-color: #f8d7da; -fx-text-fill: #721c24;";
                    } else {
                        style += "-fx-background-color: #e2e3e5; -fx-text-fill: #383d41;";
                    }
                    setStyle(style);
                    setText(item);
                }
            }
        });

        loadHistorique();
    }

    private void loadHistorique() {
        allEntries = HistoriqueService.lireTout();
        updateDisplay(allEntries);
    }

    private void updateDisplay(List<String> entries) {
        historiqueListView.getItems().clear();
        historiqueListView.getItems().addAll(entries);
        lblCount.setText(entries.size() + " entrée(s)");
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            updateDisplay(allEntries);
            return;
        }

        List<String> filtered = allEntries.stream()
                .filter(line -> line.toLowerCase().contains(searchText))
                .toList();
        updateDisplay(filtered);
    }

    @FXML
    private void refresh() {
        searchField.clear();
        loadHistorique();
    }

    @FXML
    private void copyToClipboard() {
        String selected = historiqueListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ClipboardContent content = new ClipboardContent();
            content.putString(selected);
            Clipboard.getSystemClipboard().setContent(content);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Copié");
            alert.setHeaderText(null);
            alert.setContentText("Ligne copiée dans le presse-papier");
            alert.showAndWait();
        }
    }

    @FXML
    private void fermer() {
        historiqueListView.getScene().getWindow().hide();
    }
}