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
        purgeData();
        insertTestData();
    }

    public static void purgeData() {
        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null)
            return;

        try (Statement stmt = conn.createStatement()) {
            System.out.println("🧹 Nettoyage de la base de données...");
            stmt.executeUpdate("DELETE FROM consultation");
            stmt.executeUpdate("DELETE FROM appointment");
            stmt.executeUpdate("ALTER TABLE consultation AUTO_INCREMENT = 1");
            stmt.executeUpdate("ALTER TABLE appointment AUTO_INCREMENT = 1");
            System.out.println("✅ Base de données nettoyée.");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors du nettoyage : " + e.getMessage());
        }
    }

    public static void insertTestData() {
        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null)
            return;

        Random random = new Random();

        String[] motifs = {
                "Consultation de Cardiologie", "Examen Ophtalmologique", "Suivi Dermatologique",
                "Bilan Pédiatrique", "Consultation Gynécologique", "Suivi Psychologique",
                "Urgence Dentaire", "Bilan de Santé Annuel", "Consultation de Nutrition",
                "Suivi Post-Opératoire"
        };

        String[] types = { "Présentiel", "Téléconsultation" };
        String[] statutsRdv = { "en attente", "confirmé", "terminé", "annulé" };

        String[] diagnostics = {
                "Hypertension légère", "Myopie progressive", "Eczéma atopique",
                "Carence en Vitamine D", "Stress chronique", "Infection bénigne",
                "Récupération normale", "Besoin de suivi approfondi"
        };

        String[] observations = {
                "Patient coopératif, signes vitaux stables.",
                "Légère fatigue signalée par le patient.",
                "Symptômes persistants depuis 3 jours.",
                "Amélioration notable par rapport à la dernière visite.",
                "Nécessite un examen complémentaire le mois prochain.",
                "Examen de routine sans particularité."
        };

        String[] traitements = {
                "Repos recommandé et hydratation.",
                "Application locale de crème 2x/jour.",
                "Cure de vitamines pendant 3 mois.",
                "Surveillance quotidienne de la tension.",
                "Séances de relaxation bimensuelles.",
                "Antibiotiques pour 7 jours."
        };

        String[] statutsConsult = { "Clôturée", "En cours" };

        try {
            String insertRdvSQL = "INSERT INTO appointment (date_rdv, heure_rdv, statut, motif, type_rdv, user_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmtRdv = conn.prepareStatement(insertRdvSQL, Statement.RETURN_GENERATED_KEYS);

            System.out.println("⏳ Insertion de 60 rendez-vous réalistes...");
            for (int i = 1; i <= 60; i++) {
                // Distribute dates: 20 past, 10 today/tomorrow, 30 future
                LocalDate date;
                if (i <= 20) {
                    date = LocalDate.now().minusDays(random.nextInt(60) + 1);
                } else if (i <= 30) {
                    date = LocalDate.now().plusDays(random.nextInt(2));
                } else {
                    date = LocalDate.now().plusDays(random.nextInt(90) + 2);
                }

                LocalTime time = LocalTime.of(8 + random.nextInt(10), random.nextBoolean() ? 0 : 30);

                pstmtRdv.setDate(1, java.sql.Date.valueOf(date));
                pstmtRdv.setTime(2, java.sql.Time.valueOf(time));

                String rdvStatus;
                if (date.isBefore(LocalDate.now())) {
                    rdvStatus = random.nextDouble() > 0.8 ? "annulé" : "terminé";
                } else if (date.equals(LocalDate.now())) {
                    rdvStatus = "confirmé";
                } else {
                    rdvStatus = random.nextBoolean() ? "en attente" : "confirmé";
                }

                pstmtRdv.setString(3, rdvStatus);
                pstmtRdv.setString(4, motifs[random.nextInt(motifs.length)]);
                pstmtRdv.setString(5, types[random.nextInt(types.length)]);
                pstmtRdv.setInt(6, 1); // Forçage sur user_id 1 (généralement admin ou user de test)

                pstmtRdv.executeUpdate();

                // Insert consultation for terminated past appointments
                if ("terminé".equals(rdvStatus)) {
                    ResultSet rs = pstmtRdv.getGeneratedKeys();
                    if (rs.next()) {
                        int rdvId = rs.getInt(1);
                        insertConsultation(conn, rdvId, date, diagnostics, observations, traitements, statutsConsult,
                                random);
                    }
                }
            }
            System.out.println("✅ Données générées avec succès.");

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        }
    }

    private static void insertConsultation(Connection conn, int rdvId, LocalDate date,
            String[] diagnostics, String[] observations, String[] traitements,
            String[] statutsConsult, Random random) throws SQLException {

        String insertConsultSQL = "INSERT INTO consultation (appointment_id, date_consultation, diagnostic, observation, traitement, ordonnance, duree, statut_consultation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmtConsult = conn.prepareStatement(insertConsultSQL);

        pstmtConsult.setInt(1, rdvId);
        pstmtConsult.setDate(2, java.sql.Date.valueOf(date));
        pstmtConsult.setString(3, diagnostics[random.nextInt(diagnostics.length)]);
        pstmtConsult.setString(4, observations[random.nextInt(observations.length)]);
        pstmtConsult.setString(5, traitements[random.nextInt(traitements.length)]);
        pstmtConsult.setString(6, "Ordonnance Ref-" + (1000 + rdvId));
        pstmtConsult.setInt(7, 15 + random.nextInt(31)); // 15 to 45 mins
        pstmtConsult.setString(8, "Clôturée");

        pstmtConsult.executeUpdate();
    }
}
