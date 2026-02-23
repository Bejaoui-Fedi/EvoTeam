package tn.esprit.services;

import tn.esprit.entities.Event;
import tn.esprit.utils.DBConnection;

import java.util.ArrayList;
import java.util.List;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvent {

    private Connection connection = DBConnection.getInstance().getMyConnection();

    // ================== CREATE ==================
    public void ajouter(Event event) {
        String sql = "INSERT INTO evenement (name, startDate, endDate, maxParticipants, description, fee, location) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, event.getName());
            pst.setString(2, event.getStartDate());
            pst.setString(3, event.getEndDate());
            pst.setInt(4, event.getMaxParticipants());
            pst.setString(5, event.getDescription());
            pst.setInt(6, event.getFee());
            pst.setString(7, event.getLocation());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== READ ==================
    public List<Event> getAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM evenement";

        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("eventId"),
                        rs.getString("name"),
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        rs.getInt("maxParticipants"),
                        rs.getString("description"),
                        rs.getInt("fee"),
                        rs.getString("location")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    // ================== DELETE ==================
    public void delete(int eventId) {
        String sql = "DELETE FROM evenement WHERE eventId = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, eventId);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== UPDATE ==================
    public void update(Event event) {
        String sql = "UPDATE evenement SET name=?, startDate=?, endDate=?, maxParticipants=?, " +
                "description=?, fee=?, location=? WHERE eventId=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, event.getName());
            pst.setString(2, event.getStartDate());
            pst.setString(3, event.getEndDate());
            pst.setInt(4, event.getMaxParticipants());
            pst.setString(5, event.getDescription());
            pst.setInt(6, event.getFee());
            pst.setString(7, event.getLocation());
            pst.setInt(8, event.getEventId());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== joinure ==================
    public List<Integer> getAllEventIds() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT eventId FROM evenement";
        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) ids.add(rs.getInt("eventId"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }



    // ================== GET BY ID ==================
    public Event getById(int id) {
        String sql = "SELECT * FROM evenement WHERE eventId = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Event(
                        rs.getInt("eventId"),
                        rs.getString("name"),
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        rs.getInt("maxParticipants"),
                        rs.getString("description"),
                        rs.getInt("fee"),
                        rs.getString("location")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
