package tn.esprit.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserListController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nomColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> telephoneColumn;
    @FXML private TableColumn<User, String> actifColumn;
    @FXML private TableColumn<User, Void> actionsColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label inactiveUsersLabel;
    @FXML private Label adminCountLabel;
    @FXML private Label psyCountLabel;
    @FXML private Label patientCountLabel;
    @FXML private Button clearFiltersButton;
    @FXML private Label searchInfoLabel;

    private UserService userService;
    private ObservableList<User> originalUserList;
    private FilteredList<User> filteredData;
    private SortedList<User> sortedData;

    @FXML
    public void initialize() {
        userService = new UserService();
        originalUserList = FXCollections.observableArrayList();

        // IMPORTANT: Initialiser filteredData AVANT de l'utiliser
        filteredData = new FilteredList<>(originalUserList, p -> true);
        sortedData = new SortedList<>(filteredData);

        setupTableColumns();
        setupFilters();
        loadUsers();
        setupSearchAndFilter();
        updateStats();
    }

    /**
     * Configuration des colonnes du tableau
     */
    private void setupTableColumns() {
        // Colonne ID
        idColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        // Colonne Nom
        nomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNom()));

        // Colonne Email
        emailColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmail()));

        // Colonne Rôle
        roleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole()));

        roleColumn.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox roleBox = new HBox(5);
                    Label iconLabel = new Label();
                    Label textLabel = new Label();

                    switch (item) {
                        case "ADMIN":
                            iconLabel.setText("👑");
                            textLabel.setText("Administrateur");
                            setStyle("-fx-text-fill: #8E44AD; -fx-font-weight: bold;");
                            break;
                        case "PSY_COACH":
                            iconLabel.setText("🧠");
                            textLabel.setText("Psychologue/Coach");
                            setStyle("-fx-text-fill: #3498DB; -fx-font-weight: bold;");
                            break;
                        case "PATIENT":
                            iconLabel.setText("🤝");
                            textLabel.setText("Patient");
                            setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                            break;
                        default:
                            iconLabel.setText("👤");
                            textLabel.setText(item);
                            break;
                    }

                    roleBox.getChildren().addAll(iconLabel, textLabel);
                    setGraphic(roleBox);
                    setText(null);
                }
            }
        });

        // Colonne Téléphone
        telephoneColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTelephone() != null ?
                        cellData.getValue().getTelephone() : "Non renseigné"));

        // Colonne Statut
        actifColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isActif() ? "Actif" : "Inactif"));

        actifColumn.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox statusBox = new HBox(5);
                    Label iconLabel = new Label();
                    Label textLabel = new Label(item);

                    if (item.equals("Actif")) {
                        iconLabel.setText("✅");
                        statusBox.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                    } else {
                        iconLabel.setText("❌");
                        statusBox.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                    }

                    statusBox.getChildren().addAll(iconLabel, textLabel);
                    setGraphic(statusBox);
                    setText(null);
                }
            }
        });

        // Colonne Actions
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✏️");
            private final Button deleteButton = new Button("🗑️");
            private final Button profileButton = new Button("👤");
            private final HBox actionBox = new HBox(8, editButton, profileButton, deleteButton);

            {
                String buttonStyle = "-fx-background-color: transparent; -fx-font-size: 16; -fx-cursor: hand; -fx-padding: 5; -fx-background-radius: 5;";
                editButton.setStyle(buttonStyle + "-fx-text-fill: #3498DB;");
                deleteButton.setStyle(buttonStyle + "-fx-text-fill: #E74C3C;");
                profileButton.setStyle(buttonStyle + "-fx-text-fill: #9B59B6;");

                editButton.setTooltip(new Tooltip("Modifier l'utilisateur"));
                deleteButton.setTooltip(new Tooltip("Supprimer l'utilisateur"));
                profileButton.setTooltip(new Tooltip("Voir le profil"));

                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleEditUser(user);
                });

                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user);
                });

                profileButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleViewProfile(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBox);
            }
        });
    }

    /**
     * Configuration des filtres
     */
    private void setupFilters() {
        roleFilterCombo.setItems(FXCollections.observableArrayList(
                "Tous les rôles", "Administrateur", "Psychologue/Coach", "Patient"
        ));
        roleFilterCombo.setValue("Tous les rôles");

        statusFilterCombo.setItems(FXCollections.observableArrayList(
                "Tous les statuts", "Actifs uniquement", "Inactifs uniquement"
        ));
        statusFilterCombo.setValue("Tous les statuts");

        searchField.setPromptText("🔍 Rechercher par nom, email ou téléphone...");
        clearFiltersButton.setTooltip(new Tooltip("Effacer tous les filtres"));
    }

    /**
     * Configuration de la recherche et des filtres
     */
    private void setupSearchAndFilter() {
        sortedData.comparatorProperty().bind(userTable.comparatorProperty());
        userTable.setItems(sortedData);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilterPredicate();
            updateSearchInfo();
        });

        roleFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateFilterPredicate();
            updateSearchInfo();
        });

        statusFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateFilterPredicate();
            updateSearchInfo();
        });
    }

    /**
     * Met à jour le prédicat de filtrage
     */
    private void updateFilterPredicate() {
        filteredData.setPredicate(user -> {
            boolean matches = true;

            String searchText = searchField.getText().toLowerCase().trim();
            if (!searchText.isEmpty()) {
                matches = user.getNom().toLowerCase().contains(searchText) ||
                        user.getEmail().toLowerCase().contains(searchText) ||
                        (user.getTelephone() != null && user.getTelephone().toLowerCase().contains(searchText));
                if (!matches) return false;
            }

            String selectedRole = roleFilterCombo.getValue();
            if (selectedRole != null && !selectedRole.equals("Tous les rôles")) {
                String roleToMatch = switch (selectedRole) {
                    case "Administrateur" -> "ADMIN";
                    case "Psychologue/Coach" -> "PSY_COACH";
                    case "Patient" -> "PATIENT";
                    default -> "";
                };
                matches = user.getRole().equals(roleToMatch);
                if (!matches) return false;
            }

            String selectedStatus = statusFilterCombo.getValue();
            if (selectedStatus != null && !selectedStatus.equals("Tous les statuts")) {
                boolean wantActive = selectedStatus.equals("Actifs uniquement");
                matches = user.isActif() == wantActive;
                if (!matches) return false;
            }

            return true;
        });

        updateStats();
    }

    /**
     * Met à jour les informations sur la recherche
     */
    private void updateSearchInfo() {
        if (filteredData == null) return;

        int totalFiltered = filteredData.size();
        int totalOriginal = originalUserList.size();

        if (searchInfoLabel != null) {
            if (totalFiltered < totalOriginal) {
                searchInfoLabel.setText(String.format("📋 %d résultat(s) sur %d utilisateur(s)", totalFiltered, totalOriginal));
                searchInfoLabel.setStyle("-fx-text-fill: #3A7D6B; -fx-font-weight: bold;");
            } else {
                searchInfoLabel.setText("📋 Tous les utilisateurs");
                searchInfoLabel.setStyle("-fx-text-fill: #6F9A8D;");
            }
        }
    }

    /**
     * Efface tous les filtres
     */
    @FXML
    private void handleClearFilters() {
        searchField.clear();
        roleFilterCombo.setValue("Tous les rôles");
        statusFilterCombo.setValue("Tous les statuts");
    }

    /**
     * Chargement de tous les utilisateurs
     */
    private void loadUsers() {
        try {
            List<User> users = userService.getAll();
            originalUserList.setAll(users);
            updateFilterPredicate();
            updateSearchInfo();
        } catch (SQLException e) {
            showError("Erreur de chargement", "Impossible de charger les utilisateurs : " + e.getMessage());
        }
    }

    /**
     * Mise à jour des statistiques
     */
    private void updateStats() {
        int total = originalUserList.size();
        long active = originalUserList.stream().filter(User::isActif).count();
        long inactive = total - active;

        long adminCount = originalUserList.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
        long psyCount = originalUserList.stream().filter(u -> "PSY_COACH".equals(u.getRole())).count();
        long patientCount = originalUserList.stream().filter(u -> "PATIENT".equals(u.getRole())).count();

        totalUsersLabel.setText("Total: " + total + " 👥");
        activeUsersLabel.setText("Actifs: " + active + " ✅");
        inactiveUsersLabel.setText("Inactifs: " + inactive + " ⏸️");

        adminCountLabel.setText("👑 Admins: " + adminCount);
        psyCountLabel.setText("🧠 Psy/Coach: " + psyCount);
        patientCountLabel.setText("🤝 Patients: " + patientCount);
    }

    @FXML
    private void handleSearch() {
        updateFilterPredicate();
    }

    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserFormView.fxml"));
            Parent root = loader.load();

            UserFormController controller = loader.getController();
            controller.setUserListController(this);
            controller.setUser(null);

            Stage stage = new Stage();
            stage.setTitle("Nouvel Utilisateur");
            stage.setScene(new Scene(root, 600, 750));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }

    private void handleEditUser(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserFormView.fxml"));
            Parent root = loader.load();

            UserFormController controller = loader.getController();
            controller.setUserListController(this);
            controller.setUser(user);

            Stage stage = new Stage();
            stage.setTitle("Modifier Utilisateur - " + user.getNom());
            stage.setScene(new Scene(root, 600, 750));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }

    private void handleDeleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'utilisateur : " + user.getNom());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?\nCette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.delete(user);
                loadUsers();
                showSuccess("✅ Utilisateur supprimé avec succès !");
            } catch (SQLException e) {
                showError("Erreur de suppression", "Impossible de supprimer l'utilisateur : " + e.getMessage());
            }
        }
    }

    private void handleViewProfile(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserProfileView.fxml"));
            Parent root = loader.load();

            UserProfileController controller = loader.getController();
            controller.setUser(user);

            Stage stage = new Stage();
            stage.setTitle("Profil de " + user.getNom());
            stage.setScene(new Scene(root, 500, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le profil : " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
        handleClearFilters();
        showSuccess("✅ Liste rafraîchie avec succès !");
    }

    public void refreshTable() {
        loadUsers();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}