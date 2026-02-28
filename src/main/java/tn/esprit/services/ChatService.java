package tn.esprit.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatService {

    private static final String API_KEY = "Not-Today"; // Replace with your actual Groq key

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL_NAME = "llama-3.3-70b-versatile";

    private List<ChatMessage> conversationHistory = new ArrayList<>();

    public static class ChatMessage {
        public String role;
        public String content;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    public static class ChatResponse {
        public String message;
        public boolean success;
        public List<String> suggestions;

        public ChatResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
            this.suggestions = new ArrayList<>();
        }
    }

    public ChatService() {
        addSystemPrompt();
    }

    private void addSystemPrompt() {
        String systemPrompt =
                "Tu es un coach bien-être professionnel spécialisé en santé mentale et physique. " +
                        "Tu t'appelles 'Coach Équilibre'. " +
                        "Tu réponds TOUJOURS en français, de façon chaleureuse et empathique. " +
                        "Tes conseils portent sur: gestion du stress, anxiété, motivation, habitudes saines, " +
                        "sommeil, exercice physique, méditation, alimentation équilibrée, équilibre vie-travail. " +
                        "Tu proposes des exercices pratiques et des actions concrètes. " +
                        "Tu es bienveillant, encourageant et positif. " +
                        "Si l'utilisateur mentionne des pensées suicidaires ou une dépression sévère, " +
                        "tu recommandes de consulter un professionnel de santé immédiatement. " +
                        "Tu adaptes tes réponses à l'état émotionnel de l'utilisateur. " +
                        "Tu gardes en mémoire la conversation pour offrir un suivi personnalisé.";

        conversationHistory.add(new ChatMessage("system", systemPrompt));
    }

    public ChatResponse sendMessage(String userMessage) {
        try {
            conversationHistory.add(new ChatMessage("user", userMessage));

            // Build messages array for Groq API (OpenAI-compatible format)
            JsonArray messagesArray = new JsonArray();

            // Add system prompt
            JsonObject systemMessage = new JsonObject();
            systemMessage.addProperty("role", "system");
            systemMessage.addProperty("content", conversationHistory.get(0).content);
            messagesArray.add(systemMessage);

            // Add conversation history (skip system message)
            for (int i = 1; i < conversationHistory.size(); i++) {
                ChatMessage msg = conversationHistory.get(i);
                JsonObject message = new JsonObject();
                message.addProperty("role", msg.role.equals("user") ? "user" : "assistant");
                message.addProperty("content", msg.content);
                messagesArray.add(message);
            }

            // Build request body for Groq API
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", MODEL_NAME);
            requestBody.add("messages", messagesArray);

            // Optional parameters
            requestBody.addProperty("temperature", 0.8);
            requestBody.addProperty("max_tokens", 800);
            requestBody.addProperty("top_p", 0.9);

            // Call API
            String response = callGroqAPI(requestBody);
            String aiResponse = parseGroqResponse(response);

            conversationHistory.add(new ChatMessage("assistant", aiResponse));

            ChatResponse chatResponse = new ChatResponse(aiResponse, true);
            chatResponse.suggestions = generateSuggestions(userMessage, aiResponse);

            return chatResponse;

        } catch (Exception e) {
            e.printStackTrace();
            ChatResponse errorResponse = new ChatResponse(
                    "🌿 Désolé, je rencontre des difficultés techniques. Veuillez réessayer dans un instant.",
                    false
            );
            errorResponse.suggestions = List.of(
                    "Parler de mon stress",
                    "Conseils pour mieux dormir",
                    "Exercice de respiration",
                    "Motivation"
            );
            return errorResponse;
        }
    }

    private String callGroqAPI(JsonObject requestBody) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setDoOutput(true);

        // Write request body
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();

        BufferedReader reader;
        if (responseCode == 200) {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        if (responseCode != 200) {
            System.out.println("❌ Groq API Error Response: " + response);
            throw new Exception("Groq API Error: " + responseCode + " - " + response);
        }

        return response.toString();
    }

    private String parseGroqResponse(String jsonResponse) {
        try {
            JsonObject responseObj = JsonParser.parseString(jsonResponse).getAsJsonObject();

            // Groq follows OpenAI response format
            if (responseObj.has("choices")) {
                JsonArray choices = responseObj.getAsJsonArray("choices");
                if (choices.size() > 0) {
                    JsonObject firstChoice = choices.get(0).getAsJsonObject();

                    if (firstChoice.has("message")) {
                        JsonObject message = firstChoice.getAsJsonObject("message");

                        if (message.has("content")) {
                            return message.get("content").getAsString();
                        }
                    }
                }
            }

            return "Désolé, je n'ai pas pu générer une réponse.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'analyse de la réponse.";
        }
    }

    private List<String> generateSuggestions(String userMessage, String aiResponse) {
        List<String> suggestions = new ArrayList<>();
        String lowerMsg = userMessage.toLowerCase();

        if (lowerMsg.contains("stress") || lowerMsg.contains("anxieux") || lowerMsg.contains("anxiété")) {
            suggestions.add("🧘 Respiration 4-7-8");
            suggestions.add("📝 Journal de gratitude");
            suggestions.add("🚶 Marche 10min");
        } else if (lowerMsg.contains("sommeil") || lowerMsg.contains("dormir") || lowerMsg.contains("fatigue")) {
            suggestions.add("😴 Routine du coucher");
            suggestions.add("📵 Pas d'écrans 1h avant");
            suggestions.add("☕ Pas de caféine après 14h");
        } else if (lowerMsg.contains("motivation") || lowerMsg.contains("productivité")) {
            suggestions.add("🎯 Technique Pomodoro");
            suggestions.add("🏆 Célébrer les victoires");
            suggestions.add("⏰ Se lever plus tôt");
        } else if (lowerMsg.contains("triste") || lowerMsg.contains("déprimé")) {
            suggestions.add("💚 Parler à un proche");
            suggestions.add("🌅 Lumière naturelle");
            suggestions.add("🤗 Auto-compassion");
        } else {
            suggestions.add("🧘 Respiration consciente");
            suggestions.add("💧 Boire un verre d'eau");
            suggestions.add("🌿 Pause nature 5min");
        }

        return suggestions;
    }

    public void clearConversation() {
        conversationHistory.clear();
        addSystemPrompt();
    }
}