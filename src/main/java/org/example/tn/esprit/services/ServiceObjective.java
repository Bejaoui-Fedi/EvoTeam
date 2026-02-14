package org.example.tn.esprit.services;

import org.example.tn.esprit.entities.Objective;
import org.example.tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceObjective implements IService<Objective> {

    private Connection connection;

    public ServiceObjective() {
        connection = MyDataBase.getInstance().getMyConnection();
        if (connection == null) {
            throw new IllegalStateException("Connexion BD NULL dans ServiceObjective. Vérifie MyDataBase.");
        }
    }

    public List<Objective> getAll() throws SQLException {
        return show();
    }

    @Override
    public void insert(Objective objective) throws SQLException {
        String sql = "INSERT INTO objective (user_id, title, description, level, isPublished) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, objective.getUserId());
            ps.setString(2, objective.getTitle());
            ps.setString(3, objective.getDescription());
            ps.setString(4, objective.getLevel());
            ps.setInt(5, objective.isPublished());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    objective.setIdObjective(rs.getLong(1));
            }
        }
    }

    @Override
    public void update(Objective objective) throws SQLException {
        String sql = "UPDATE objective SET user_id=?, title=?, description=?, level=?, isPublished=? "
                + "WHERE id_objective=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, objective.getUserId());
            stmt.setString(2, objective.getTitle());
            stmt.setString(3, objective.getDescription());
            stmt.setString(4, objective.getLevel());
            stmt.setInt(5, objective.isPublished());
            stmt.setLong(6, objective.getIdObjective());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM objective WHERE `id_objective`=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Objective> show() throws SQLException {
        List<Objective> objectives = new ArrayList<>();
        String sql = "SELECT * FROM objective";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                objectives.add(map(rs));
            }
        }
        return objectives;
    }

    @Override
    public Objective getById(int id) throws SQLException {
        String sql = "SELECT * FROM objective WHERE id_objective = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    private Objective map(ResultSet rs) throws SQLException {
        Objective o = new Objective();
        o.setIdObjective(rs.getLong("id_objective"));
        o.setTitle(rs.getString("title"));
        o.setDescription(rs.getString("description"));
        o.setLevel(rs.getString("level"));
        o.setPublished(rs.getInt("isPublished"));
        o.setUserId(rs.getInt("user_id"));

        Timestamp c = rs.getTimestamp("createdAt");
        if (c != null)
            o.setCreatedAt(c.toLocalDateTime());

        Timestamp u = rs.getTimestamp("updatedAt");
        if (u != null)
            o.setUpdatedAt(u.toLocalDateTime());

        return o;
    }

    public List<Objective> showPublished() throws SQLException {
        List<Objective> list = new ArrayList<>();
        String sql = "SELECT * FROM objective WHERE isPublished = 1";
        try (Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }
}