package org.example.tn.esprit.mains;

import org.example.tn.esprit.entities.Objective;
import org.example.tn.esprit.entities.User;
import org.example.tn.esprit.services.ServiceObjective;
import org.example.tn.esprit.utils.MyDataBase;
import org.example.tn.esprit.utils.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ExerciseSeederCorrect {

    public static void main(String[] args) {
        // Mock admin session
        User admin = new User();
        admin.setRole("ADMIN");
        admin.setId(1);
        Session.currentUser = admin;

        ServiceObjective serviceObjective = new ServiceObjective();
        Connection conn = MyDataBase.getInstance().getMyConnection();

        try {
            List<Objective> objectives = serviceObjective.show();
            if (objectives.isEmpty()) {
                System.out.println("ERROR: No objectives found!");
                return;
            }

            int objCount = objectives.size();
            long obj1Id = objectives.get(0).getIdObjective();
            long obj2Id = (objCount > 1) ? objectives.get(1).getIdObjective() : obj1Id;
            long obj3Id = (objCount > 2) ? objectives.get(2).getIdObjective() : obj1Id;

            String sql = "INSERT INTO exercise " +
                    "(`user_id`, `objectiveId`, `title`, `description`, `type`, `durationMinutes`, `difficulty`, `mediaUrl`, `isPublished`) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            System.out.println("Starting Exercise seeding with correct ENUM values...");

            // Objective 1 - 3 exercises (respiration theme)
            insertExercise(ps, 1, obj1Id, "Respiration Profonde",
                    "Exercice de respiration abdominale pour reduire le stress.", "respiration", 10, "debutant",
                    "https://youtube.com/deepbreathing");
            insertExercise(ps, 1, obj1Id, "Respiration 4-7-8", "Technique de respiration pour favoriser le sommeil.",
                    "respiration", 5, "moyen", "https://youtube.com/478breathing");
            insertExercise(ps, 1, obj1Id, "Respiration Alternee",
                    "Respiration par les narines alternees pour equilibrer energie.", "respiration", 8, "avance",
                    "https://youtube.com/alternatebreathing");

            // Objective 2 - 3 exercises (meditation & relaxation)
            insertExercise(ps, 1, obj2Id, "Meditation Guidee", "Meditation de pleine conscience pour debutants.",
                    "meditation", 15, "debutant", "https://youtube.com/guidedmeditation");
            insertExercise(ps, 1, obj2Id, "Relaxation Musculaire", "Technique de relaxation progressive des muscles.",
                    "relaxation", 20, "moyen", "https://youtube.com/musclerelaxation");
            insertExercise(ps, 1, obj2Id, "Meditation Avancee", "Meditation en silence pour pratiquants experimentes.",
                    "meditation", 30, "avance", "https://youtube.com/advancedmeditation");

            // Objective 3 - 4 exercises (journaling, cbt, challenge)
            insertExercise(ps, 1, obj3Id, "Journal de Gratitude",
                    "Ecrire 3 choses pour lesquelles vous etes reconnaissant.", "journaling", 10, "debutant",
                    "https://youtube.com/gratitudejournal");
            insertExercise(ps, 1, obj3Id, "Restructuration Cognitive",
                    "Identifier et modifier les pensees negatives automatiques.", "cbt", 15, "moyen",
                    "https://youtube.com/cognitiverestructuring");
            insertExercise(ps, 1, obj3Id, "Defi Quotidien", "Sortir de sa zone de confort avec un petit defi.",
                    "challenge", 5, "moyen", "https://youtube.com/dailychallenge");
            insertExercise(ps, 1, obj3Id, "Journal Emotionnel", "Analyser et comprendre ses emotions du jour.",
                    "journaling", 12, "avance", "https://youtube.com/emotionaljournal");

            ps.close();
            System.out.println("=== Seeding completed successfully! ===");
            System.out.println("10 exercises have been added to the database.");

        } catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertExercise(PreparedStatement ps, int userId, long objectiveId, String title,
            String description, String type, int duration, String difficulty, String mediaUrl) {
        try {
            ps.setInt(1, userId);
            ps.setLong(2, objectiveId);
            ps.setString(3, title);
            ps.setString(4, description);
            ps.setString(5, type);
            ps.setInt(6, duration);
            ps.setString(7, difficulty);
            ps.setString(8, mediaUrl);
            ps.setInt(9, 1); // isPublished
            ps.executeUpdate();
            System.out.println("+ Inserted: " + title + " (" + type + ", " + difficulty + ")");
        } catch (SQLException e) {
            System.err.println("X Error inserting " + title + ": " + e.getMessage());
        }
    }
}
