package tn.esprit.services;

import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import java.io.InputStream;
import java.util.Properties;

public class WeatherService {
    private String apiKey;
    private String city = "Tunis";

    public WeatherService() {
        loadConfig();
    }

    private void loadConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("❌ Fichier config.properties introuvable.");
                return;
            }
            props.load(input);
            this.apiKey = props.getProperty("weather.api.key");
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement config Météo : " + e.getMessage());
        }
    }

    private String getUrl() {
        return "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + apiKey;
    }

    public String getWeatherInfo() {
        if (apiKey == null) {
            return "Météo indisponible (API Key manquante)";
        }
        try {
            HttpClient client = createUnsafeHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getUrl()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("DEBUG Météo Status: " + response.statusCode());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                double temp = json.getJSONObject("main").getDouble("temp");
                String description = json.getJSONArray("weather").getJSONObject(0).getString("description");

                return String.format("%.1f°C - %s", temp, capitalize(description));
            } else {
                System.err.println("DEBUG Météo Error Body: " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Erreur météo: " + e.getMessage());
            e.printStackTrace();
        }
        return "Météo indisponible";
    }

    private HttpClient createUnsafeHttpClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .build();
        } catch (Exception e) {
            System.err.println("Failed to create unsafe HttpClient: " + e.getMessage());
            return HttpClient.newHttpClient();
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
