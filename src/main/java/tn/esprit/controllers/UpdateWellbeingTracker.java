package tn.esprit.controllers;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

public class UpdateWellbeingTracker {

    @FXML private Label lblId;
    @FXML private ComboBox<Integer> cbUserId;
    @FXML private ComboBox<Integer> cbRoutineTaskId;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Integer> cbMood;
    @FXML private ComboBox<Integer> cbStress;
    @FXML private ComboBox<Integer> cbEnergy;
    @FXML private TextField tfSleepHours;
    @FXML private TextArea taNote;
    @FXML private Label lblCreatedAt;

    // NEW: Search fields for User
    @FXML private TextField searchUserField;
    @FXML private ListView<String> userSuggestionsList;
    @FXML private Label selectedUserLabel;
    @FXML private Button clearUserBtn;

    // NEW: Search fields for Task
    @FXML private TextField searchTaskField;
    @FXML private ListView<String> taskSuggestionsList;
    @FXML private Label selectedTaskLabel;
    @FXML private Button clearTaskBtn;

    private WellbeingTracker tracker;
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
        if (userSuggestionsList != null) {
            userSuggestionsList.setVisible(false);
            userSuggestionsList.setManaged(false);
        }
        if (taskSuggestionsList != null) {
            taskSuggestionsList.setVisible(false);
            taskSuggestionsList.setManaged(false);
        }

        // Initialize selection labels
        if (selectedUserLabel != null) {
            selectedUserLabel.setText("Aucun utilisateur sélectionné");
        }
        if (selectedTaskLabel != null) {
            selectedTaskLabel.setText("Aucune tâche sélectionnée");
        }

