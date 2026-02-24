package tn.esprit.controllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.DailyRoutineTask;
import tn.esprit.entities.User;
import tn.esprit.services.Search;
import tn.esprit.services.ServiceDailyRoutineTask;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DisplayDailyRoutineTask {

    @FXML private FlowPane cardsContainer;
    @FXML private Label lblCount;
    @FXML private ToggleGroup filterGroup;

    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private Button clearSearchBtn;
    @FXML private ListView<String> suggestionsListView;
    @FXML private ToggleButton filterAllBtn;
    @FXML private ToggleButton filterTitleBtn;
    @FXML private ToggleButton filterUserBtn;
    @FXML private ToggleButton filterStatusBtn;

    private final ServiceDailyRoutineTask serviceTask = new ServiceDailyRoutineTask();
    private List<DailyRoutineTask> allTasks;
    private final Search searchService = new Search();
    private final UserService userService = new UserService();

    private List<DailyRoutineTask> allTasks2 = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();
    private PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    @FXML
    public void initialize() {
        loadTasks();

        initializeSearch();

        // Listener pour les filtres
        filterGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                applyFilter();
            }
        });
    }


    private void displayTasks(List<DailyRoutineTask> tasks) {
        cardsContainer.getChildren().clear();
        lblCount.setText(tasks.size() + " tâche(s)");

        for (DailyRoutineTask task : tasks) {
            VBox card = createTaskCard(task);
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createTaskCard(DailyRoutineTask task) {
        VBox card = new VBox(10);
        card.setPrefWidth(280);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-border-color: " + (task.isCompleted() ? "#2ECC71" : "#E0E0E0") + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Titre
        Label titleLabel = new Label("📌 " + task.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setWrapText(true);
        titleLabel.setTextFill(Color.BLACK); // Force black text

        // Statut
        Label statusLabel = new Label(task.isCompleted() ? "✅ Complétée" : "⏳ À faire");
        statusLabel.setTextFill(task.isCompleted() ? Color.web("#2ECC71") : Color.web("#F39C12"));
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Info - User ID only
        Label infoLabel = new Label("👤 User: " + task.getUserId());
        infoLabel.setFont(Font.font("System", 12));
        infoLabel.setTextFill(Color.DARKGRAY); // Changed from GRAY to DARKGRAY for better visibility

        // Dates
        Label createdLabel = new Label("📅 Créée: " + task.getCreatedAt());
        createdLabel.setFont(Font.font("System", 11));
        createdLabel.setTextFill(Color.DARKGRAY);

        card.getChildren().addAll(titleLabel, statusLabel, infoLabel, createdLabel);

        if (task.isCompleted() && task.getCompletedAt() != null) {
            Label completedLabel = new Label("✓ Complétée: " + task.getCompletedAt());
            completedLabel.setFont(Font.font("System", 11));
            completedLabel.setTextFill(Color.web("#2ECC71"));
            card.getChildren().add(completedLabel);
        }

        // Boutons d'action
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button btnEdit = new Button("✏️");
        btnEdit.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        btnEdit.setOnAction(e -> goToUpdate(task));

        Button btnDelete = new Button("🗑️");
        btnDelete.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> deleteTask(task));

        if (!task.isCompleted()) {
            Button btnComplete = new Button("✓");
            btnComplete.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
            btnComplete.setOnAction(e -> markCompleted(task));
            buttons.getChildren().add(btnComplete);
        }

        buttons.getChildren().addAll(btnEdit, btnDelete);
        card.getChildren().add(buttons);

        return card;
    }

    private void applyFilter() {
        RadioButton selected = (RadioButton) filterGroup.getSelectedToggle();
        String filter = selected.getText();

        List<DailyRoutineTask> filtered = allTasks2.stream()
                .filter(t -> {
                    switch (filter) {
                        case "À faire": return !t.isCompleted();
                        case "Complétées": return t.isCompleted();
                        default: return true;
                    }
                })
                .collect(Collectors.toList());

        displayTasks(filtered);
    }

    private void markCompleted(DailyRoutineTask task) {
        serviceTask.markAsCompleted(task.getId());
        refresh();
    }

    private void deleteTask(DailyRoutineTask task) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer cette tâche '" + task.getTitle() + "' ?");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                serviceTask.delete(task.getId());
                refresh();
            }
        });
    }

    private void goToUpdate(DailyRoutineTask task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateDailyRoutineTask.fxml"));
            Parent root = loader.load();

            UpdateDailyRoutineTask controller = loader.getController();
            controller.initData(task);

            Stage stage = new Stage();
            stage.setTitle("Modifier Tâche - " + task.getTitle());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh after closing
            refresh();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la modification");
        }
    }

    @FXML
    void goToAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddDailyRoutineTask.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nouvelle Tâche");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh after closing
            refresh();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire");
        }
    }

    @FXML
    void refresh() {
        loadTasks();
        clearSearch();
    }

    @FXML
    void goToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdminDashboard.fxml"));
            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner au dashboard");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    private void initializeSearch() {
        // Load users for search
        try {
            allUsers = userService.getAll(); // Make sure this method exists in UserService
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setup search field listener with delay
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(event -> performSearch(newValue));
            searchDelay.playFromStart();

            // Show/hide clear button
            clearSearchBtn.setVisible(!newValue.isEmpty());
            clearSearchBtn.setManaged(!newValue.isEmpty());

            // Update suggestions
            updateSuggestions(newValue);
        });

        // Search button action
        searchBtn.setOnAction(event -> performSearch(searchField.getText()));

        // Clear search button
        clearSearchBtn.setOnAction(event -> clearSearch());

        // Handle enter key in search field
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                performSearch(searchField.getText());
            }
        });

        // Suggestions list click handler
        suggestionsListView.setOnMouseClicked(event -> {
            String selected = suggestionsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                searchField.setText(selected);
                performSearch(selected);
                suggestionsListView.setVisible(false);
                suggestionsListView.setManaged(false);
            }
        });

        // Filter toggles
        ToggleButton[] filters = {filterAllBtn, filterTitleBtn, filterUserBtn, filterStatusBtn};
        for (ToggleButton filter : filters) {
            filter.setOnAction(event -> performSearch(searchField.getText()));
        }
    }

    private void updateDisplayWithResults(List<Search.SearchResult> results) {
        cardsContainer.getChildren().clear();

        if (results.isEmpty()) {
            // Show "no results" message
            Label noResults = new Label("🔍 Aucune tâche trouvée pour \"" + searchField.getText() + "\"");
            noResults.setStyle("-fx-font-size: 16; -fx-text-fill: #7f8c8d; -fx-padding: 50;");
            cardsContainer.getChildren().add(noResults);
            lblCount.setText("0 résultat(s)");
            return;
        }

        // Separate results by type
        List<Search.SearchResult> taskResults = new ArrayList<>();
        List<Search.SearchResult> userResults = new ArrayList<>();

        for (Search.SearchResult result : results) {
            if (result.entityType.equals("TASK")) {
                taskResults.add(result);
            } else if (result.entityType.equals("USER")) {
                userResults.add(result);
            }
        }

        // Display user results first (as headers or special cards)
        for (Search.SearchResult userResult : userResults) {
            // Create a special card for users showing their tasks
            VBox userCard = createUserResultCard(userResult);
            cardsContainer.getChildren().add(userCard);

            // Also add their tasks
            for (DailyRoutineTask task : userResult.userTasks) {
                cardsContainer.getChildren().add(createTaskCard(task));
            }
        }

        // Display individual task results (that don't belong to displayed users)
        for (Search.SearchResult taskResult : taskResults) {
            DailyRoutineTask task = (DailyRoutineTask) taskResult.entity;

            // Check if this task's user was already displayed
            boolean userAlreadyDisplayed = userResults.stream()
                    .anyMatch(u -> u.userId == task.getUserId());

            if (!userAlreadyDisplayed) {
                cardsContainer.getChildren().add(createTaskCard(task));
            }
        }

        lblCount.setText(taskResults.size() + " tâche(s) trouvée(s)");
    }

    private VBox createUserResultCard(Search.SearchResult userResult) {
        VBox card = new VBox(10);
        card.setPrefWidth(280);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #2980B9; " +
                "-fx-background-radius: 10; " +
                "-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        Label userLabel = new Label("👤 " + userResult.title);
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

        Label emailLabel = new Label("📧 " + userResult.subtitle);
        emailLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");

        Label statsLabel = new Label("📊 " + userResult.userTasks.size() + " tâches");
        statsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");

        card.getChildren().addAll(userLabel, emailLabel, statsLabel);

        return card;
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        clearSearchBtn.setVisible(false);
        clearSearchBtn.setManaged(false);
        suggestionsListView.setVisible(false);
        suggestionsListView.setManaged(false);
        loadTasks(); // Reload all tasks
    }
    private void loadTasks() {
        allTasks2 = serviceTask.getAll(); // Make sure this line exists
        displayTasks(allTasks2);

        // Update search service with new data
        searchService.setData(allTasks2, new ArrayList<>(), allUsers);
    }
    private void updateSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) {
            suggestionsListView.setVisible(false);
            suggestionsListView.setManaged(false);
            return;
        }

        // Make sure search service has latest data
        searchService.setData(allTasks2, new ArrayList<>(), allUsers);

        List<String> suggestions = searchService.getSuggestions(query, Search.SearchFilter.TASKS);

        if (!suggestions.isEmpty()) {
            suggestionsListView.getItems().setAll(suggestions);
            suggestionsListView.setVisible(true);
            suggestionsListView.setManaged(true);
        } else {
            suggestionsListView.setVisible(false);
            suggestionsListView.setManaged(false);
        }
    }

    private void performSearch(String query) {
        suggestionsListView.setVisible(false);
        suggestionsListView.setManaged(false);

        // Make sure search service has latest data
        searchService.setData(allTasks2, new ArrayList<>(), allUsers);

        // Determine filter
        Search.SearchFilter filter = Search.SearchFilter.ALL;
        if (filterTitleBtn.isSelected()) filter = Search.SearchFilter.TASKS;
        else if (filterUserBtn.isSelected()) filter = Search.SearchFilter.USERS;
        else if (filterStatusBtn.isSelected()) filter = Search.SearchFilter.TASKS;

        // Search
        List<Search.SearchResult> results = searchService.search(query, filter);

        // Update display
        updateDisplayWithResults(results);
    }

}