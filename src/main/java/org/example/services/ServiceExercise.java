package org.example.services;

import org.example.entities.Exercise;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceExercise {

    private final Connection conn;

    public ServiceExercise() {
        this.conn = MyDataBase.getInstance().getMyConnection();
        if (this.conn == null) {
            throw new IllegalStateException("Connexion BD = NULL. Vérifie MyDataBase (URL/user/password).");
        }
    }

    // ==========================================================
    // INSERT (Option B: user_id + objectiveId + autres colonnes)
    // ==========================================================
    public void insert(Exercise e) throws SQLException {

        //  Vérification cohérence user_id / objectiveId (recommandé)
        if (!objectiveBelongsToUser(e.getObjectiveId(), e.getUserId())) {
            throw new SQLException("Cohérence invalide: objectiveId=" + e.getObjectiveId()
                    + " n'appartient pas au user_id=" + e.getUserId());
        }

        String sql = "INSERT INTO `exercise` " +
                "(`user_id`, `objectiveId`, `title`, `description`, `type`, `durationMinutes`, `difficulty`, `mediaUrl`, `isPublished`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getUserId());
            ps.setInt(2, e.getObjectiveId());
            ps.setString(3, e.getTitle());
            ps.setString(4, e.getDescription());
            ps.setString(5, e.getType());
            ps.setInt(6, e.getDurationMinutes());
            ps.setString(7, e.getDifficulty());
            ps.setString(8, e.getMediaUrl());
            ps.setInt(9, e.getIsPublished()); // 0/1

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setIdExercise(rs.getInt(1));
                }
            }
        }
    }

    // ==========================================================
    // UPDATE
    // ==========================================================
    public void update(Exercise e) throws SQLException {

        //  Vérification cohérence user_id / objectiveId (recommandé)
        if (!objectiveBelongsToUser(e.getObjectiveId(), e.getUserId())) {
            throw new SQLException("Cohérence invalide: objectiveId=" + e.getObjectiveId()
                    + " n'appartient pas au user_id=" + e.getUserId());
        }

        String sql = "UPDATE `exercise` SET " +
                "`user_id`=?, `objectiveId`=?, `title`=?, `description`=?, `type`=?, `durationMinutes`=?, " +
                "`difficulty`=?, `mediaUrl`=?, `isPublished`=? " +
                "WHERE `id_exercise`=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getUserId());
            ps.setInt(2, e.getObjectiveId());
            ps.setString(3, e.getTitle());
            ps.setString(4, e.getDescription());
            ps.setString(5, e.getType());
            ps.setInt(6, e.getDurationMinutes());
            ps.setString(7, e.getDifficulty());
            ps.setString(8, e.getMediaUrl());
            ps.setInt(9, e.getIsPublished());
            ps.setInt(10, e.getIdExercise());

            ps.executeUpdate();
        }
    }

    // ==========================================================
    // DELETE
    // ==========================================================
    public void delete(int idExercise) throws SQLException {
        String sql = "DELETE FROM `exercise` WHERE `id_exercise`=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idExercise);
            ps.executeUpdate();
        }
    }

    // ==========================================================
    // SELECT ALL
    // ==========================================================
    public List<Exercise> getAll() throws SQLException {
        String sql = "SELECT * FROM `exercise`";
        List<Exercise> list = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToExercise(rs));
            }
        }
        return list;
    }

    // ==========================================================
    // SELECT BY ID
    // ==========================================================
    public Exercise getById(int idExercise) throws SQLException {
        String sql = "SELECT * FROM `exercise` WHERE `id_exercise`=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idExercise);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToExercise(rs);
            }
        }
        return null;
    }

    // ==========================================================
    // SELECT BY objectiveId
    // ==========================================================
    public List<Exercise> getByObjectiveId(int objectiveId) throws SQLException {
        String sql = "SELECT * FROM `exercise` WHERE `objectiveId`=?";
        List<Exercise> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, objectiveId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowToExercise(rs));
            }
        }
        return list;
    }

    // ==========================================================
    // SELECT BY user_id (Option B utile)
    // ==========================================================
    public List<Exercise> getByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM `exercise` WHERE `user_id`=?";
        List<Exercise> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowToExercise(rs));
            }
        }
        return list;
    }

    // ==========================================================
    // PUBLISH / UNPUBLISH
    // ==========================================================
    public void setPublished(int idExercise, int published) throws SQLException {
        String sql = "UPDATE `exercise` SET `isPublished`=? WHERE `id_exercise`=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, published); // 0/1
            ps.setInt(2, idExercise);
            ps.executeUpdate();
        }
    }

    // ==========================================================
    // COHERENCE CHECK (objectiveId doit appartenir au user_id)
    // ==========================================================
    private boolean objectiveBelongsToUser(int objectiveId, int userId) throws SQLException {
        String sql = "SELECT 1 FROM `objective` WHERE `id_objective`=? AND `user_id`=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, objectiveId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ==========================================================
    // MAPPER ResultSet -> Exercise (corrigé difficulty)
    // ==========================================================
    private Exercise mapRowToExercise(ResultSet rs) throws SQLException {
        Exercise e = new Exercise();

        e.setIdExercise(rs.getInt("id_exercise"));
        e.setUserId(rs.getInt("user_id"));                 //  Option B
        e.setObjectiveId(rs.getInt("objectiveId"));

        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setType(rs.getString("type"));
        e.setDurationMinutes(rs.getInt("durationMinutes"));

        e.setDifficulty(rs.getString("difficulty"));      //  FIX (pas level)
        e.setMediaUrl(rs.getString("mediaUrl"));
        e.setIsPublished(rs.getInt("isPublished"));

        // createdAt / updatedAt : safe si colonnes existent
        setIfTimestampExists(rs, e);

        return e;
    }

    // ==========================================================
    // Helper: read createdAt/updatedAt safely (si colonnes absentes -> ignore)
    // ==========================================================
    private void setIfTimestampExists(ResultSet rs, Exercise e) {
        try {
            Timestamp createdTs = rs.getTimestamp("createdAt");
            if (createdTs != null) e.setCreatedAt(createdTs.toLocalDateTime());
        } catch (SQLException ignored) {}

        try {
            Timestamp updatedTs = rs.getTimestamp("updatedAt");
            if (updatedTs != null) e.setUpdatedAt(updatedTs.toLocalDateTime());
        } catch (SQLException ignored) {}
    }
}