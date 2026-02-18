package tn.esprit.services;

import tn.esprit.entities.WellbeingTracker;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceWellbeingTracker {

    private Connection connection = DBConnection.getInstance().getConnection();

    // ================== CREATE ==================
    public void ajouter(WellbeingTracker tracker) {
        // CORRIGÉ: userId → user_id, routineTaskId → daily_routine_task_id, sleepHours → sleep_hours, createdAt → created_at
        String sql = "INSERT INTO wellbeing_tracker (user_id, daily_routine_task_id, date, mood, stress, energy, sleep_hours, note, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, tracker.getUserId());
            pst.setInt(2, tracker.getRoutineTaskId());
            pst.setString(3, tracker.getDate());
            pst.setInt(4, tracker.getMood());
            pst.setInt(5, tracker.getStress());
            pst.setInt(6, tracker.getEnergy());
            pst.setDouble(7, tracker.getSleepHours());
            pst.setString(8, tracker.getNote());

            pst.executeUpdate();
            System.out.println("✅ WellbeingTracker ajouté !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== READ ALL ==================
    public List<WellbeingTracker> getAll() {
        List<WellbeingTracker> trackers = new ArrayList<>();
        String sql = "SELECT * FROM wellbeing_tracker ORDER BY date DESC";

        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                trackers.add(new WellbeingTracker(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("daily_routine_task_id"),
                        rs.getString("date"),
                        rs.getInt("mood"),
                        rs.getInt("stress"),
                        rs.getInt("energy"),
                        rs.getDouble("sleep_hours"),
                        rs.getString("note"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trackers;
    }

    // ================== UPDATE ==================
    public void update(WellbeingTracker tracker) {
        String sql = "UPDATE wellbeing_tracker SET user_id=?, daily_routine_task_id=?, date=?, " +
                "mood=?, stress=?, energy=?, sleep_hours=?, note=? WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, tracker.getUserId());
            pst.setInt(2, tracker.getRoutineTaskId());
            pst.setString(3, tracker.getDate());
            pst.setInt(4, tracker.getMood());
            pst.setInt(5, tracker.getStress());
            pst.setInt(6, tracker.getEnergy());
            pst.setDouble(7, tracker.getSleepHours());
            pst.setString(8, tracker.getNote());
            pst.setInt(9, tracker.getId());

            pst.executeUpdate();
            System.out.println("✅ WellbeingTracker modifié !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== DELETE ==================
    public void delete(int id) {
        String sql = "DELETE FROM wellbeing_tracker WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ WellbeingTracker supprimé !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== GET BY ID ==================
    public WellbeingTracker getById(int id) {
        String sql = "SELECT * FROM wellbeing_tracker WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new WellbeingTracker(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("daily_routine_task_id"),
                        rs.getString("date"),
                        rs.getInt("mood"),
                        rs.getInt("stress"),
                        rs.getInt("energy"),
                        rs.getDouble("sleep_hours"),
                        rs.getString("note"),
                        rs.getString("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ================== GET BY USER ID ==================
    public List<WellbeingTracker> getByUserId(int userId) {
        List<WellbeingTracker> trackers = new ArrayList<>();
        String sql = "SELECT * FROM wellbeing_tracker WHERE user_id=? ORDER BY date DESC";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                trackers.add(new WellbeingTracker(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("daily_routine_task_id"),
                        rs.getString("date"),
                        rs.getInt("mood"),
                        rs.getInt("stress"),
                        rs.getInt("energy"),
                        rs.getDouble("sleep_hours"),
                        rs.getString("note"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trackers;
    }

    // ================== GET ALL TRACKER IDS ==================
    public List<Integer> getAllTrackerIds() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM wellbeing_tracker ORDER BY id";
        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}