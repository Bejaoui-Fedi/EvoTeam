package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import tn.esprit.entities.Exercise;
import tn.esprit.entities.Objective;
import tn.esprit.services.ServiceExercise;
import tn.esprit.utils.Session;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AffichageExercicesParObjectifController {

    @FXML
    private Label lblObjectiveTitle;
    @FXML
    private TableView<Exercise> tableExercises;

    // Columns
    @FXML
    private TableColumn<Exercise, Integer> colId;
    @FXML
    private TableColumn<Exercise, String> colTitle;
    @FXML
    private TableColumn<Exercise, String> colDescription;
    @FXML
    private TableColumn<Exercise, String> colType;
    @FXML
    private TableColumn<Exercise, Integer> colDuration;
    @FXML
    private TableColumn<Exercise, String> colDifficulty;
    @FXML
    private TableColumn<Exercise, String> colMedia;
    @FXML
    private TableColumn<Exercise, Integer> colPublished;
    @FXML
    private TableColumn<Exercise, Integer> colUserId;
    @FXML
    private TableColumn<Exercise, LocalDateTime> colCreatedAt;
    @FXML
    private TableColumn<Exercise, LocalDateTime> colUpdatedAt;

    @FXML
    private Button btnBack;
    @FXML
    private Button btnGoAdd;
    @FXML
    private Button btnGoEdit;
    @FXML
    private Button btnGoDelete;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnDoExercise;
    @FXML
    private Button btnDetails;

    private final ServiceExercise service = new ServiceExercise();
    private final ObservableList<Exercise> data = FXCollections.observableArrayList();
    private Objective selectedObjective;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        // Standard Bindings
        colId.setCellValueFactory(new PropertyValueFactory<>("idExercise"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        colDifficulty.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        colMedia.setCellValueFactory(new PropertyValueFactory<>("mediaUrl"));
        colPublished.setCellValueFactory(new PropertyValueFactory<>("isPublished"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colUpdatedAt.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        // Simple Renderers (No Badges)
        colPublished.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : (v == 1 ? "Oui" : "Non"));
            }
        });

        colCreatedAt.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? "-" : dtf.format(v));
            }
        });

        colUpdatedAt.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? "-" : dtf.format(v));
            }
        });

        // Wrap Description
        colDescription.setCellFactory(tc -> {
            TableCell<Exercise, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(tc.widthProperty().subtract(10));
            cell.itemProperty().addListener((obs, old, val) -> {
                text.setText(val == null ? "" : val);
            });
            return cell;
        });

        applyRolePermissions();

        // Actions
        btnBack.setOnAction(e -> TemplateController.navigate("/AffichageObjectifs.fxml", null));
        btnRefresh.setOnAction(e -> reload());
        btnDoExercise.setOnAction(e -> {
            Exercise ex = tableExercises.getSelectionModel().getSelectedItem();
            if (ex != null) {
                TemplateController.navigate("/PasserExercice.fxml", c -> {
                    if (c instanceof PasserExerciceController p)
                        p.initData(ex, selectedObjective);
                });
            }
        });

        if (btnGoAdd != null)
            btnGoAdd.setOnAction(e -> {
                if (selectedObjective != null)
                    TemplateController.navigate("/AjoutExercice.fxml", c -> ((AjoutExerciceController) c)
                            .initObjective(selectedObjective));
            });

        if (btnDetails != null) {
            btnDetails.setOnAction(e -> {
                Exercise selected = tableExercises.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un exercice.").showAndWait();
                    return;
                }
                TemplateController.navigate("/DetailExercice.fxml",
                        c -> ((DetailExerciceController) c).initData(selected));
            });
        }

        // Edit/Delete handlers...
        if (btnGoEdit != null)
            btnGoEdit.setOnAction(e -> {
                Exercise selected = tableExercises.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un exercice à modifier.").showAndWait();
                    return;
                }
                TemplateController.navigate("/ModificationExercice.fxml",
                        c -> ((ModificationExerciceController) c).initData(selected));
            });
        if (btnGoDelete != null)
            btnGoDelete.setOnAction(e -> TemplateController.navigate("/SuppressionExercice.fxml",
                    c -> ((SuppressionExerciceController) c)
                            .initData(tableExercises.getSelectionModel().getSelectedItem())));
    }

    public void initObjective(Objective obj) {
        this.selectedObjective = obj;
        if (lblObjectiveTitle != null)
            lblObjectiveTitle.setText("Objectif : " + (obj != null ? obj.getTitle() : "-"));
        reload();
    }

    private void reload() {
        if (selectedObjective == null)
            return;
        data.clear();
        try {
            List<Exercise> list;
            int objId = Math.toIntExact(selectedObjective.getIdObjective());
            if (Session.isAdmin()) {
                list = service.getByObjectiveId(objId);
            } else {
                list = service.getByObjectiveIdPublished(objId);
            }
            data.addAll(list);
            tableExercises.setItems(data);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void applyRolePermissions() {
        boolean admin = Session.isAdmin();
        if (btnGoAdd != null) {
            btnGoAdd.setVisible(admin);
            btnGoAdd.setManaged(admin);
        }
        if (btnGoEdit != null) {
            btnGoEdit.setVisible(admin);
            btnGoEdit.setManaged(admin);
        }
        if (btnGoDelete != null) {
            btnGoDelete.setVisible(admin);
            btnGoDelete.setManaged(admin);
        }
    }
}