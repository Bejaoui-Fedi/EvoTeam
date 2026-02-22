package tn.esprit.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Evolia Event Planner";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public static Calendar getCalendarService() throws IOException, GeneralSecurityException {
        // Charger les credentials depuis le fichier dans resources
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new IOException("Fichier credentials.json non trouvé dans resources");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/calendar"));

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static String addEventToGoogleCalendar(tn.esprit.entities.Event event) {
        try {
            Calendar service = getCalendarService();

            // Créer l'événement Google Calendar
            Event googleEvent = new Event()
                    .setSummary(event.getName())
                    .setDescription(event.getDescription())
                    .setLocation(event.getLocation());

            // Définir les dates (ajustez le format si nécessaire)
            String startDateTimeStr = event.getStartDate() + "T09:00:00+01:00";
            String endDateTimeStr = event.getEndDate() + "T17:00:00+01:00";

            com.google.api.client.util.DateTime startDateTime =
                    new com.google.api.client.util.DateTime(startDateTimeStr);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Africa/Tunis");
            googleEvent.setStart(start);

            com.google.api.client.util.DateTime endDateTime =
                    new com.google.api.client.util.DateTime(endDateTimeStr);
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Africa/Tunis");
            googleEvent.setEnd(end);

            // Insérer l'événement dans le calendrier principal
            googleEvent = service.events().insert("eleuchyessine38@gmail.com", googleEvent).execute();

            return googleEvent.getHtmlLink(); // Lien pour voir l'événement

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}