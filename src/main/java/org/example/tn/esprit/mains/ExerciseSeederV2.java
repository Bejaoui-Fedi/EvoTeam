package org.example.tn.esprit.mains;

import org.example.tn.esprit.entities.Exercise;
import org.example.tn.esprit.entities.Objective;
import org.example.tn.esprit.entities.User;
import org.example.tn.esprit.services.ServiceExercise;
import org.example.tn.esprit.services.ServiceObjective;
import org.example.tn.esprit.utils.Session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExerciseSeederV2 {

    public static void main(String[] args) {
        User admin = new User();
        admin.setRole("ADMIN");
        admin.setId(1);
        Session.currentUser = admin;

        ServiceObjective serviceObjective = new ServiceObjective();
        ServiceExercise serviceExercise = new ServiceExercise();

        try {
            List<Objective> objectives = serviceObjective.show();
            if (objectives.isEmpty()) {
                System.out.println("ERROR: No objectives found! Please run ObjectiveSeeder first.");
                return;
            }

            int objCount = objectives.size();
            Objective obj1 = objectives.get(0);
            Objective obj2 = (objCount > 1) ? objectives.get(1) : obj1;
            Objective obj3 = (objCount > 2) ? objectives.get(2) : obj1;

            List<Exercise> exercises = new ArrayList<>();

            // 1
            exercises.add(new Exercise(1, Math.toIntExact(obj1.getIdObjective()),
                    "Burpees Intenses", "Série de 3x15 burpees pour le cardio.", "Cardio", 15, "Difficile",
                    "https://youtube.com/burpees", 1));
            exercises.add(new Exercise(1, Math.toIntExact(obj1.getIdObjective()),
                    "Jumping Jacks", "Saut avec écart latéral pour l'échauffement.", "Cardio", 10, "Facile",
                    "https://youtube.com/jumpingjacks", 1));
            exercises.add(new Exercise(1, Math.toIntExact(obj1.getIdObjective()),
                    "Mountain Climbers", "Exercice au sol simulant l'escalade.", "Cardio", 12, "Moyen",
                    "https://youtube.com/mountainclimbers", 1));

            // 2
            exercises.add(new Exercise(1, Math.toIntExact(obj2.getIdObjective()),
                    "Salutation au Soleil", "Enchaînement de postures de yoga fluide.", "Souplesse", 20, "Moyen",
                    "https://youtube.com/suryanamaskar", 1));
            exercises.add(new Exercise(1, Math.toIntExact(obj2.getIdObjective()),
                    "Posture de l'Arbre", "Équilibre et concentration sur une jambe.", "Équilibre", 5, "Facile",
                    "https://youtube.com/tree", 1));
            exercises.add(new Exercise(1, Math.toIntExact(obj2.getIdObjective()),
                    "Chien Tête en Bas", "Étirement complet du dos et des jambes.", "Souplesse", 5, "Moyen",
                    "https://youtube.com/downdog", 1));

            // 3
            exercises.add(new Exercise(1, Math.toIntExact(obj3.getIdObjective()),
                    "Développé Couché", "Exercice roi pour les pectoraux (Barre/Haltères).", "Musculation", 45,
                    "Difficile", "https://youtube.com/benchpress", 1));
            exercises.add(new Exercise(1, Math.toIntExact(obj3.getIdObjective()),
                    "Squat Complet", "Flexion des jambes pour les quadriceps et fessiers.", "Musculation", 30,
                    "Difficile", "https://youtube.com/squat", 1));
            exercises.add(new Exercise(1, Math.toIntExact(obj3.getIdObjective()),
                    "Soulevé de Terre", "Exercice polyarticulaire pour tout le corps.", "Musculation", 40, "Difficile",
                    "https://youtube.com/deadlift", 1));
            exercises.add(new Exercise(1, Math.toIntExact(obj3.getIdObjective()),
                    "Traction", "Exercice de tirage pour le dos et les biceps.", "Musculation", 15, "Difficile",
                    "https://youtube.com/pullup", 1));

            System.out.println("Starting Exercise seeding V2...");
            for (Exercise ex : exercises) {
                try {
                    serviceExercise.insert(ex);
                    System.out.println("Inserted: " + ex.getTitle());
                } catch (Exception e) {
                    System.out.println("ERROR inserting " + ex.getTitle() + ": " + e.getMessage());
                }
            }
            System.out.println("Finished.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
