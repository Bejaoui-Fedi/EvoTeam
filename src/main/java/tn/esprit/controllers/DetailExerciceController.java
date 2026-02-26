package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import tn.esprit.entities.Exercise;
import tn.esprit.utils.Session;

import java.time.format.DateTimeFormatter;

public class DetailExerciceController {

    @FXML
    private Label lblTitle;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblType;
    @FXML
    private Label lblDuration;
    @FXML
    private Label lblDifficulty;
    @FXML
    private Label lblMedia;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblUser;
    @FXML
    private Label lblDates;

    @FXML
    private StackPane mediaContainer;

    @FXML
    private Button btnBack;

    // Admin actions
    @FXML
    private HBox boxAdminActions;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    private Exercise currentExercise;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        // Retour vers la liste des exercices (qui est technicquement
        // AffichageExercicesParObjectif)
        // Mais comme on a besoin de l'objectif ID pour revenir proprement, on va
        // utiliser le history ou juste re-naviguer
        // Simplification: retour arriere simple si possible, sinon on verra.
        // Ici on va assumer qu'on peut retourner vers la page précédente via
        // TemplateController si on avait un systeme de stack.
        // Comme on n'a pas de stack, on va devoir ruser ou juste retourner à la liste
        // globale (ou demander l'obj ID).
        // Solution simple: Le bouton retour renvoie vers AffichageObjectifs pour
        // l'instant, ou on essaie de recuperer l'objectif.
        // ALTERNATIVE MIEUX: On stocke l'ID objectif dans l'exercice donc on peut
        // recharger la page exercices.

        btnBack.setOnAction(e -> goBack());

        if (boxAdminActions != null) {
            boolean isAdmin = Session.isAdmin();
            boxAdminActions.setVisible(isAdmin);
            boxAdminActions.setManaged(isAdmin);

            if (btnEdit != null) {
                btnEdit.setOnAction(e -> TemplateController.navigate("/ModificationExercice.fxml",
                        c -> ((ModificationExerciceController) c).initData(currentExercise)));
            }
            if (btnDelete != null) {
                btnDelete.setOnAction(e -> TemplateController.navigate("/SuppressionExercice.fxml",
                        c -> ((SuppressionExerciceController) c).initData(currentExercise)));
            }
        }
    }

    public void initData(Exercise ex) {
        this.currentExercise = ex;
        if (ex == null)
            return;

        lblTitle.setText(ex.getTitle());
        lblDescription.setText(ex.getDescription());

        lblType.setText(ex.getType());
        lblDuration.setText(ex.getDurationMinutes() + " min");
        lblDifficulty.setText(ex.getDifficulty());
        lblMedia.setText(ex.getMediaUrl());

        String status = (ex.getIsPublished() == 1) ? "Publié" : "Brouillon";
        lblStatus.setText(status);

        lblUser.setText("Créé par utilisateur ID: " + ex.getUserId());

        String created = (ex.getCreatedAt() != null) ? dtf.format(ex.getCreatedAt()) : "-";
        String updated = (ex.getUpdatedAt() != null) ? dtf.format(ex.getUpdatedAt()) : "-";
        lblDates.setText("Créé le: " + created + " | Modifié le: " + updated);

        loadMedia(ex.getMediaUrl());
    }

    private void loadMedia(String url) {
        if (mediaContainer == null)
            return;
        mediaContainer.getChildren().clear();

        if (url == null || url.isBlank()) {
            mediaContainer.getChildren().add(new Label("Aucun média disponible"));
            return;
        }

        String lowUrl = url.toLowerCase();
        if (lowUrl.endsWith(".jpg") || lowUrl.endsWith(".jpeg") || lowUrl.endsWith(".png") || lowUrl.endsWith(".gif")
                || lowUrl.contains("unsplash.com")) {
            // Image
            try {
                Image img = new Image(url, true); // true = lazy load
                ImageView iv = new ImageView(img);
                iv.setPreserveRatio(true);
                iv.setFitWidth(680); // Un peu moins que maxWidth de la VBox
                iv.setFitHeight(380);
                mediaContainer.getChildren().add(iv);
            } catch (Exception e) {
                mediaContainer.getChildren().add(new Label("Erreur de chargement de l'image"));
            }
        } else if (lowUrl.contains("youtube.com") || lowUrl.contains("youtu.be")) {
            // YouTube
            try {
                String videoId = extractYouTubeId(url);
                if (videoId != null) {
                    WebView webView = new WebView();
                    String embedUrl = "https://www.youtube.com/embed/" + videoId;
                    String html = "<html><body style='margin:0;padding:0;background:#000;'>" +
                            "<iframe width='100%' height='100%' src='" + embedUrl + "' " +
                            "frameborder='0' allow='accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture' allowfullscreen></iframe>"
                            +
                            "</body></html>";
                    webView.getEngine().loadContent(html);
                    mediaContainer.getChildren().add(webView);
                } else {
                    mediaContainer.getChildren().add(new Label("URL YouTube invalide"));
                }
            } catch (Exception e) {
                mediaContainer.getChildren().add(new Label("Erreur de chargement de la vidéo"));
            }
        } else {
            // Autre (Lien texte cliquable ?)
            mediaContainer.getChildren().add(new Label("Format de média non supporté par l'aperçu"));
        }
    }

    private String extractYouTubeId(String url) {
        if (url == null)
            return null;
        if (url.contains("v=")) {
            int start = url.indexOf("v=") + 2;
            int end = url.indexOf("&", start);
            return (end == -1) ? url.substring(start) : url.substring(start, end);
        } else if (url.contains("youtu.be/")) {
            int start = url.indexOf("youtu.be/") + 9;
            int end = url.indexOf("?", start);
            return (end == -1) ? url.substring(start) : url.substring(start, end);
        } else if (url.contains("embed/")) {
            int start = url.indexOf("embed/") + 6;
            int end = url.indexOf("?", start);
            return (end == -1) ? url.substring(start) : url.substring(start, end);
        }
        return null;
    }

    private void goBack() {
        // Naviguer vers AffichageExercicesParObjectif en rechargant les exos de
        // l'objectif courant
        if (currentExercise != null) {
            // On a besoin de l'objet Objective pour initData... c'est embêtant car Exercise
            // ne stocke que l'ID.
            // On va tricher : on retourne à AffichageObjectifs pour l'instant, c'est plus
            // sur.
            // OU on appelle le service pour choper l'objectif (trop lourd ici).
            // Le plus simple UX pour l'instant: Retour Objectifs.
            TemplateController.navigate("/AffichageObjectifs.fxml", null);
        } else {
            TemplateController.navigate("/AffichageObjectifs.fxml", null);
        }
    }
}
