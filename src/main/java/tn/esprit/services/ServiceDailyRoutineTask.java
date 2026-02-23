package tn.esprit.services;

import tn.esprit.entities.DailyRoutineTask;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDailyRoutineTask {

    private Connection connection = DBConnection.getInstance().getConnection();

    // ================== CREATE ==================
    public void ajouter(DailyRoutineTask task) {
        // REMOVED: wellbeing_tracker_id - it doesn't exist in your database!
        String sql = "INSERT INTO daily_routine_task (user_id, title, is_completed, completed_at, created_at) " +
                "VALUES (?, ?, ?, ?, NOW())";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, task.getUserId());
            pst.setString(2, task.getTitle());
            pst.setBoolean(3, task.isCompleted());
            pst.setString(4, task.getCompletedAt());

            pst.executeUpdate();
            System.out.println("✅ DailyRoutineTask ajoutée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== READ ALL ==================
    public List<DailyRoutineTask> getAll() {
        List<DailyRoutineTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM daily_routine_task ORDER BY created_at DESC";

        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                tasks.add(new DailyRoutineTask(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getBoolean("is_completed"),
                        rs.getString("completed_at"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // ================== UPDATE ==================
    public void update(DailyRoutineTask task) {
        // REMOVED: wellbeing_tracker_id
        String sql = "UPDATE daily_routine_task SET user_id=?, title=?, " +
                "is_completed=?, completed_at=? WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, task.getUserId());
            pst.setString(2, task.getTitle());
            pst.setBoolean(3, task.isCompleted());
            pst.setString(4, task.getCompletedAt());
            pst.setInt(5, task.getId());

            pst.executeUpdate();
            System.out.println("✅ DailyRoutineTask modifiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== DELETE ==================
    public void delete(int id) {
        String sql = "DELETE FROM daily_routine_task WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ DailyRoutineTask supprimée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== GET BY ID ==================
    public DailyRoutineTask getById(int id) {
        String sql = "SELECT * FROM daily_routine_task WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new DailyRoutineTask(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getBoolean("is_completed"),
                        rs.getString("completed_at"),
                        rs.getString("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ================== GET BY USER ID ==================
    public List<DailyRoutineTask> getByUserId(int userId) {
        List<DailyRoutineTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM daily_routine_task WHERE user_id=? ORDER BY created_at DESC";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tasks.add(new DailyRoutineTask(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getBoolean("is_completed"),
                        rs.getString("completed_at"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // ================== MARK AS COMPLETED ==================
    public void markAsCompleted(int taskId) {
        String sql = "UPDATE daily_routine_task SET is_completed=1, completed_at=NOW() WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, taskId);
            pst.executeUpdate();
            System.out.println("✅ Tâche marquée comme complétée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== GET ALL TASK IDS ==================
    public List<Integer> getAllTaskIds() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM daily_routine_task";
        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) ids.add(rs.getInt("id"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}