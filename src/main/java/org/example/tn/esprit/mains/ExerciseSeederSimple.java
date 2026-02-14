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

public class ExerciseSeederSimple {

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

            // Simplified SQL without type - let's see what columns are actually required
            String sql = "INSERT INTO exercise " +
                    "(`user_id`, `objectiveId`, `title`, `description`, `durationMinutes`, `difficulty`, `mediaUrl`, `isPublished`) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            System.out.println("Starting Exercise seeding (simplified)...");

            // Objective 1 - 3 exercises
            insertExercise(ps, 1, obj1Id, "Burpees Intenses", "Serie de 3x15 burpees pour le cardio.", 15, "Difficile",
                    "https://youtube.com/burpees");
            insertExercise(ps, 1, obj1Id, "Jumping Jacks", "Saut avec ecart lateral pour echauffement.", 10, "Facile",
                    "https://youtube.com/jumpingjacks");
            insertExercise(ps, 1, obj1Id, "Mountain Climbers", "Exercice au sol simulant escalade.", 12, "Moyen",
                    "https://youtube.com/mountainclimbers");

            // Objective 2 - 3 exercises
            insertExercise(ps, 1, obj2Id, "Salutation au Soleil", "Enchainement de postures de yoga fluide.", 20,
                    "Moyen", "https://youtube.com/suryanamaskar");
            insertExercise(ps, 1, obj2Id, "Posture de Arbre", "Equilibre et concentration sur une jambe.", 5, "Facile",
                    "https://youtube.com/tree");
            insertExercise(ps, 1, obj2Id, "Chien Tete en Bas", "Etirement complet du dos et des jambes.", 5, "Moyen",
                    "https://youtube.com/downdog");

            // Objective 3 - 4 exercises
            insertExercise(ps, 1, obj3Id, "Developpe Couche", "Exercice roi pour les pectoraux.", 45, "Difficile",
                    "https://youtube.com/benchpress");
            insertExercise(ps, 1, obj3Id, "Squat Complet", "Flexion des jambes pour quadriceps et fessiers.", 30,
                    "Difficile", "https://youtube.com/squat");
            insertExercise(ps, 1, obj3Id, "Souleve de Terre", "Exercice polyarticulaire pour tout le corps.", 40,
                    "Difficile", "https://youtube.com/deadlift");
            insertExercise(ps, 1, obj3Id, "Traction", "Exercice de tirage pour le dos et les biceps.", 15, "Difficile",
                    "https://youtube.com/pullup");

            ps.close();
            System.out.println("Seeding completed successfully!");

        } catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertExercise(PreparedStatement ps, int userId, long objectiveId, String title,
            String description, int duration, String difficulty, String mediaUrl) {
        try {
            ps.setInt(1, userId);
            ps.setLong(2, objectiveId);
            ps.setString(3, title);
            ps.setString(4, description);
            ps.setInt(5, duration);
            ps.setString(6, difficulty);
            ps.setString(7, mediaUrl);
            ps.setInt(8, 1); // isPublished
            ps.executeUpdate();
            System.out.println("Inserted: " + title);
        } catch (SQLException e) {
            System.err.println("Error inserting " + title + ": " + e.getMessage());
        }
    }
}
