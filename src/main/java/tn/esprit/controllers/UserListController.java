package tn.esprit.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label inactiveUsersLabel;

    private UserService userService;
    private ObservableList<User> userList;

    @FXML
    public void initialize() {
        userService = new UserService();
        userList = FXCollections.observableArrayList();

        setupTableColumns();
        loadUsers();
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

        // Colonne Rôle avec affichage personnalisé
        roleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole()));

        roleColumn.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    switch (item) {
                        case "ADMIN":
                            setText("Administrateur");
                            setStyle("-fx-text-fill: #8E44AD; -fx-font-weight: bold;");
                            break;
                        case "PSY_COACH":
                            setText("Psychologue/Coach");
                            setStyle("-fx-text-fill: #3498DB; -fx-font-weight: bold;");
                            break;
                        case "PATIENT":
                            setText("Patient");
                            setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                            break;
                        default:
                            setText(item);
                            break;
                    }
                }
            }
        });

        // Colonne Téléphone
        telephoneColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTelephone() != null ?
                        cellData.getValue().getTelephone() : "Non renseigné"));

        // Colonne Statut (Actif/Inactif)
        actifColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isActif() ? "✓ Actif" : "✗ Inactif"));

        actifColumn.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Actif")) {
                        setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Colonne Actions (Boutons Modifier, Profil, Supprimer)
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✏️ Modifier");
            private final Button deleteButton = new Button("🗑️ Supprimer");
            private final Button profileButton = new Button("👤 Profil");
            private final HBox actionBox = new HBox(5, editButton, profileButton, deleteButton);

            {
                // Style des boutons
                editButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 11; -fx-background-radius: 3;");
                deleteButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 11; -fx-background-radius: 3;");
                profileButton.setStyle("-fx-background-color: #9B59B6; -fx-text-fill: white; -fx-font-size: 11; -fx-background-radius: 3;");

                // Actions des boutons
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
     * Chargement de tous les utilisateurs
     */
    private void loadUsers() {
        try {
            List<User> users = userService.getAll();
            userList.setAll(users);
            userTable.setItems(userList);
        } catch (SQLException e) {
            showError("Erreur de chargement", "Impossible de charger les utilisateurs : " + e.getMessage());
        }
    }

    /**
     * Mise à jour des statistiques
     */
    private void updateStats() {
        int total = userList.size();
        long active = userList.stream().filter(User::isActif).count();
        long inactive = total - active;

        totalUsersLabel.setText("Total: " + total + " utilisateur" + (total > 1 ? "s" : ""));
        activeUsersLabel.setText("Actifs: " + active);
        inactiveUsersLabel.setText("Inactifs: " + inactive);
    }

    /**
     * Recherche d'utilisateurs
     */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            loadUsers();
            return;
        }

        try {
            List<User> allUsers = userService.getAll();
            List<User> filtered = allUsers.stream()
                    .filter(user ->
                            user.getNom().toLowerCase().contains(searchText) ||
                                    user.getEmail().toLowerCase().contains(searchText) ||
                                    (user.getTelephone() != null && user.getTelephone().contains(searchText)))
                    .toList();

            userList.setAll(filtered);
            userTable.setItems(userList);
            updateStats();
        } catch (SQLException e) {
            showError("Erreur de recherche", e.getMessage());
        }
    }

    /**
     * Ajouter un nouvel utilisateur
     */
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

    /**
     * Modifier un utilisateur
     */
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

    /**
     * Supprimer un utilisateur
     */
    private void handleDeleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'utilisateur : " + user.getNom());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?\nCette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.delete(user);
                userList.remove(user);
                updateStats();
                showSuccess("✅ Utilisateur supprimé avec succès !");
            } catch (SQLException e) {
                showError("Erreur de suppression", "Impossible de supprimer l'utilisateur : " + e.getMessage());
            }
        }
    }

    /**
     * Voir le profil d'un utilisateur
     */
    private void handleViewProfile(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserProfileView.fxml"));
            Parent root = loader.load();

            UserProfileController controller = loader.getController();
            controller.setUser(user);  // ✅ Maintenant ça fonctionne !

            Stage stage = new Stage();
            stage.setTitle("Profil de " + user.getNom());
            stage.setScene(new Scene(root, 500, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le profil : " + e.getMessage());
        }
    }

    /**
     * Rafraîchir la liste
     */
    @FXML
    private void handleRefresh() {
        loadUsers();
        updateStats();
        searchField.clear();
        showSuccess("✅ Liste rafraîchie avec succès !");
    }

    /**
     * Méthode publique pour rafraîchir le tableau (appelée depuis UserFormController)
     */
    public void refreshTable() {
        loadUsers();
        updateStats();
    }

    /**
     * Afficher une erreur
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Afficher un message de succès
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}