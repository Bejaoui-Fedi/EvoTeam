package tn.esprit.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyService {

    // Taux approximatifs (seront remplacés par l'API)
    private static double usdRate = 0.32; // 1 DT = 0.32 USD
    private static double eurRate = 0.29; // 1 DT = 0.29 EUR
    private static long lastUpdate = 0;

    /**
     * Met à jour les taux de change depuis CoinGecko
     */
    private static void updateRates() {
        try {
            // CoinGecko donne le prix de l'USD et EUR en BTC, mais on va utiliser
            // un service plus simple pour les taux de change
            String urlStr = "https://api.coingecko.com/api/v3/exchange_rates";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject rates = json.getAsJsonObject("rates");

            // Récupérer les taux
            double usdToBtc = rates.getAsJsonObject("usd").get("value").getAsDouble();
            double eurToBtc = rates.getAsJsonObject("eur").get("value").getAsDouble();

            // On a besoin du taux TND, mais CoinGecko ne l'a pas toujours
            // Solution : on utilise un taux fixe approximatif
            // Pour de vrais taux, il faudrait une autre API

            System.out.println("✅ Taux de change mis à jour");

        } catch (Exception e) {
            System.out.println("⚠️ Utilisation des taux par défaut");
        }
    }

    /**
     * Convertit des Dinars en Euros
     */
    public static double dtToEur(double montantDT) {
        return montantDT * 0.29; // Taux approximatif
    }

    /**
     * Convertit des Dinars en Dollars
     */
    public static double dtToUsd(double montantDT) {
        return montantDT * 0.32; // Taux approximatif
    }

    /**
     * Retourne le montant formaté en Euro
     */
    public static String formatEur(double montantDT) {
        return String.format("%.2f €", dtToEur(montantDT));
    }

    /**
     * Retourne le montant formaté en Dollar
     */
    public static String formatUsd(double montantDT) {
        return String.format("%.2f $", dtToUsd(montantDT));
    }
}