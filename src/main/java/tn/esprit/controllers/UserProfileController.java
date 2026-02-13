package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.entities.UserProfile;
import tn.esprit.services.UserProfileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserProfileController {

    // ============ HEADER ============
    @FXML private Label usernameHeaderLabel;

    // ============ INFOS USER ============
    @FXML private Label nomLabel;
    @FXML private Label emailLabel;
    @FXML private Label telephoneLabel;
    @FXML private Label roleLabel;
    @FXML private Label statusLabel;

    // ============ INFOS PROFIL (VIEW MODE) ============
    @FXML private Label bioLabel;
    @FXML private Label dateNaissanceLabel;
    @FXML private Label langueLabel;
    @FXML private Label confidentialiteLabel;
    @FXML private Label notificationsEmailLabel;
    @FXML private Label notificationsSmsLabel;
    @FXML private ImageView avatarImageView;
    @FXML private GridPane viewModeGrid;

    // ============ CHAMPS ÉDITION ============
    @FXML private VBox editModeVBox;
    @FXML private TextArea bioTextArea;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private ComboBox<String> langueComboBox;
    @FXML private ComboBox<String> confidentialiteComboBox;
    @FXML private CheckBox notificationsEmailCheckBox;
    @FXML private CheckBox notificationsSmsCheckBox;
    @FXML private Button uploadAvatarButton;

    // ============ BOUTONS ============
    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    // ============ VARIABLES ============
    private User currentUser;
    private UserProfile userProfile;
    private UserProfileService userProfileService = new UserProfileService();
    private boolean isEditMode = false;
    private File selectedAvatarFile;

    @FXML
    public void initialize() {
        System.out.println("✅ UserProfileController initialisé");

        // Initialiser les combobox
        if (langueComboBox != null) {
            langueComboBox.getItems().addAll("FR", "EN", "AR", "ES", "DE");
            langueComboBox.setValue("FR");
        }

        if (confidentialiteComboBox != null) {
            confidentialiteComboBox.getItems().addAll("PUBLIC", "PRIVATE", "FRIENDS_ONLY");
            confidentialiteComboBox.setValue("PUBLIC");
        }
    }

    public void setUser(User user) {
        this.currentUser = user;

        if (user != null) {
            this.userProfile = userProfileService.getByUserId(user.getId());
            if (this.userProfile == null) {
                this.userProfile = userProfileService.createDefaultProfile(user.getId());
            }
        }

        loadProfile();
        updateUIMode();
    }

    private void loadProfile() {
        if (currentUser == null || userProfile == null) return;

        try {
            // === HEADER ===
            if (usernameHeaderLabel != null) {
                usernameHeaderLabel.setText(currentUser.getNom());
            }

            // === INFOS USER ===
            if (nomLabel != null) nomLabel.setText(currentUser.getNom());
            if (emailLabel != null) emailLabel.setText(currentUser.getEmail());
            if (telephoneLabel != null) {
                telephoneLabel.setText(currentUser.getTelephone() != null ?
                        currentUser.getTelephone() : "Non renseigné");
            }

            // Rôle
            if (roleLabel != null) {
                String role = currentUser.getRole();
                switch (role) {
                    case "ADMIN":
                        roleLabel.setText("Administrateur");
                        roleLabel.setStyle("-fx-text-fill: #8E44AD; -fx-font-weight: bold;");
                        break;
                    case "PSY_COACH":
                        roleLabel.setText("Psychologue/Coach");
                        roleLabel.setStyle("-fx-text-fill: #3498DB; -fx-font-weight: bold;");
                        break;
                    case "PATIENT":
                        roleLabel.setText("Patient");
                        roleLabel.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                        break;
                    default:
                        roleLabel.setText(role);
                        break;
                }
            }

            // Statut
            if (statusLabel != null) {
                if (currentUser.isActif()) {
                    statusLabel.setText("Actif");
                    statusLabel.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                } else {
                    statusLabel.setText("Inactif");
                    statusLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                }
            }

            // === INFOS PROFIL ===
            // Bio
            if (bioLabel != null) {
                bioLabel.setText(userProfile.getBio() != null && !userProfile.getBio().isEmpty() ?
                        userProfile.getBio() : "Aucune biographie");
            }
            if (bioTextArea != null) {
                bioTextArea.setText(userProfile.getBio());
            }

            // Date naissance
            if (dateNaissanceLabel != null) {
                dateNaissanceLabel.setText(userProfile.getDateNaissanceFormatted() != null ?
                        userProfile.getDateNaissanceFormatted() : "Non renseignée");
            }
            if (dateNaissancePicker != null) {
                dateNaissancePicker.setValue(userProfile.getDateNaissance());
            }

            // Langue
            if (langueLabel != null) {
                langueLabel.setText(getLangueDisplay(userProfile.getLangue()));
            }
            if (langueComboBox != null) {
                langueComboBox.setValue(userProfile.getLangue() != null ?
                        userProfile.getLangue() : "FR");
            }

            // Confidentialité
            if (confidentialiteLabel != null) {
                confidentialiteLabel.setText(getConfidentialiteDisplay(userProfile.getParametresConfidentialite()));
                confidentialiteLabel.setStyle(getConfidentialiteStyle(userProfile.getParametresConfidentialite()));
            }
            if (confidentialiteComboBox != null) {
                confidentialiteComboBox.setValue(userProfile.getParametresConfidentialite() != null ?
                        userProfile.getParametresConfidentialite() : "PUBLIC");
            }

            // Notifications Email
            if (notificationsEmailLabel != null) {
                notificationsEmailLabel.setText(userProfile.isNotificationsEmail() ? "Activées" : "Désactivées");
                notificationsEmailLabel.setStyle(userProfile.isNotificationsEmail() ?
                        "-fx-text-fill: #27AE60;" : "-fx-text-fill: #E74C3C;");
            }
            if (notificationsEmailCheckBox != null) {
                notificationsEmailCheckBox.setSelected(userProfile.isNotificationsEmail());
            }

            // Notifications SMS
            if (notificationsSmsLabel != null) {
                notificationsSmsLabel.setText(userProfile.isNotificationsSms() ? "Activées" : "Désactivées");
                notificationsSmsLabel.setStyle(userProfile.isNotificationsSms() ?
                        "-fx-text-fill: #27AE60;" : "-fx-text-fill: #E74C3C;");
            }
            if (notificationsSmsCheckBox != null) {
                notificationsSmsCheckBox.setSelected(userProfile.isNotificationsSms());
            }

            // Avatar
            loadAvatar();

        } catch (Exception e) {
            System.out.println("❌ Erreur loadProfile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAvatar() {
        if (avatarImageView == null) return;

        try {
            if (userProfile != null && userProfile.getAvatar() != null) {
                File avatarFile = new File(userProfile.getAvatar());
                if (avatarFile.exists()) {
                    Image avatar = new Image(avatarFile.toURI().toString(), 120, 120, true, true);
                    avatarImageView.setImage(avatar);
                    return;
                }
            }
            // Image par défaut
            setDefaultAvatar();
        } catch (Exception e) {
            setDefaultAvatar();
        }
    }

    private void setDefaultAvatar() {
        try {
            Image defaultAvatar = new Image(getClass().getResourceAsStream("/images/default-avatar.png"));
            avatarImageView.setImage(defaultAvatar);
        } catch (Exception e) {
            System.out.println("⚠️ Image par défaut non trouvée");
        }
    }

    @FXML
    private void handleEdit() {
        isEditMode = true;
        updateUIMode();
    }

    @FXML
    private void handleSave() {
        try {
            // Mettre à jour le profil
            userProfile.setBio(bioTextArea.getText());
            userProfile.setLangue(langueComboBox.getValue());
            userProfile.setDateNaissance(dateNaissancePicker.getValue());
            userProfile.setNotificationsEmail(notificationsEmailCheckBox.isSelected());
            userProfile.setNotificationsSms(notificationsSmsCheckBox.isSelected());
            userProfile.setParametresConfidentialite(confidentialiteComboBox.getValue());

            // Gérer l'avatar
            if (selectedAvatarFile != null) {
                String avatarPath = saveAvatar(selectedAvatarFile);
                userProfile.setAvatar(avatarPath);
                selectedAvatarFile = null;
            }

            // Sauvegarder en base
            if (userProfileService.update(userProfile)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Profil mis à jour avec succès !");

                // Recharger le profil
                this.userProfile = userProfileService.getByUserId(currentUser.getId());
                loadProfile();

                // Quitter le mode édition
                isEditMode = false;
                updateUIMode();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la sauvegarde: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        isEditMode = false;
        selectedAvatarFile = null;
        loadProfile();
        updateUIMode();
    }

    @FXML
    private void handleUploadAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo de profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(avatarImageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                Image avatar = new Image(selectedFile.toURI().toString(), 120, 120, true, true);
                avatarImageView.setImage(avatar);
                selectedAvatarFile = selectedFile;
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'image");
            }
        }
    }

    private String saveAvatar(File sourceFile) throws IOException {
        String uploadDir = "uploads/avatars/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String extension = sourceFile.getName().substring(sourceFile.getName().lastIndexOf("."));
        String fileName = "avatar_" + currentUser.getId() + "_" + System.currentTimeMillis() + extension;
        File destFile = new File(uploadDir + fileName);

        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return destFile.getPath();
    }

    private void updateUIMode() {
        boolean showViewMode = !isEditMode;
        boolean showEditMode = isEditMode;

        // Mode visualisation
        if (viewModeGrid != null) {
            viewModeGrid.setVisible(showViewMode);
            viewModeGrid.setManaged(showViewMode);
        }

        // Mode édition
        if (editModeVBox != null) {
            editModeVBox.setVisible(showEditMode);
            editModeVBox.setManaged(showEditMode);
        }
        if (uploadAvatarButton != null) {
            uploadAvatarButton.setVisible(showEditMode);
            uploadAvatarButton.setManaged(showEditMode);
        }

        // Boutons
        if (editButton != null) {
            editButton.setVisible(showViewMode);
            editButton.setManaged(showViewMode);
        }
        if (saveButton != null) {
            saveButton.setVisible(showEditMode);
            saveButton.setManaged(showEditMode);
        }
        if (cancelButton != null) {
            cancelButton.setVisible(showEditMode);
            cancelButton.setManaged(showEditMode);
        }
    }

    @FXML
    private void handleBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameHeaderLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 600));
            stage.setTitle("PsyCoach Pro - Accueil");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner à l'accueil");
            e.printStackTrace();
        }
    }

    // ============ UTILITAIRES ============
    private String getLangueDisplay(String code) {
        if (code == null) return "Français";
        switch (code) {
            case "FR": return "Français";
            case "EN": return "Anglais";
            case "AR": return "Arabe";
            case "ES": return "Espagnol";
            case "DE": return "Allemand";
            default: return code;
        }
    }

    private String getConfidentialiteDisplay(String conf) {
        if (conf == null) return "Public";
        switch (conf) {
            case "PUBLIC": return "Public";
            case "PRIVATE": return "Privé";
            case "FRIENDS_ONLY": return "Amis uniquement";
            default: return conf;
        }
    }

    private String getConfidentialiteStyle(String conf) {
        if (conf == null) return "-fx-text-fill: #3498DB;";
        switch (conf) {
            case "PUBLIC": return "-fx-text-fill: #3498DB;";
            case "PRIVATE": return "-fx-text-fill: #E74C3C;";
            case "FRIENDS_ONLY": return "-fx-text-fill: #F39C12;";
            default: return "-fx-text-fill: #2C3E50;";
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}