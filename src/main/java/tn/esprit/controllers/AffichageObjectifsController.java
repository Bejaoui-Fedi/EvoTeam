package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.entities.Objective;
import tn.esprit.services.ServiceObjective;
import tn.esprit.utils.Session;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AffichageObjectifsController {

    @FXML
    private TableView<Objective> tableObjectives;

    @FXML
    private TableColumn<Objective, Long> colId;
    @FXML
    private TableColumn<Objective, String> colTitle;
    @FXML
    private TableColumn<Objective, String> colDescription;
    @FXML
    private TableColumn<Objective, String> colLevel;
    @FXML
    private TableColumn<Objective, Integer> colPublished;
    @FXML
    private TableColumn<Objective, Integer> colUserId;
    @FXML
    private TableColumn<Objective, LocalDateTime> colCreatedAt;
    @FXML
    private TableColumn<Objective, LocalDateTime> colUpdatedAt;

    @FXML
    private Button btnDetails;
    @FXML
    private Button btnViewExercises;
    @FXML
    private Button btnGoAdd;
    @FXML
    private Button btnGoEdit;
    @FXML
    private Button btnGoDelete;
    @FXML
    private Button btnRefresh;

    private final ServiceObjective service = new ServiceObjective();
    private final ObservableList<Objective> data = FXCollections.observableArrayList();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        // Simple PropertyValueFactory binding
        colId.setCellValueFactory(new PropertyValueFactory<>("idObjective"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colPublished.setCellValueFactory(new PropertyValueFactory<>("published"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colUpdatedAt.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        // Simple Text Formatting (No Badges)
        colPublished.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item == 1 ? "Publié" : "Brouillon"));
            }
        });

        colCreatedAt.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : dtf.format(item));
            }
        });

        colUpdatedAt.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : dtf.format(item));
            }
        });

        applyRolePermissions();
        loadData();

        btnRefresh.setOnAction(e -> loadData());
        btnViewExercises.setOnAction(e -> goToExercises());

        if (btnDetails != null) {
            btnDetails.setOnAction(e -> {
                Objective obj = tableObjectives.getSelectionModel().getSelectedItem();
                if (obj == null) {
                    new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un objectif.").showAndWait();
                    return;
                }
                TemplateController.navigate("/DetailObjectif.fxml", c -> ((DetailObjectifController) c).initData(obj));
            });
        }

        tableObjectives.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2)
                goToExercises();
        });

        if (btnGoAdd != null)
            btnGoAdd.setOnAction(e -> TemplateController.navigate("/AjoutObjectif.fxml", null));
        if (btnGoEdit != null)
            btnGoEdit.setOnAction(e -> {
                Objective selected = tableObjectives.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un objectif à modifier.").showAndWait();
                    return;
                }
                TemplateController.navigate("/ModificationObjectif.fxml",
                        c -> ((ModificationObjectifController) c).initData(selected));
            });
        if (btnGoDelete != null)
            btnGoDelete.setOnAction(e -> TemplateController.navigate("/SuppressionObjectif.fxml", null));
    }

    private void goToExercises() {
        Objective obj = tableObjectives.getSelectionModel().getSelectedItem();
        if (obj == null) {
            new Alert(Alert.AlertType.INFORMATION, "Sélectionne un objectif.").showAndWait();
            return;
        }
        TemplateController.navigate("/AffichageExercicesParObjectif.fxml", c -> {
            if (c instanceof AffichageExercicesParObjectifController a) {
                a.initObjective(obj);
            }
        });
    }

    private void applyRolePermissions() {
        boolean admin = Session.isAdmin();
        btnGoAdd.setVisible(admin);
        btnGoAdd.setManaged(admin);
        btnGoEdit.setVisible(admin);
        btnGoEdit.setManaged(admin);
        btnGoDelete.setVisible(admin);
        btnGoDelete.setManaged(admin);
        btnViewExercises.setVisible(true);
    }

    private void loadData() {
        data.clear();
        try {
            List<Objective> list;
            if (Session.isAdmin()) {
                list = service.show();
            } else {
                list = service.showPublished();
            }
            data.addAll(list);
            tableObjectives.setItems(data);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}