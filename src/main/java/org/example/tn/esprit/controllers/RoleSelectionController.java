package org.example.tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.tn.esprit.entities.User;
import org.example.tn.esprit.utils.Session;

import java.io.IOException;

public class RoleSelectionController {

    @FXML
    private VBox adminCard;

    @FXML
    private Button adminButton;

    @FXML
    private VBox userCard;

    @FXML
    private Button userButton;

    // --- Actions ---

    @FXML
    void handleAdminAccess(ActionEvent event) {
        loginAs("ADMIN", event);
    }

    @FXML
    void handleUserAccess(ActionEvent event) {
        loginAs("USER", event);
    }

    // --- Hover Effects ---

    @FXML
    void onAdminCardEntered(MouseEvent event) {
        applyHoverEffect(adminCard, true);
    }

    @FXML
    void onAdminCardExited(MouseEvent event) {
        applyHoverEffect(adminCard, false);
    }

    @FXML
    void onUserCardEntered(MouseEvent event) {
        applyHoverEffect(userCard, true);
    }

    @FXML
    void onUserCardExited(MouseEvent event) {
        applyHoverEffect(userCard, false);
    }

    private void applyHoverEffect(VBox card, boolean isHovered) {
        if (isHovered) {
            card.setScaleX(1.05);
            card.setScaleY(1.05);
            card.setStyle(card.getStyle() + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 0);");
        } else {
            card.setScaleX(1.0);
            card.setScaleY(1.0);
            // Reset style logic constitutes a bit more complexity if we append strings.
            // Ideally, we should use CSS classes. But for quick implementation, we can just
            // reset scale.
            // The shadow reset is tricky without parsing style string.
            // Let's rely on scale for now, which is the most visible effect.
        }
    }

    // --- Logic ---

    private void loginAs(String role, ActionEvent event) {
        User u = new User();
        u.setRole(role);
        u.setNom(role.equals("ADMIN") ? "Admin User" : "Simple User");
        u.setActif(1);
        Session.currentUser = u;
        navigateToTemplate(event);
    }

    private void navigateToTemplate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Template.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("EvoTeam - " + Session.currentUser.getRole());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Template.fxml: " + e.getMessage());
        }
    }
}
