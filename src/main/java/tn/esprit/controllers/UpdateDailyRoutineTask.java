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
import tn.esprit.entities.DailyRoutineTask;
import tn.esprit.entities.User;
import tn.esprit.services.ServiceDailyRoutineTask;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateDailyRoutineTask {

    @FXML private Label lblId;
    @FXML private ComboBox<Integer> cbUserId;
    @FXML private TextField tfTitle;
    @FXML private CheckBox chkCompleted;
    @FXML private Label lblCompletedAt;
    @FXML private Label lblCreatedAt;

    // NEW: User search fields
    @FXML private TextField searchUserField;
    @FXML private ListView<String> userSuggestionsList;
    @FXML private Label selectedUserLabel;
    @FXML private Button clearUserBtn;

    private DailyRoutineTask task;
    private final ServiceDailyRoutineTask serviceTask = new ServiceDailyRoutineTask();
    private final UserService serviceUser = new UserService();

    // Data lists
    private List<User> allUsers = new ArrayList<>();
    private User selectedUser = null;

    // Search delay
    private PauseTransition userSearchDelay = new PauseTransition(Duration.millis(300));

    @FXML
    public void initialize() {
        loadAllUsers();
        setupUserSearch();
        loadUserIds(); // Keep for backward compatibility

        // Hide suggestion list initially
        if (userSuggestionsList != null) {
            userSuggestionsList.setVisible(false);
            userSuggestionsList.setManaged(false);
        }

        // Initialize selection label
        if (selectedUserLabel != null) {
            selectedUserLabel.setText("Aucun utilisateur sélectionné");
        }

        if (clearUserBtn != null) {
            clearUserBtn.setVisible(false);
            clearUserBtn.setManaged(false);
        }
    }

    private void loadAllUsers() {
        try {
            allUsers = serviceUser.getAll(); // Make sure this method exists
        } catch (Exception e) {
            showValidationError("Impossible de charger les utilisateurs: " + e.getMessage());
        }
    }

    private void setupUserSearch() {
        if (searchUserField == null) return;

        searchUserField.textProperty().addListener((observable, oldValue, newValue) -> {
            userSearchDelay.setOnFinished(event -> searchUsers(newValue));
            userSearchDelay.playFromStart();

            // Show clear button if there's text
            if (clearUserBtn != null) {
                clearUserBtn.setVisible(!newValue.isEmpty());
                clearUserBtn.setManaged(!newValue.isEmpty());
            }
        });

        // Handle selection from suggestions
        if (userSuggestionsList != null) {
            userSuggestionsList.setOnMouseClicked(event -> {
                String selected = userSuggestionsList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    selectUser(selected);
                }
            });
        }

        // Clear user selection
        if (clearUserBtn != null) {
            clearUserBtn.setOnAction(event -> clearUserSelection());
        }

        // Handle Enter key
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

    private void searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            if (userSuggestionsList != null) {
                userSuggestionsList.setVisible(false);
                userSuggestionsList.setManaged(false);
            }
            return;
        }

        String lowerQuery = query.toLowerCase().trim();

        // Search users by name or email
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

    private void selectUser(String selectedText) {
        // Extract user name from selection
        String userName = selectedText.split(" 📧 ")[0];

        // Find the user
        selectedUser = allUsers.stream()
                .filter(u -> u.getNom().equals(userName))
                .findFirst()
                .orElse(null);

        if (selectedUser != null) {
            if (selectedUserLabel != null) {
                selectedUserLabel.setText("👤 Sélectionné: " + selectedUser.getNom() + " (" + selectedUser.getEmail() + ")");
                selectedUserLabel.setStyle("-fx-text-fill: #3A7D6B; -fx-font-weight: bold;");
            }

            // Set the ComboBox value (for backward compatibility)
            cbUserId.setValue(selectedUser.getId());

            // Hide suggestions
            if (userSuggestionsList != null) {
                userSuggestionsList.setVisible(false);
                userSuggestionsList.setManaged(false);
            }

            if (searchUserField != null) {
                searchUserField.setText(selectedUser.getNom());
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

    public void initData(DailyRoutineTask task) {
        this.task = task;

        lblId.setText("ID: " + task.getId());

        // Find and select the user for this task
        if (allUsers != null) {
            selectedUser = allUsers.stream()
                    .filter(u -> u.getId() == task.getUserId())
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

        cbUserId.setValue(task.getUserId());
        tfTitle.setText(task.getTitle());
        chkCompleted.setSelected(task.isCompleted());
        lblCompletedAt.setText(task.getCompletedAt() != null ? task.getCompletedAt() : "Non complétée");
        lblCreatedAt.setText(task.getCreatedAt());
    }

    @FXML
    void handleUpdate() {
        if (!validateInputs()) {
            return;
        }

        task.setUserId(cbUserId.getValue());
        task.setTitle(tfTitle.getText().trim());
        task.setCompleted(chkCompleted.isSelected());

        if (chkCompleted.isSelected() && task.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now().toString());
        } else if (!chkCompleted.isSelected()) {
            task.setCompletedAt(null);
        }

        try {
            serviceTask.update(task);

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setHeaderText(null);
            success.setContentText("✅ Tâche modifiée avec succès !");
            success.showAndWait();

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier la tâche: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        // Check if user is selected via search or ComboBox
        if (selectedUser == null && cbUserId.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur");
            return false;
        }

        // If user was selected via search but ComboBox isn't set, set it now
        if (selectedUser != null && cbUserId.getValue() == null) {
            cbUserId.setValue(selectedUser.getId());
        }

        if (tfTitle.getText() == null || tfTitle.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez saisir un titre");
            return false;
        }
        return true;
    }

    @FXML
    void goToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdminDashboard.fxml"));
            Stage stage = (Stage) lblId.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner au dashboard");
        }
    }

    @FXML
    void closeWindow() {
        Stage stage = (Stage) lblId.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
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
}