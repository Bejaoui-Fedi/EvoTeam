package tn.esprit.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import tn.esprit.services.ChatService;
import tn.esprit.services.ChatService.ChatResponse;

import java.io.IOException;
import java.util.List;

public class ChatController {

    @FXML private VBox chatContainer;
    @FXML private TextArea messageInput;
    @FXML private Button sendButton;
    @FXML private Button clearChatButton;
    @FXML private ScrollPane chatScrollPane;
    @FXML private Label typingIndicator;
    @FXML private VBox suggestionsContainer;
    @FXML private FlowPane suggestionsFlowPane;

    private final ChatService chatService = new ChatService();

    @FXML
    public void initialize() {
        // Initialize suggestionsFlowPane if null
        if (suggestionsFlowPane == null) {
            suggestionsFlowPane = new FlowPane();
            suggestionsFlowPane.setHgap(10);
            suggestionsFlowPane.setVgap(10);
        }

        // Hide suggestions container initially
        suggestionsContainer.setVisible(false);
        suggestionsContainer.setManaged(false);

        // Set up button actions
        sendButton.setOnAction(event -> sendMessage());
        clearChatButton.setOnAction(event -> clearChat());

        messageInput.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER && !event.isShiftDown()) {
                event.consume();
                sendMessage();
            }
        });

        // Auto-scroll to bottom
        chatContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            chatScrollPane.setVvalue(1.0);
        });

        // ✅ FIX: Delay the welcome message to ensure UI is ready
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.millis(100));
        delay.setOnFinished(event -> {
            addWelcomeMessage();
        });
        delay.play();
    }

    private void addWelcomeMessage() {
        String welcome =
                "🌿 Bonjour! Je suis Coach Équilibre, votre accompagnateur bien-être.\n\n" +
                        "Je suis là pour vous aider avec:\n" +
                        "• Gestion du stress et de l'anxiété\n" +
                        "• Amélioration du sommeil\n" +
                        "• Motivation et productivité\n" +
                        "• Habitudes saines\n" +
                        "• Exercices de respiration\n" +
                        "• Bien-être émotionnel\n\n" +
                        "Comment vous sentez-vous aujourd'hui? 💚";

        addMessage(welcome, "assistant");
    }

    private void sendMessage() {
        String userMessage = messageInput.getText().trim();
        if (userMessage.isEmpty()) return;

        addMessage(userMessage, "user");
        messageInput.clear();

        showTypingIndicator(true);
        sendButton.setDisable(true);

        new Thread(() -> {
            try {
                ChatResponse response = chatService.sendMessage(userMessage);

                Platform.runLater(() -> {
                    showTypingIndicator(false);

                    if (response.success) {
                        addMessage(response.message, "assistant");
                        showSuggestions(response.suggestions);
                    } else {
                        addMessage(response.message, "assistant");
                    }

                    sendButton.setDisable(false);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showTypingIndicator(false);
                    addMessage("🌿 Désolé, une erreur est survenue. Veuillez réessayer.", "assistant");
                    sendButton.setDisable(false);
                });
            }
        }).start();
    }

    private void addMessage(String content, String sender) {
        HBox messageBox = new HBox();
        messageBox.setPadding(new Insets(10));
        messageBox.setMaxWidth(chatContainer.getWidth() * 0.85);

        VBox messageContent = new VBox(5);
        messageContent.setMaxWidth(500);

        // Sender label
        Label senderLabel = new Label(sender.equals("user") ? "👤 Vous" : "🌿 Coach Équilibre");
        senderLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        senderLabel.setTextFill(sender.equals("user") ? Color.web("#2C3E50") : Color.web("#3A7D6B"));

        // Message text - Use a single Text object for the entire message
        Text text = new Text(content);
        text.setFont(Font.font("System", 14));

        TextFlow textFlow = new TextFlow(text);
        textFlow.setPadding(new Insets(12));
        textFlow.setMaxWidth(450);
        textFlow.setLineSpacing(5); // Add line spacing for better readability

        if (sender.equals("user")) {
            textFlow.setStyle("-fx-background-color: #E8F4E8; -fx-background-radius: 15 15 0 15;");
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageContent.setAlignment(Pos.CENTER_RIGHT);
        } else {
            textFlow.setStyle("-fx-background-color: white; -fx-background-radius: 15 15 15 0; " +
                    "-fx-border-color: #E2E2E2; -fx-border-radius: 15 15 15 0; -fx-border-width: 1;");
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageContent.setAlignment(Pos.CENTER_LEFT);
        }

        messageContent.getChildren().addAll(senderLabel, textFlow);
        messageBox.getChildren().add(messageContent);

        chatContainer.getChildren().add(messageBox);
    }

    private void showTypingIndicator(boolean show) {
        typingIndicator.setVisible(show);
        typingIndicator.setManaged(show);
    }

    private void showSuggestions(List<String> suggestions) {
        if (suggestionsContainer == null || suggestionsFlowPane == null) return;

        suggestionsContainer.setVisible(true);
        suggestionsContainer.setManaged(true);
        suggestionsFlowPane.getChildren().clear();

        if (suggestions == null || suggestions.isEmpty()) {
            suggestionsContainer.setVisible(false);
            suggestionsContainer.setManaged(false);
            return;
        }

        for (String suggestion : suggestions) {
            Button btn = new Button(suggestion);
            btn.setStyle("-fx-background-color: rgba(58, 125, 107, 0.1); " +
                    "-fx-text-fill: #2C3E50; " +
                    "-fx-background-radius: 20; " +
                    "-fx-padding: 8 15; " +
                    "-fx-font-size: 12; " +
                    "-fx-cursor: hand; " +
                    "-fx-border-color: #3A7D6B; " +
                    "-fx-border-radius: 20; " +
                    "-fx-border-width: 1;");

            btn.setOnAction(e -> {
                messageInput.setText(btn.getText());
                sendMessage();
            });

            suggestionsFlowPane.getChildren().add(btn);
        }
    }

    private void clearChat() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Effacer la conversation");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous effacer toute la conversation ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                chatService.clearConversation();
                chatContainer.getChildren().clear();
                suggestionsFlowPane.getChildren().clear();
                suggestionsContainer.setVisible(false);
                suggestionsContainer.setManaged(false);
                addWelcomeMessage();
            }
        });
    }

    @FXML
    private void goToDashboard() {
        // Just close the current chat window
        Stage stage = (Stage) chatContainer.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}