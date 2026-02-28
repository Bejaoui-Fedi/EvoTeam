package tn.esprit.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TraductionService {

    /**
     * Traduit un texte en spécifiant la langue source et la langue cible
     * @param texte Le texte à traduire
     * @param sourceLangue Code de la langue source (ex: "fr", "en", "ar")
     * @param cibleLangue Code de la langue cible (ex: "it", "es", "de")
     * @return Le texte traduit
     */
    public static String traduireAvecSource(String texte, String sourceLangue, String cibleLangue) {
        if (texte == null || texte.trim().isEmpty()) {
            return texte;
        }

        try {
            // Encodage du texte pour l'URL
            String texteEncode = URLEncoder.encode(texte, StandardCharsets.UTF_8.toString());

            // Construction de l'URL avec la langue source spécifiée
            String urlStr = "https://api.mymemory.translated.net/get?q="
                    + texteEncode
                    + "&langpair=" + sourceLangue + "|" + cibleLangue;

            System.out.println("📡 URL appelée : " + urlStr);

            // Connexion HTTP
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Lecture de la réponse
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String responseStr = response.toString();
            System.out.println("📦 Réponse reçue");

            // Parse du JSON
            JsonObject json = JsonParser.parseString(responseStr).getAsJsonObject();

            // Vérification du statut
            String responseStatus = json.get("responseStatus").getAsString();

            if (!"200".equals(responseStatus)) {
                String errorDetails = json.get("responseDetails").getAsString();
                return "Erreur (" + responseStatus + ") : " + errorDetails;
            }

            // Extraction du texte traduit
            String translated = json.getAsJsonObject("responseData")
                    .get("translatedText").getAsString();

            return translated;

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur : " + e.getMessage();
        }
    }

    /**
     * Version simplifiée avec détection automatique de la langue source
     */
    public static String traduire(String texte, String cibleLangue) {
        return traduireAvecSource(texte, "auto", cibleLangue);
    }

    /**
     * Retourne la liste des codes de langue disponibles
     */
    public static String[] getLanguesDisponibles() {
        return new String[]{"fr", "en", "es", "de", "it", "pt", "ar"};
    }

    /**
     * Convertit un code de langue en nom lisible
     */
    public static String getNomLangue(String code) {
        switch (code) {
            case "fr": return "Français";
            case "en": return "Anglais";
            case "es": return "Espagnol";
            case "de": return "Allemand";
            case "it": return "Italien";
            case "pt": return "Portugais";
            case "ar": return "Arabe";
            default: return code;
        }
    }
}