package tn.esprit.services;

import tn.esprit.entities.Exercise;
import tn.esprit.utils.DBConnection;
import tn.esprit.utils.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceExercise implements IEService<Exercise> {

    private final Connection connection;

    public ServiceExercise() {
        connection = DBConnection.getInstance().getConnection();
        if (connection == null) {
            throw new IllegalStateException("Connexion BD NULL dans ServiceExercise. Vérifie MyDataBase.");
        }
    }

    public List<Exercise> getByObjectiveId(int objectiveId) throws SQLException {
        String sql = "SELECT " +
                " id_exercise AS idExercise, " +
                " user_id     AS userId, " +
                " objectiveId AS objectiveId, " +
                " title, description, `type`, " +
                " durationMinutes AS durationMinutes, " +
                " difficulty, " +
                " mediaUrl AS mediaUrl, " +
                " steps, " +
                " isPublished AS isPublished, " +
                " createdAt AS createdAt, " +
                " updatedAt AS updatedAt " +
                "FROM exercise WHERE objectiveId = ?";

        return queryList(sql, objectiveId);
    }

    // ✅ USER voit seulement publiés
    public List<Exercise> getByObjectiveIdPublished(int objectiveId) throws SQLException {
        String sql = "SELECT " +
                " id_exercise AS idExercise, " +
                " user_id     AS userId, " +
                " objectiveId AS objectiveId, " +
                " title, description, `type`, " +
                " durationMinutes AS durationMinutes, " +
                " difficulty, " +
                " mediaUrl AS mediaUrl, " +
                " steps, " +
                " isPublished AS isPublished, " +
                " createdAt AS createdAt, " +
                " updatedAt AS updatedAt " +
                "FROM exercise WHERE objectiveId = ? AND isPublished = 1";

        return queryList(sql, objectiveId);
    }

    private List<Exercise> queryList(String sql, int objectiveId) throws SQLException {
        List<Exercise> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, objectiveId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(map(rs));
            }
        }
        return list;
    }

    // ===== CRUD ADMIN ONLY (backend security) =====
    @Override
    public void insert(Exercise e) throws SQLException {
        if (!Session.isAdmin())
            throw new SecurityException("ADMIN only");

        String sql = "INSERT INTO exercise " +
                "(`user_id`, `objectiveId`, `title`, `description`, `type`, `durationMinutes`, `difficulty`, `mediaUrl`, `steps`, `isPublished`, `createdAt`, `updatedAt`) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getUserId());
            ps.setInt(2, e.getObjectiveId());
            ps.setString(3, e.getTitle());
            ps.setString(4, e.getDescription());
            ps.setString(5, e.getType());
            ps.setInt(6, e.getDurationMinutes());
            ps.setString(7, e.getDifficulty());
            ps.setString(8, e.getMediaUrl());
            ps.setString(9, e.getSteps());
            ps.setInt(10, e.getIsPublished());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    e.setIdExercise(keys.getInt(1));
            }
        }
    }

    @Override
    public void update(Exercise e) throws SQLException {
        if (!Session.isAdmin())
            throw new SecurityException("ADMIN only");

        String sql = "UPDATE exercise SET " +
                " `user_id`=?, `objectiveId`=?, `title`=?, `description`=?, `type`=?, `durationMinutes`=?, `difficulty`=?, `mediaUrl`=?, `steps`=?, `isPublished`=?, `updatedAt`=NOW() "
                +
                "WHERE `id_exercise`=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, e.getUserId());
            ps.setInt(2, e.getObjectiveId());
            ps.setString(3, e.getTitle());
            ps.setString(4, e.getDescription());
            ps.setString(5, e.getType());
            ps.setInt(6, e.getDurationMinutes());
            ps.setString(7, e.getDifficulty());
            ps.setString(8, e.getMediaUrl());
            ps.setString(9, e.getSteps());
            ps.setInt(10, e.getIsPublished());
            ps.setInt(11, e.getIdExercise());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int idExercise) throws SQLException {
        if (!Session.isAdmin())
            throw new SecurityException("ADMIN only");

        String sql = "DELETE FROM exercise WHERE id_exercise=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idExercise);
            ps.executeUpdate();
        }
    }

    @Override
    public Exercise getById(int id) throws SQLException {
        String sql = "SELECT " +
                " id_exercise AS idExercise, " +
                " user_id     AS userId, " +
                " objectiveId AS objectiveId, " +
                " title, description, `type`, " +
                " durationMinutes AS durationMinutes, " +
                " difficulty, " +
                " mediaUrl AS mediaUrl, " +
                " steps, " +
                " isPublished AS isPublished, " +
                " createdAt AS createdAt, " +
                " updatedAt AS updatedAt " +
                "FROM exercise WHERE id_exercise = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return map(rs);
            }
        }
        return null;
    }

    @Override
    public List<Exercise> show() throws SQLException {
        String sql = "SELECT " +
                " id_exercise AS idExercise, " +
                " user_id     AS userId, " +
                " objectiveId AS objectiveId, " +
                " title, description, `type`, " +
                " durationMinutes AS durationMinutes, " +
                " difficulty, " +
                " mediaUrl AS mediaUrl, " +
                " steps, " +
                " isPublished AS isPublished, " +
                " createdAt AS createdAt, " +
                " updatedAt AS updatedAt " +
                "FROM exercise";
        List<Exercise> list = new ArrayList<>();
        try (Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    private Exercise map(ResultSet rs) throws SQLException {
        Exercise e = new Exercise();

        e.setIdExercise(rs.getInt("idExercise"));
        e.setUserId(rs.getInt("userId"));
        e.setObjectiveId(rs.getInt("objectiveId"));

        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setType(rs.getString("type"));
        e.setDurationMinutes(rs.getInt("durationMinutes"));
        e.setDifficulty(rs.getString("difficulty"));
        e.setMediaUrl(rs.getString("mediaUrl"));
        e.setSteps(rs.getString("steps"));
        e.setIsPublished(rs.getInt("isPublished"));

        Timestamp c = rs.getTimestamp("createdAt");
        if (c != null)
            e.setCreatedAt(c.toLocalDateTime());

        Timestamp u = rs.getTimestamp("updatedAt");
        if (u != null)
            e.setUpdatedAt(u.toLocalDateTime());

        return e;
    }
}