        if (clearUserBtn != null) {
            clearUserBtn.setVisible(false);
            clearUserBtn.setManaged(false);
        }
        if (clearTaskBtn != null) {
            clearTaskBtn.setVisible(false);
            clearTaskBtn.setManaged(false);
        }
    }

    private void loadAllData() {
        try {
            allUsers = serviceUser.getAll(); // Make sure this method exists
            allTasks = serviceTask.getAll();
        } catch (Exception e) {
            showValidationError("Impossible de charger les données: " + e.getMessage());
        }
    }

    private void setupUserSearch() {
        if (searchUserField == null) return;

        searchUserField.textProperty().addListener((observable, oldValue, newValue) -> {
            userSearchDelay.setOnFinished(event -> searchUsers(newValue));
            userSearchDelay.playFromStart();

            if (clearUserBtn != null) {
                clearUserBtn.setVisible(!newValue.isEmpty());
                clearUserBtn.setManaged(!newValue.isEmpty());
            }
        });

        if (userSuggestionsList != null) {
            userSuggestionsList.setOnMouseClicked(event -> {
                String selected = userSuggestionsList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    selectUser(selected);
                }
            });
        }

        if (clearUserBtn != null) {
            clearUserBtn.setOnAction(event -> clearUserSelection());
        }

        if (searchUserField != null) {
            searchUserField.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                    if (userSuggestionsList != null && !userSuggestionsList.getItems().isEmpty()) {
                        selectUser(userSuggestionsList.getItems().get(0));
                    }
                }
            });
        }
    }

    private void setupTaskSearch() {
        if (searchTaskField == null) return;

        searchTaskField.textProperty().addListener((observable, oldValue, newValue) -> {
            taskSearchDelay.setOnFinished(event -> searchTasks(newValue));
            taskSearchDelay.playFromStart();

            if (clearTaskBtn != null) {
                clearTaskBtn.setVisible(!newValue.isEmpty());
                clearTaskBtn.setManaged(!newValue.isEmpty());
            }
        });

        if (taskSuggestionsList != null) {
            taskSuggestionsList.setOnMouseClicked(event -> {
                String selected = taskSuggestionsList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    selectTask(selected);
                }
            });
        }

        if (clearTaskBtn != null) {
            clearTaskBtn.setOnAction(event -> clearTaskSelection());
        }

        if (searchTaskField != null) {
            searchTaskField.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                    if (taskSuggestionsList != null && !taskSuggestionsList.getItems().isEmpty()) {
                        selectTask(taskSuggestionsList.getItems().get(0));
                    }
                }
            });
        }
    }

    private void searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            if (userSuggestionsList != null) {
                userSuggestionsList.setVisible(false);
                userSuggestionsList.setManaged(false);
            }
            return;
        }

        String lowerQuery = query.toLowerCase().trim();

        List<String> suggestions = allUsers.stream()
                .filter(user -> user.getNom().toLowerCase().contains(lowerQuery) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerQuery)))
                .map(user -> user.getNom() + " 📧 " + user.getEmail())
                .limit(10)
                .collect(Collectors.toList());

        if (!suggestions.isEmpty() && userSuggestionsList != null) {
            userSuggestionsList.getItems().setAll(suggestions);
            userSuggestionsList.setVisible(true);
            userSuggestionsList.setManaged(true);
        } else if (userSuggestionsList != null) {
            userSuggestionsList.setVisible(false);
            userSuggestionsList.setManaged(false);
        }
    }

    private void searchTasks(String query) {
        if (query == null || query.trim().isEmpty()) {
            if (taskSuggestionsList != null) {
                taskSuggestionsList.setVisible(false);
                taskSuggestionsList.setManaged(false);
            }
            return;
        }

        String lowerQuery = query.toLowerCase().trim();

        List<String> suggestions = allTasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(lowerQuery))
                .map(task -> "📌 " + task.getTitle() + " (User: " + task.getUserId() + ")")
                .limit(10)
                .collect(Collectors.toList());

        if (!suggestions.isEmpty() && taskSuggestionsList != null) {
            taskSuggestionsList.getItems().setAll(suggestions);
            taskSuggestionsList.setVisible(true);
            taskSuggestionsList.setManaged(true);
        } else if (taskSuggestionsList != null) {
            taskSuggestionsList.setVisible(false);
            taskSuggestionsList.setManaged(false);
        }
    }

    private void selectUser(String selectedText) {
        String userName = selectedText.split(" 📧 ")[0];

        selectedUser = allUsers.stream()
                .filter(u -> u.getNom().equals(userName))
                .findFirst()
                .orElse(null);

        if (selectedUser != null) {
            if (selectedUserLabel != null) {
                selectedUserLabel.setText("👤 Sélectionné: " + selectedUser.getNom() + " (" + selectedUser.getEmail() + ")");
                selectedUserLabel.setStyle("-fx-text-fill: #3A7D6B; -fx-font-weight: bold;");
            }

            cbUserId.setValue(selectedUser.getId());

            if (userSuggestionsList != null) {
                userSuggestionsList.setVisible(false);
                userSuggestionsList.setManaged(false);
            }

            if (searchUserField != null) {
                searchUserField.setText(selectedUser.getNom());
            }
        }
    }

    private void selectTask(String selectedText) {
        String taskTitle = selectedText.replace("📌 ", "").split(" \\(User:")[0];

        selectedTask = allTasks.stream()
                .filter(t -> t.getTitle().equals(taskTitle))
                .findFirst()
                .orElse(null);

        if (selectedTask != null) {
            if (selectedTaskLabel != null) {
                selectedTaskLabel.setText("✅ Sélectionnée: " + selectedTask.getTitle());
                selectedTaskLabel.setStyle("-fx-text-fill: #3A7D6B; -fx-font-weight: bold;");
            }

            cbRoutineTaskId.setValue(selectedTask.getId());

            if (taskSuggestionsList != null) {
                taskSuggestionsList.setVisible(false);
                taskSuggestionsList.setManaged(false);
            }

            if (searchTaskField != null) {
                searchTaskField.setText(selectedTask.getTitle());
            }
        }
    }

    private void clearUserSelection() {
        selectedUser = null;

        if (selectedUserLabel != null) {
            selectedUserLabel.setText("Aucun utilisateur sélectionné");
            selectedUserLabel.setStyle("-fx-text-fill: #7f8c8d;");
        }

        if (searchUserField != null) {
            searchUserField.clear();
        }

        cbUserId.setValue(null);

        if (userSuggestionsList != null) {
            userSuggestionsList.setVisible(false);
            userSuggestionsList.setManaged(false);
        }

        if (clearUserBtn != null) {
            clearUserBtn.setVisible(false);
            clearUserBtn.setManaged(false);
        }
    }

    private void clearTaskSelection() {
        selectedTask = null;

        if (selectedTaskLabel != null) {
            selectedTaskLabel.setText("Aucune tâche sélectionnée");
            selectedTaskLabel.setStyle("-fx-text-fill: #7f8c8d;");
        }

        if (searchTaskField != null) {
            searchTaskField.clear();
        }

        cbRoutineTaskId.setValue(null);

        if (taskSuggestionsList != null) {
            taskSuggestionsList.setVisible(false);
            taskSuggestionsList.setManaged(false);
        }

        if (clearTaskBtn != null) {
            clearTaskBtn.setVisible(false);
            clearTaskBtn.setManaged(false);
        }
    }

    private void loadDefaultValues() {
        cbMood.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbStress.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbEnergy.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
    }

    public void initData(WellbeingTracker tracker) {
        this.tracker = tracker;

        lblId.setText("ID: " + tracker.getId());

        // Find and select the user for this tracker
        if (allUsers != null) {
            selectedUser = allUsers.stream()
                    .filter(u -> u.getId() == tracker.getUserId())
                    .findFirst()
                    .orElse(null);

            if (selectedUser != null && selectedUserLabel != null) {
                selectedUserLabel.setText("👤 Sélectionné: " + selectedUser.getNom() + " (" + selectedUser.getEmail() + ")");
                selectedUserLabel.setStyle("-fx-text-fill: #3A7D6B; -fx-font-weight: bold;");

                if (searchUserField != null) {
                    searchUserField.setText(selectedUser.getNom());
                }
            }
        }

        // Find and select the task for this tracker
        if (allTasks != null) {
            selectedTask = allTasks.stream()
                    .filter(t -> t.getId() == tracker.getRoutineTaskId())
                    .findFirst()
                    .orElse(null);

            if (selectedTask != null && selectedTaskLabel != null) {
                selectedTaskLabel.setText("✅ Sélectionnée: " + selectedTask.getTitle());
                selectedTaskLabel.setStyle("-fx-text-fill: #3A7D6B; -fx-font-weight: bold;");

                if (searchTaskField != null) {
                    searchTaskField.setText(selectedTask.getTitle());
                }
            }
        }

        cbUserId.setValue(tracker.getUserId());
        cbRoutineTaskId.setValue(tracker.getRoutineTaskId());
        datePicker.setValue(LocalDate.parse(tracker.getDate()));
        cbMood.setValue(tracker.getMood());
        cbStress.setValue(tracker.getStress());
        cbEnergy.setValue(tracker.getEnergy());
        tfSleepHours.setText(String.valueOf(tracker.getSleepHours()));
        taNote.setText(tracker.getNote());
        lblCreatedAt.setText("Créé le: " + tracker.getCreatedAt());
    }

    @FXML
    public void handleUpdate() {
        if (!validateForm()) {
            return;
        }

        updateTrackerData();

        try {
            serviceTracker.update(tracker);
            showSuccessMessage();
            closeWindow();
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la modification");
        }
    }

    private boolean validateForm() {
        // Check user
        if (selectedUser == null && cbUserId.getValue() == null) {
            showValidationError("Veuillez sélectionner un utilisateur");
            return false;
        }
        if (selectedUser != null && cbUserId.getValue() == null) {
            cbUserId.setValue(selectedUser.getId());
        }

        // Check task
        if (selectedTask == null && cbRoutineTaskId.getValue() == null) {
            showValidationError("Veuillez sélectionner une tâche");
            return false;
        }
        if (selectedTask != null && cbRoutineTaskId.getValue() == null) {
            cbRoutineTaskId.setValue(selectedTask.getId());
        }

        if (datePicker.getValue() == null) {
            showValidationError("Veuillez sélectionner une date");
            return false;
        }
        if (cbMood.getValue() == null) {
            showValidationError("Veuillez sélectionner une humeur");
            return false;
        }
        if (cbStress.getValue() == null) {
            showValidationError("Veuillez sélectionner un stress");
            return false;
        }
        if (cbEnergy.getValue() == null) {
            showValidationError("Veuillez sélectionner une énergie");
            return false;
        }
        if (tfSleepHours.getText() == null || tfSleepHours.getText().trim().isEmpty()) {
            showValidationError("Veuillez saisir les heures de sommeil");
            return false;
        }

        try {
            double hours = Double.parseDouble(tfSleepHours.getText().trim());
            if (hours < 0 || hours > 24) {
                showValidationError("Les heures doivent être entre 0 et 24");
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Format d'heures invalide");
            return false;
        }

        return true;
    }

    private void updateTrackerData() {
        tracker.setUserId(cbUserId.getValue());
        tracker.setRoutineTaskId(cbRoutineTaskId.getValue());
        tracker.setDate(datePicker.getValue().toString());
        tracker.setMood(cbMood.getValue());
        tracker.setStress(cbStress.getValue());
        tracker.setEnergy(cbEnergy.getValue());
        tracker.setSleepHours(Double.parseDouble(tfSleepHours.getText().trim()));
        tracker.setNote(taNote.getText());
    }

    @FXML
    public void goToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
            Stage stage = (Stage) lblId.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            showValidationError("Impossible de retourner au dashboard");
        }
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) lblId.getScene().getWindow();
        stage.close();
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("✅ Suivi modifié avec succès !");
        alert.showAndWait();
    }

    private void loadUserIds() {
        try {
            List<Integer> userIds = serviceUser.getAllUsersIds();
            cbUserId.setItems(FXCollections.observableArrayList(userIds));
        } catch (Exception e) {
            showValidationError("Impossible de charger les utilisateurs");
        }
    }

    private void loadTaskIds() {
        try {
            List<Integer> taskIds = serviceTask.getAllTaskIds();
            cbRoutineTaskId.setItems(FXCollections.observableArrayList(taskIds));
        } catch (Exception e) {
            showValidationError("Impossible de charger les tâches");
        }
    }
}