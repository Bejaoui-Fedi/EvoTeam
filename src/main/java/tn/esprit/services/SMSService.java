package tn.esprit.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.io.InputStream;
import java.util.Properties;

public class SMSService {

    private String accountSid;
    private String authToken;
    private String twilioPhone;

    public SMSService() {
        loadConfig();
        Twilio.init(accountSid, authToken);
    }

    private void loadConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("❌ Fichier config.properties introuvable.");
                return;
            }
            props.load(input);
            this.accountSid = props.getProperty("twilio.sid");
            this.authToken = props.getProperty("twilio.token");
            this.twilioPhone = props.getProperty("twilio.phone");
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement config SMS : " + e.getMessage());
        }
    }

    public void sendSMS(String to, String body) {
        try {
            if (accountSid == null || authToken == null) {
                throw new RuntimeException("Configuration SMS manquante.");
            }
            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(twilioPhone),
                    body)
                    .create();
            System.out.println("✅ SMS envoyé avec succès ! SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("❌ Erreur Twilio : " + e.getMessage());
            throw e; // Relaunch to show in UI if integrated
        }
    }
}
