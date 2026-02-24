package tn.esprit.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.DailyRoutineTask;
import tn.esprit.services.ServiceDailyRoutineTask;
import tn.esprit.services.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

import tn.esprit.services.QuoteService;
import tn.esprit.services.QuoteService.Quote;
import javafx.application.Platform;

import static tn.esprit.services.QuoteService.API_KEY;
import static tn.esprit.services.QuoteService.API_URL;

import javafx.application.Platform;
import tn.esprit.services.QuoteService;
import javafx.scene.control.Button; // Make sure btnSuggestQuote is imported

public class AddDailyRoutineTask {

    @FXML private ComboBox<Integer> cbUserId;
    @FXML private TextArea tfTitle;
    @FXML private CheckBox chkCompleted;
    @FXML private Button btnSuggestQuote;


    private final ServiceDailyRoutineTask serviceTask = new ServiceDailyRoutineTask();
    private final UserService serviceUser = new UserService();
    private final QuoteService quoteService = new QuoteService();

    @FXML
    public void initialize() {
        loadUserIds();
    }

    private void loadUserIds() {
        try {
            List<Integer> userIds = serviceUser.getAllUsersIds();
            cbUserId.setItems(FXCollections.observableArrayList(userIds));
            if (userIds.isEmpty()) {
                cbUserId.setPromptText("Aucun utilisateur disponible");
                cbUserId.setDisable(true);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les utilisateurs: " + e.getMessage());
        }
    }

    @FXML
    void handleAddTask(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        DailyRoutineTask task = new DailyRoutineTask(
                cbUserId.getValue(),
                tfTitle.getText().trim()
        );

        task.setCompleted(chkCompleted.isSelected());
        if (chkCompleted.isSelected()) {
            task.setCompletedAt(LocalDateTime.now().toString());
        }

        try {
            serviceTask.ajouter(task);

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setHeaderText(null);
            success.setContentText("✅ Tâche ajoutée avec succès !");
            success.showAndWait();

            // Close the current window
            closeWindow();

        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ajouter la tâche: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (cbUserId.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur");
            return false;
        }
        if (tfTitle.getText() == null || tfTitle.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez saisir un titre");
            return false;
        }
        return true;
    }

    @FXML
    private void goToDisplay() {
        closeWindow();
    }

    @FXML
    private void goToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdminDashboard.fxml"));
            Stage stage = (Stage) cbUserId.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner au dashboard");
        }
    }

    @FXML
    private void resetForm() {
        cbUserId.getSelectionModel().clearSelection();
        tfTitle.clear();
        chkCompleted.setSelected(false);
    }

    private void closeWindow() {
        Stage stage = (Stage) cbUserId.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    private void suggestQuoteTask() {
        // Disable button during API call
        btnSuggestQuote.setDisable(true);
        btnSuggestQuote.setText("⏳ Chargement...");

        // Clear the field
        tfTitle.clear();

        new Thread(() -> {
            try {
                QuoteService quoteService = new QuoteService();
                QuoteService.Quote quote = quoteService.getWellnessQuote();

                Platform.runLater(() -> {
                    try {
                        if (quote != null && quote.quote != null) {
                            // Format with quote, author, and category
                            String taskTitle = "💭 " + quote.quote;

                            if (quote.author != null && !quote.author.isEmpty() && !quote.author.equals("Unknown")) {
                                taskTitle += "\n— " + quote.author;
                            }

                            if (quote.category != null && !quote.category.isEmpty()) {
                                taskTitle += "\n🌿 " + quote.category.substring(0, 1).toUpperCase()
                                        + quote.category.substring(1).toLowerCase();
                            }

                            tfTitle.setText(taskTitle);
                        } else {
                            // No quote found after MAX_ATTEMPTS
                            tfTitle.setText("💭 Désolé, aucune citation bien-être trouvée pour le moment");
                        }

                    } finally {
                        btnSuggestQuote.setDisable(false);
                        btnSuggestQuote.setText("💡 Citation bien-être");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    btnSuggestQuote.setDisable(false);
                    btnSuggestQuote.setText("💡 Citation bien-être");
                    tfTitle.setText("💭 Erreur lors du chargement de la citation");
                });
            }
        }).start();
    }
}