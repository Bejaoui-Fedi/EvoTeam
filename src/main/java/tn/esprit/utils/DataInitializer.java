package tn.esprit.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

public class DataInitializer {

    public static void main(String[] args) {
        insertTestData();
    }

    public static void insertTestData() {
        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null) {
            System.out.println("❌ Connexion échouée, impossible d'insérer les données.");
            return;
        }

        Random random = new Random();
        String[] motifs = { "Consultation de routine", "Suivi mensuel", "Bilan initial", "Urgence",
                "Conseils bien-être", "Suivi thérapie", "Validation résultats" };
        String[] types = { "Présentiel", "Téléconsultation" };
        String[] statutsRdv = { "en attente", "confirmé", "terminé", "annulé" };
        String[] diagnostics = { "Anxiété", "Fatigue", "Stress", "Stabilité" };
        String[] statutsConsult = { "Clôturée", "En cours", "Attente" };

        try {
            // 1. Insert 20 Appointments
            String insertRdvSQL = "INSERT INTO appointment (date_rdv, heure_rdv, statut, motif, type_rdv, user_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmtRdv = conn.prepareStatement(insertRdvSQL, Statement.RETURN_GENERATED_KEYS);

            System.out.println("⏳ Insertion de 20 rendez-vous...");
            for (int i = 1; i <= 20; i++) {
                LocalDate date = LocalDate.now().plusDays(random.nextInt(30) - 10); // From 10 days ago to 20 days in
                                                                                    // future
                LocalTime time = LocalTime.of(8 + random.nextInt(10), random.nextBoolean() ? 0 : 30); // Between 8:00
                                                                                                      // and 17:30

                pstmtRdv.setDate(1, java.sql.Date.valueOf(date));
                pstmtRdv.setTime(2, java.sql.Time.valueOf(time));
                pstmtRdv.setString(3, statutsRdv[random.nextInt(statutsRdv.length)]);
                pstmtRdv.setString(4, motifs[random.nextInt(motifs.length)]);
                pstmtRdv.setString(5, types[random.nextInt(types.length)]);
                pstmtRdv.setInt(6, 1 + random.nextInt(5)); // Random user_id between 1 and 5

                pstmtRdv.executeUpdate();

                // For each appointment, maybe insert a consultation if it's in the past
                ResultSet rs = pstmtRdv.getGeneratedKeys();
                if (rs.next() && (date.isBefore(LocalDate.now()) || random.nextBoolean())) {
                    int rdvId = rs.getInt(1);
                    insertConsultation(conn, rdvId, date, diagnostics, statutsConsult, random);
                }
            }
            System.out.println("✅ Insertion terminée.");

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertConsultation(Connection conn, int rdvId, LocalDate date, String[] diagnostics,
            String[] statutsConsult, Random random) throws SQLException {
        String insertConsultSQL = "INSERT INTO consultation (appointment_id, date_consultation, diagnostic, observation, traitement, ordonnance, duree, statut_consultation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmtConsult = conn.prepareStatement(insertConsultSQL);

        pstmtConsult.setInt(1, rdvId);
        pstmtConsult.setDate(2, java.sql.Date.valueOf(date));
        pstmtConsult.setString(3, diagnostics[random.nextInt(diagnostics.length)]);
        pstmtConsult.setString(4, "Observation automatique " + rdvId);
        pstmtConsult.setString(5, "Traitement suggéré");
        pstmtConsult.setString(6, "Ordonnance type " + rdvId);
        pstmtConsult.setInt(7, 15 + random.nextInt(45)); // 15 to 60 mins
        pstmtConsult.setString(8, statutsConsult[random.nextInt(statutsConsult.length)]);

        pstmtConsult.executeUpdate();
    }
}
