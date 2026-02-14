package org.example.tn.esprit.mains;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxMain extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Start with New Template (green sidebar layout)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewTemplate.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("EVOLIA - Gestion des Exercices");
        stage.setScene(scene);
        stage.setMaximized(true); // Start maximized for better view
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
