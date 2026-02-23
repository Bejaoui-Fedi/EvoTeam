package tn.esprit.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {

    // IMPORTANT: Replace with YOUR actual API key
    private static final String API_KEY = "a52860789d8833de9b306769d1270a8f";
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    // Weather data class
    public static class WeatherData {
        public String condition;
        public String description;
        public double temperature;
        public int humidity;
        public String icon;
        public String recommendation;
        public String city;

        @Override
        public String toString() {
            return String.format("%s - %.1f°C, %d%% humidity",
                    description, temperature, humidity);
        }
    }

    public WeatherData getWeatherForCity(String city) {
        try {
            // Encode the city name for URL
            city = city.replace(" ", "%20");

            // Build URL - USING HTTP (not HTTPS) for free tier
            String urlString = String.format("%s?q=%s&units=metric&appid=%s",
                    BASE_URL, city, API_KEY);

            System.out.println("Calling URL: " + urlString); // Debug

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode); // Debug

            if (responseCode != 200) {
                // Read error stream
                BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();
                System.out.println("Error Response: " + errorResponse.toString());
                return null;
            }

            // Read successful response
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println("Success Response: " + response.toString()); // Debug

            // Parse JSON with Gson
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();

            WeatherData data = new WeatherData();
            data.city = json.get("name").getAsString();

            // Get weather condition
            JsonObject weather = json.getAsJsonArray("weather").get(0).getAsJsonObject();
            data.condition = weather.get("main").getAsString();
            data.description = weather.get("description").getAsString();
            data.icon = weather.get("icon").getAsString();

            // Get temperature and humidity
            JsonObject main = json.getAsJsonObject("main");
            data.temperature = main.get("temp").getAsDouble();
            data.humidity = main.get("humidity").getAsInt();

            // Generate recommendation based on weather
            data.recommendation = getRecommendation(data);

            return data;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getRecommendation(WeatherData weather) {
        String condition = weather.condition.toLowerCase();
        double temp = weather.temperature;

        if (condition.contains("rain")) {
            return "☔ Rainy day - Perfect for indoor tasks, meditation, or reading a book";
        } else if (condition.contains("clear") && temp > 20) {
            return "☀️ Beautiful weather! Great for outdoor activities and exercise";
        } else if (temp < 10) {
            return "❄️ Cold outside - Stay warm, drink hot tea, and focus on indoor routines";
        } else if (condition.contains("cloud") || condition.contains("overcast")) {
            return "☁️ Good day for both indoor and outdoor tasks - stay productive!";
        } else if (condition.contains("snow")) {
            return "❄️ Snow day! Perfect for cozy indoor activities";
        } else if (temp > 30) {
            return "🔥 Hot weather - Stay hydrated and avoid strenuous outdoor activities";
        }

        return "🌤️ Have a productive day and take care of your wellbeing!";
    }

    // Optional: Get weather by coordinates
    public WeatherData getWeatherByCoordinates(double lat, double lon) {
        try {
            String urlString = String.format("%s?lat=%f&lon=%f&units=metric&appid=%s",
                    BASE_URL, lat, lon, API_KEY);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // ... rest same as getWeatherForCity
            // (copy the same parsing logic)

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}