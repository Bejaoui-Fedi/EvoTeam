package org.example.tn.esprit.mains;

import org.example.tn.esprit.utils.MyDataBase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateMediaUrls {

    public static void main(String[] args) {
        Connection conn = MyDataBase.getInstance().getMyConnection();

        try {
            System.out.println("Updating media URLs for objectives and exercises...");

            // Update Objectives with relevant images from Unsplash
            updateObjective(conn, 1, "https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=800"); // Stress/meditation
            updateObjective(conn, 2, "https://images.unsplash.com/photo-1545389336-cf090694435e?w=800"); // Mindfulness/nature
            updateObjective(conn, 3, "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=800"); // Fitness/gym
            updateObjective(conn, 4, "https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=800"); // Nutrition/healthy
                                                                                                            // food
            updateObjective(conn, 5, "https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=800"); // Productivity/workspace
            updateObjective(conn, 6, "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=800"); // Relationships/connection
            updateObjective(conn, 7, "https://images.unsplash.com/photo-1447452001602-7090c7ab2db3?w=800"); // Sleep/rest
            updateObjective(conn, 8, "https://images.unsplash.com/photo-1512438248247-f0f2a5a8b7f0?w=800"); // Creativity/art
            updateObjective(conn, 9, "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=800"); // Learning/education
            updateObjective(conn, 10, "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?w=800"); // Gratitude/journal

            // Update Exercises with relevant images
            updateExercise(conn, 1, "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=800"); // Deep
                                                                                                        // breathing
            updateExercise(conn, 2, "https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=800"); // 4-7-8
                                                                                                           // breathing
            updateExercise(conn, 3, "https://images.unsplash.com/photo-1599901860904-17e6ed7083a0?w=800"); // Alternate
                                                                                                           // breathing
            updateExercise(conn, 4, "https://images.unsplash.com/photo-1508672019048-805c876b67e2?w=800"); // Guided
                                                                                                           // meditation
            updateExercise(conn, 5, "https://images.unsplash.com/photo-1545389336-cf090694435e?w=800"); // Muscle
                                                                                                        // relaxation
            updateExercise(conn, 6, "https://images.unsplash.com/photo-1447452001602-7090c7ab2db3?w=800"); // Advanced
                                                                                                           // meditation
            updateExercise(conn, 7, "https://images.unsplash.com/photo-1455390582262-044cdead277a?w=800"); // Gratitude
                                                                                                           // journal
            updateExercise(conn, 8, "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=800"); // Cognitive
                                                                                                           // restructuring
            updateExercise(conn, 9, "https://images.unsplash.com/photo-1484480974693-6ca0a78fb36b?w=800"); // Daily
                                                                                                           // challenge
            updateExercise(conn, 10, "https://images.unsplash.com/photo-1517842645767-c639042777db?w=800"); // Emotional
                                                                                                            // journal

            System.out.println("=== Media URLs updated successfully! ===");

        } catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void updateObjective(Connection conn, int id, String mediaUrl) throws SQLException {
        String sql = "UPDATE objective SET icon = ? WHERE id_objective = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mediaUrl);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("+ Updated objective " + id + " with image");
            }
        }
    }

    private static void updateExercise(Connection conn, int id, String mediaUrl) throws SQLException {
        String sql = "UPDATE exercise SET mediaUrl = ? WHERE id_exercise = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mediaUrl);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("+ Updated exercise " + id + " with image");
            }
        }
    }
}
