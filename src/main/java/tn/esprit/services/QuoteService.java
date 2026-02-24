package tn.esprit.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class QuoteService {

    // 🔑 PASTE YOUR API KEY HERE
    public static final String API_KEY = "VT52HCl775s3hWcFtKO5tYPL1B0dCQQngp4uLsx0";
    public static final String API_URL = "https://api.api-ninjas.com/v1/quotes";
    private static final int MAX_ATTEMPTS = 10; // Try up to 10 times to get a wellness quote

    // Health & wellness related keywords to filter
    private static final String[] WELLNESS_KEYWORDS = {
            "health", "wellness", "fitness", "mind", "body", "peace", "calm",
            "stress", "mental", "happiness", "joy", "healing", "care", "balance",
            "meditation", "yoga", "exercise", "nutrition", "sleep", "energy",
            "motivation", "inspiration", "hope", "courage", "strength"
    };

    public static class Quote {
        public String quote;
        public String author;
        public String category;
        public boolean isWellnessRelated;

        @Override
        public String toString() {
            return (quote != null ? quote : "") + " - " + (author != null ? author : "Unknown");
        }
    }

    private boolean isWellnessRelated(Quote quote) {
        if (quote == null || quote.quote == null) return false;

        String lowerQuote = quote.quote.toLowerCase();
        String lowerCategory = quote.category != null ? quote.category.toLowerCase() : "";

        // Check if quote contains wellness keywords
        for (String keyword : WELLNESS_KEYWORDS) {
            if (lowerQuote.contains(keyword) || lowerCategory.contains(keyword)) {
                return true;
            }
        }

        // Check category specifically
        String[] wellnessCategories = {"health", "wellness", "fitness", "happiness", "inspirational", "motivational"};
        for (String cat : wellnessCategories) {
            if (lowerCategory.contains(cat)) {
                return true;
            }
        }

        return false;
    }

    public Quote getWellnessQuote() {
        // First try: Get from API with filtering
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            Quote quote = getRandomQuoteFromAPI();
            if (quote != null && isWellnessRelated(quote)) {
                quote.isWellnessRelated = true;
                System.out.println("Found wellness quote after " + (attempt + 1) + " attempts");
                return quote;
            }
        }

        // If no wellness quote found after MAX_ATTEMPTS, return null
        System.out.println("No wellness quote found after " + MAX_ATTEMPTS + " attempts");
        return null;
    }

    private Quote getRandomQuoteFromAPI() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("X-Api-Key", API_KEY);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return null;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();

            if (!jsonArray.isEmpty()) {
                JsonObject jsonQuote = jsonArray.get(0).getAsJsonObject();

                Quote quote = new Quote();
                quote.quote = jsonQuote.has("quote") ? jsonQuote.get("quote").getAsString() : "No quote";
                quote.author = jsonQuote.has("author") ? jsonQuote.get("author").getAsString() : "Unknown";
                quote.category = jsonQuote.has("category") ? jsonQuote.get("category").getAsString() : "general";

                return quote;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Optional: Get quote for specific wellness category
    public Quote getQuoteForCategory(String wellnessCategory) {
        // Try API first
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            Quote quote = getRandomQuoteFromAPI();
            if (quote != null && quote.quote.toLowerCase().contains(wellnessCategory.toLowerCase())) {
                return quote;
            }
        }

        // Return null if no matching quote found
        return null;
    }
}