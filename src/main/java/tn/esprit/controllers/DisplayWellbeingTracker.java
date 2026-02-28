package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.WellbeingTracker;
import tn.esprit.services.ServiceWellbeingTracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Modality;



// Add these imports at the top if not already there
import javafx.application.Platform;
import tn.esprit.services.WeatherService.WeatherData;
import tn.esprit.services.WeatherService;  // Missing!
import javafx.geometry.Insets;

// Add these imports at the top
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import tn.esprit.services.Search;
import tn.esprit.services.UserService;
import tn.esprit.entities.User;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class DisplayWellbeingTracker {

    @FXML private FlowPane cardsContainer;
    @FXML private Label lblCount;
    @FXML private Label lblAvgMood;
    @FXML private Label lblAvgSleep;
    @FXML private VBox weatherBox; // Make sure this exists in your FXML
    // Add these fields with your existing FXML fields
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private Button clearSearchBtn;
    @FXML private ListView<String> suggestionsListView;
    @FXML private ToggleButton filterAllBtn;
    @FXML private ToggleButton filterUserBtn;
    @FXML private ToggleButton filterMoodBtn;
    @FXML private ToggleButton filterStressBtn;
    @FXML private ToggleButton filterEnergyBtn;

    private final ServiceWellbeingTracker serviceTracker = new ServiceWellbeingTracker();
    private final WeatherService weatherService = new WeatherService();
    private static final String API_KEY = "a52860789d8833de9b306769d1270a8f";

    private final Search searchService = new Search();
    private final UserService userService = new UserService();
    private List<WellbeingTracker> allTrackers = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();
    private PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    @FXML
    public void initialize() {
        loadCards();
        showWeather();
        initializeSearch();
    }

    @FXML
    private void refresh() {
        loadCards();
        showWeather();
        initializeSearch();

    }

    private void loadCards() {
        allTrackers = serviceTracker.getAll();
        cardsContainer.getChildren().clear();
        try {
            List<WellbeingTracker> trackers = serviceTracker.getAll();
            lblCount.setText(trackers.size() + " suivi(s)");

            // Calculer les moyennes
            double avgMood = trackers.stream().mapToInt(WellbeingTracker::getMood).average().orElse(0);
            double avgSleep = trackers.stream().mapToDouble(WellbeingTracker::getSleepHours).average().orElse(0);

            lblAvgMood.setText(String.format("😊 Humeur moyenne: %.1f/5", avgMood));
            lblAvgSleep.setText(String.format("😴 Sommeil moyen: %.1fh", avgSleep));

            for (WellbeingTracker t : trackers) {
                cardsContainer.getChildren().add(createCard(t));
            }

        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les suivis: " + e.getMessage());
        }
        searchService.setData(new ArrayList<>(), allTrackers, allUsers);
    }

    private VBox createCard(WellbeingTracker t) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setStyle("""
            -fx-background-color: white;
            -fx-padding: 16;
            -fx-border-color: #E2E2E2;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);
            -fx-cursor: hand;
            """);

        // En-tête avec date
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label dateLabel = new Label("📅 " + t.getDate());
        dateLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnEdit = new Button("✏️");
        Button btnDelete = new Button("🗑️");

        btnEdit.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        btnDelete.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");

        btnEdit.setOnAction(e -> openUpdateWindow(t));
        btnDelete.setOnAction(e -> confirmDelete(t));

        header.getChildren().addAll(dateLabel, spacer, btnEdit, btnDelete);

        // Indicateurs (Mood, Stress, Energy, Sleep)
        HBox indicators = new HBox(15);
        indicators.setAlignment(Pos.CENTER_LEFT);

        Label mood = new Label("😊 " + t.getMood() + "/5");
        Label stress = new Label("😰 " + t.getStress() + "/5");
        Label energy = new Label("⚡ " + t.getEnergy() + "/5");
        Label sleep = new Label("😴 " + t.getSleepHours() + "h");

        String indicatorStyle = "-fx-background-color: #F0F0F0; -fx-padding: 4 10; -fx-background-radius: 20; -fx-text-fill: #2C3E50;";
        mood.setStyle(indicatorStyle);
        stress.setStyle(indicatorStyle);
        energy.setStyle(indicatorStyle);
        sleep.setStyle(indicatorStyle);

        indicators.getChildren().addAll(mood, stress, energy, sleep);

        // Note
        Label note = new Label(t.getNote() == null || t.getNote().isEmpty() ?
                "(aucune note)" : "📝 " + t.getNote());
        note.setWrapText(true);
        note.setStyle("-fx-text-fill: #555; -fx-font-style: italic;");

        // Métadonnées (User ID et Task ID)
        Label meta = new Label("👤 User: " + t.getUserId() + " | ✅ Task: " + t.getRoutineTaskId());
        meta.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 11;");

        card.getChildren().addAll(header, new Separator(), indicators, note, meta);
        return card;
    }

    private void confirmDelete(WellbeingTracker tracker) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer ce suivi du " + tracker.getDate() + " ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceTracker.delete(tracker.getId());
                    refresh();
                    showInfo("Succès", "✅ Suivi supprimé !");
                } catch (Exception e) {
                    showAlert("Erreur", "Impossible de supprimer le suivi");
                }
            }
        });
    }

    private void openUpdateWindow(WellbeingTracker tracker) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateWellbeingTracker.fxml"));
            Parent root = loader.load();

            UpdateWellbeingTracker controller = loader.getController();
            controller.initData(tracker);

            Stage stage = new Stage();
            stage.setTitle("Modifier suivi - " + tracker.getDate());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            refresh(); // Refresh after closing
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la modification: " + e.getMessage());
        }
    }
    @FXML
    private void goToAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddWellbeingTracker.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un suivi bien-être");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh after closing
            refresh();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'ajout: " + e.getMessage());
        }
    }
    @FXML
    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard Administrateur");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner au dashboard: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }










    // NEW: Weather display method
    private void showWeather() {
        // Check if weatherBox exists
        if (weatherBox == null) {
            System.out.println("weatherBox is null - check your FXML");
            return;
        }

        // Show loading
        weatherBox.getChildren().clear();
        ProgressIndicator loading = new ProgressIndicator();
        loading.setPrefSize(30, 30);
        weatherBox.getChildren().add(loading);

        // Run API call in background thread
        new Thread(() -> {
            try {
                // Try multiple cities
                WeatherData weather = weatherService.getWeatherForCity("Tunis");

                if (weather == null) {
                    weather = weatherService.getWeatherForCity("London");
                }
                if (weather == null) {
                    weather = weatherService.getWeatherForCity("Paris");
                }

                final WeatherData finalWeather = weather;

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    weatherBox.getChildren().clear();

                    if (finalWeather != null) {
                        VBox weatherInfo = new VBox(8);
                        weatherInfo.setPadding(new Insets(15));
                        weatherInfo.setStyle(
                                "-fx-background-color: linear-gradient(to right, #3498db, #2980b9);" +
                                        "-fx-background-radius: 10;" +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);"
                        );

                        Label cityLabel = new Label("🌍 " + finalWeather.city);
                        cityLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

                        Label tempLabel = new Label(String.format("🌡️ %.1f°C", finalWeather.temperature));
                        tempLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");

                        Label conditionLabel = new Label("☁️ " + finalWeather.description);
                        conditionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

                        Label humidityLabel = new Label("💧 " + finalWeather.humidity + "% humidity");
                        humidityLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");

                        Label recLabel = new Label("💡 " + finalWeather.recommendation);
                        recLabel.setWrapText(true);
                        recLabel.setStyle(
                                "-fx-text-fill: #2c3e50;" +
                                        "-fx-background-color: white;" +
                                        "-fx-padding: 10;" +
                                        "-fx-background-radius: 8;" +
                                        "-fx-font-weight: bold;"
                        );

                        weatherInfo.getChildren().addAll(cityLabel, tempLabel, conditionLabel,
                                humidityLabel, new Separator(), recLabel);
                        weatherBox.getChildren().add(weatherInfo);

                    } else {
                        Label errorLabel = new Label("🌤️ Weather unavailable");
                        errorLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
                        weatherBox.getChildren().add(errorLabel);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    weatherBox.getChildren().clear();
                    Label errorLabel = new Label("⚠️ Weather service unavailable");
                    errorLabel.setStyle("-fx-text-fill: #e74c3c;");
                    weatherBox.getChildren().add(errorLabel);
                });
            }
        }).start();
    }
    private void initializeSearch() {
        // Load users for search
        try {
            allUsers = userService.getAll(); // You need to implement this method
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setup search field listener with delay (to avoid searching on every keystroke)
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
            if (event.getCode() == KeyCode.ENTER) {
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
        ToggleButton[] filters = {filterAllBtn, filterUserBtn, filterMoodBtn, filterStressBtn, filterEnergyBtn};
        for (ToggleButton filter : filters) {
            filter.setOnAction(event -> performSearch(searchField.getText()));
        }
    }
    private void updateSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) {
            suggestionsListView.setVisible(false);
            suggestionsListView.setManaged(false);
            return;
        }

        // Get suggestions from search service
        List<String> suggestions = searchService.getSuggestions(query, Search.SearchFilter.WELLBEING);

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
        // Hide suggestions
        suggestionsListView.setVisible(false);
        suggestionsListView.setManaged(false);

        // Determine filter
        Search.SearchFilter filter = Search.SearchFilter.ALL;
        if (filterUserBtn.isSelected()) filter = Search.SearchFilter.USERS;
        else if (filterMoodBtn.isSelected()) filter = Search.SearchFilter.WELLBEING;
        else if (filterStressBtn.isSelected()) filter = Search.SearchFilter.WELLBEING;
        else if (filterEnergyBtn.isSelected()) filter = Search.SearchFilter.WELLBEING;

        // Perform search
        List<Search.SearchResult> results = searchService.search(query, filter);

        // Update display
        updateDisplayWithResults(results);
    }

    private void updateDisplayWithResults(List<Search.SearchResult> results) {
        cardsContainer.getChildren().clear();

        if (results.isEmpty()) {
            // Show "no results" message
            Label noResults = new Label("🔍 Aucun suivi trouvé pour \"" + searchField.getText() + "\"");
            noResults.setStyle("-fx-font-size: 16; -fx-text-fill: #7f8c8d; -fx-padding: 50;");
            cardsContainer.getChildren().add(noResults);
            lblCount.setText("0 résultat(s)");
            return;
        }

        // Separate results by type
        List<Search.SearchResult> wellbeingResults = new ArrayList<>();
        List<Search.SearchResult> userResults = new ArrayList<>();

        for (Search.SearchResult result : results) {
            if (result.entityType.equals("WELLBEING")) {
                wellbeingResults.add(result);
            } else if (result.entityType.equals("USER")) {
                userResults.add(result);
            }
        }

        System.out.println("Displaying: " + userResults.size() + " users, " + wellbeingResults.size() + " wellbeing entries");

        // Display user results first
        for (Search.SearchResult userResult : userResults) {
            // Create a special card for users
            VBox userCard = createUserResultCard(userResult);
            cardsContainer.getChildren().add(userCard);

            // Also add their wellbeing entries
            for (WellbeingTracker tracker : userResult.userWellbeing) {
                cardsContainer.getChildren().add(createCard(tracker));
            }
        }

        // Display individual wellbeing results
        for (Search.SearchResult wellbeingResult : wellbeingResults) {
            WellbeingTracker tracker = (WellbeingTracker) wellbeingResult.entity;

            // Check if this tracker's user was already displayed
            boolean userAlreadyDisplayed = userResults.stream()
                    .anyMatch(u -> u.userId == tracker.getUserId());

            if (!userAlreadyDisplayed) {
                cardsContainer.getChildren().add(createCard(tracker));
            }
        }

        lblCount.setText(wellbeingResults.size() + " suivi(s) trouvé(s)");
    }

    private VBox createUserResultCard(Search.SearchResult userResult) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #9B59B6; " +
                "-fx-background-radius: 12; " +
                "-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        Label userLabel = new Label("👤 " + userResult.title);
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

        Label emailLabel = new Label("📧 " + userResult.subtitle);
        emailLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");

        Label statsLabel = new Label("📊 " + userResult.userWellbeing.size() + " suivis bien-être");
        statsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");

        // Add mood stats if available
        if (!userResult.userWellbeing.isEmpty()) {
            double avgMood = userResult.userWellbeing.stream()
                    .mapToInt(WellbeingTracker::getMood)
                    .average()
                    .orElse(0);
            Label moodLabel = new Label(String.format("😊 Humeur moyenne: %.1f/5", avgMood));
            moodLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");
            card.getChildren().add(moodLabel);
        }

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
        loadCards(); // Reload all cards
    }


    @FXML
    private void openSelfCareChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Chat.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Coach Équilibre - Bien-être");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Optional: makes it modal
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le coach bien-être");
        }
    }

}