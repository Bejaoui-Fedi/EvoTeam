package tn.esprit.mains;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Change le chemin pour tester chaque interface
        // Parent root =
        // FXMLLoader.load(getClass().getResource("/fxml/user/user_prendre_rdv.fxml"));
        // Parent root =
        // FXMLLoader.load(getClass().getResource("/fxml/user/user_mes_rdv.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/professionnel/pro_gestion_rdv.fxml"));
        // Parent root =
        // FXMLLoader.load(getClass().getResource("/fxml/professionnel/pro_gestion_consultation.fxml"));
        // Parent root =
        // FXMLLoader.load(getClass().getResource("/fxml/admin/admin_supervision_rdv.fxml"));
        // Parent root =
        // FXMLLoader.load(getClass().getResource("/fxml/admin/admin_supervision_consultation.fxml"));

        primaryStage.setTitle("Evolution Management System - Professional Space");
        primaryStage.setScene(new Scene(root, 1400, 850));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}