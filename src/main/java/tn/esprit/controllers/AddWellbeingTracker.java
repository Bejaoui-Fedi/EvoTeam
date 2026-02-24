package tn.esprit.controllers;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.WellbeingTracker;
import tn.esprit.entities.User;
import tn.esprit.entities.DailyRoutineTask;
import tn.esprit.services.ServiceWellbeingTracker;
import tn.esprit.services.ServiceDailyRoutineTask;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddWellbeingTracker {

    // Original FXML fields
    @FXML private ComboBox<Integer> cbUserId;
    @FXML private ComboBox<Integer> cbRoutineTaskId;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Integer> cbMood;
    @FXML private ComboBox<Integer> cbStress;
    @FXML private ComboBox<Integer> cbEnergy;
    @FXML private TextField tfSleepHours;
    @FXML private TextArea taNote;

    // NEW: Search fields
    @FXML private TextField searchUserField;
    @FXML private ListView<String> userSuggestionsList;
    @FXML private Label selectedUserLabel;
    @FXML private Button clearUserBtn;

    @FXML private TextField searchTaskField;
    @FXML private ListView<String> taskSuggestionsList;
    @FXML private Label selectedTaskLabel;
    @FXML private Button clearTaskBtn;

    private final ServiceWellbeingTracker serviceTracker = new ServiceWellbeingTracker();
    private final ServiceDailyRoutineTask serviceTask = new ServiceDailyRoutineTask();
    private final UserService serviceUser = new UserService();

    // Data lists
    private List<User> allUsers = new ArrayList<>();
    private List<DailyRoutineTask> allTasks = new ArrayList<>();
    private User selectedUser = null;
    private DailyRoutineTask selectedTask = null;

    // Search delays
    private PauseTransition userSearchDelay = new PauseTransition(Duration.millis(300));
    private PauseTransition taskSearchDelay = new PauseTransition(Duration.millis(300));

    @FXML
    public void initialize() {
        loadAllData();
        setupUserSearch();
        setupTaskSearch();
        loadDefaultValues();

        // Hide suggestion lists initially
        userSuggestionsList.setVisible(false);
        userSuggestionsList.setManaged(false);
        taskSuggestionsList.setVisible(false);
        taskSuggestionsList.setManaged(false);
    }

    private void loadAllData() {
        try {
            allUsers = serviceUser.getAll(); // Make sure this method exists
            allTasks = serviceTask.getAll();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les données: " + e.getMessage());
        }
    }

    private void setupUserSearch() {
        searchUserField.textProperty().addListener((observable, oldValue, newValue) -> {
            userSearchDelay.setOnFinished(event -> searchUsers(newValue));
            userSearchDelay.playFromStart();

            // Show clear button if there's text
            clearUserBtn.setVisible(!newValue.isEmpty());
            clearUserBtn.setManaged(!newValue.isEmpty());
        });

        // Handle selection from suggestions
        userSuggestionsList.setOnMouseClicked(event -> {
            String selected = userSuggestionsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectUser(selected);
            }
        });

        // Clear user selection
        clearUserBtn.setOnAction(event -> clearUserSelection());

        // Handle Enter key
        searchUserField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                if (!userSuggestionsList.getItems().isEmpty()) {
                    selectUser(userSuggestionsList.getItems().get(0));
                }
            }
        });
    }

    private void setupTaskSearch() {
        searchTaskField.textProperty().addListener((observable, oldValue, newValue) -> {
            taskSearchDelay.setOnFinished(event -> searchTasks(newValue));
            taskSearchDelay.playFromStart();

            clearTaskBtn.setVisible(!newValue.isEmpty());
            clearTaskBtn.setManaged(!newValue.isEmpty());
        });

        taskSuggestionsList.setOnMouseClicked(event -> {
            String selected = taskSuggestionsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectTask(selected);
            }
        });

        clearTaskBtn.setOnAction(event -> clearTaskSelection());

        searchTaskField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                if (!taskSuggestionsList.getItems().isEmpty()) {
                    selectTask(taskSuggestionsList.getItems().get(0));
                }
            }
        });
    }

    private void searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            userSuggestionsList.setVisible(false);
            userSuggestionsList.setManaged(false);
            return;
        }

        String lowerQuery = query.toLowerCase().trim();

        // Search users by first name, last name, or email
        List<String> suggestions = allUsers.stream()
                .filter(user -> user.getNom().toLowerCase().contains(lowerQuery) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerQuery)))
                .map(user -> user.getNom() + " 📧 " + user.getEmail())
                .limit(10)
                .collect(Collectors.toList());

        if (!suggestions.isEmpty()) {
            userSuggestionsList.getItems().setAll(suggestions);
            userSuggestionsList.setVisible(true);
            userSuggestionsList.setManaged(true);
        } else {
            userSuggestionsList.setVisible(false);
            userSuggestionsList.setManaged(false);
        }
    }

    private void searchTasks(String query) {
        if (query == null || query.trim().isEmpty()) {
            taskSuggestionsList.setVisible(false);
            taskSuggestionsList.setManaged(false);
            return;
        }

        String lowerQuery = query.toLowerCase().trim();

        // Search tasks by title
        List<String> suggestions = allTasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(lowerQuery))
                .map(task -> "📌 " + task.getTitle() + " (User: " + task.getUserId() + ")")
                .limit(10)
                .collect(Collectors.toList());

        if (!suggestions.isEmpty()) {
            taskSuggestionsList.getItems().setAll(suggestions);
            taskSuggestionsList.setVisible(true);
            taskSuggestionsList.setManaged(true);
        } else {
            taskSuggestionsList.setVisible(false);
            taskSuggestionsList.setManaged(false);
        }
    }

    private void selectUser(String selectedText) {
        // Extract user name from selection
        String userName = selectedText.split(" 📧 ")[0];

        // Find the user
        selectedUser = allUsers.stream()
                .filter(u -> u.getNom().equals(userName))
                .findFirst()
                .orElse(null);

        if (selectedUser != null) {
            selectedUserLabel.setText("👤 Sélectionné: " + selectedUser.getNom() + " (" + selectedUser.getEmail() + ")");
            selectedUserLabel.setStyle("-fx-text-fill: #3A7D6B; -fx-font-weight: bold;");
            cbUserId.setValue(selectedUser.getId());

            // Hide suggestions
            userSuggestionsList.setVisible(false);
            userSuggestionsList.setManaged(false);
            searchUserField.setText(selectedUser.getNom());
        }
    }

    private void selectTask(String selectedText) {
        // Extract task title
        String taskTitle = selectedText.replace("📌 ", "").split(" \\(User:")[0];

        // Find the task
        selectedTask = allTasks.stream()
                .filter(t -> t.getTitle().equals(taskTitle))
                .findFirst()
                .orElse(null);

        if (selectedTask != null) {
            selectedTaskLabel.setText("✅ Sélectionnée: " + selectedTask.getTitle());
            selectedTaskLabel.setStyle("-fx-text-fill: #3A7D6B; -fx-font-weight: bold;");
            cbRoutineTaskId.setValue(selectedTask.getId());

            taskSuggestionsList.setVisible(false);
            taskSuggestionsList.setManaged(false);
            searchTaskField.setText(selectedTask.getTitle());
        }
    }

    private void clearUserSelection() {
        selectedUser = null;
        selectedUserLabel.setText("Aucun utilisateur sélectionné");
        selectedUserLabel.setStyle("-fx-text-fill: #7f8c8d;");
        searchUserField.clear();
        cbUserId.setValue(null);
        userSuggestionsList.setVisible(false);
        userSuggestionsList.setManaged(false);
        clearUserBtn.setVisible(false);
        clearUserBtn.setManaged(false);
    }

    private void clearTaskSelection() {
        selectedTask = null;
        selectedTaskLabel.setText("Aucune tâche sélectionnée");
        selectedTaskLabel.setStyle("-fx-text-fill: #7f8c8d;");
        searchTaskField.clear();
        cbRoutineTaskId.setValue(null);
        taskSuggestionsList.setVisible(false);
        taskSuggestionsList.setManaged(false);
        clearTaskBtn.setVisible(false);
        clearTaskBtn.setManaged(false);
    }

    private void loadDefaultValues() {
        cbMood.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbStress.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbEnergy.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));

        datePicker.setValue(LocalDate.now());
        cbMood.setValue(3);
        cbStress.setValue(3);
        cbEnergy.setValue(3);
        tfSleepHours.setText("8.0");

        // Initialize selection labels
        selectedUserLabel.setText("Aucun utilisateur sélectionné");
        selectedTaskLabel.setText("Aucune tâche sélectionnée");
        clearUserBtn.setVisible(false);
        clearUserBtn.setManaged(false);
        clearTaskBtn.setVisible(false);
        clearTaskBtn.setManaged(false);
    }

    @FXML
    void handleAddTracker(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        double sleepHours = Double.parseDouble(tfSleepHours.getText().trim());

        WellbeingTracker tracker = new WellbeingTracker(
                cbUserId.getValue(),
                cbRoutineTaskId.getValue(),
                datePicker.getValue().toString(),
                cbMood.getValue(),
                cbStress.getValue(),
                cbEnergy.getValue(),
                sleepHours,
                taNote.getText()
        );

        try {
            serviceTracker.ajouter(tracker);

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setHeaderText(null);
            success.setContentText("✅ Suivi bien-être ajouté avec succès !");
            success.showAndWait();

            closeWindow();

        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ajouter le suivi: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (selectedUser == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur");
            return false;
        }
        if (selectedTask == null) {
            showAlert("Erreur", "Veuillez sélectionner une tâche");
            return false;
        }
        if (datePicker.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une date");
            return false;
        }
        if (cbMood.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une humeur");
            return false;
        }
        if (cbStress.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner un niveau de stress");
            return false;
        }
        if (cbEnergy.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner un niveau d'énergie");
            return false;
        }
        if (tfSleepHours.getText() == null || tfSleepHours.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez saisir les heures de sommeil");
            return false;
        }

        try {
            double hours = Double.parseDouble(tfSleepHours.getText().trim());
            if (hours < 0 || hours > 24) {
                showAlert("Erreur", "Les heures de sommeil doivent être entre 0 et 24");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format d'heures de sommeil invalide");
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
        clearUserSelection();
        clearTaskSelection();
        datePicker.setValue(LocalDate.now());
        cbMood.setValue(3);
        cbStress.setValue(3);
        cbEnergy.setValue(3);
        tfSleepHours.setText("8.0");
        taNote.clear();
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
}