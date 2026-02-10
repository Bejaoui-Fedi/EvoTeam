package org.example.services;

import org.example.entities.User;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements IService<User> {

    private final Connection conn;

    public ServiceUser() {
        this.conn = MyDataBase.getInstance().getMyConnection();
        if (this.conn == null) {
            throw new IllegalStateException("Connexion BD = NULL. Vérifie MyDataBase.");
        }
    }

    @Override
    public void insert(User u) throws SQLException {
        String sql = "INSERT INTO `user`(`nom`, `email`, `password`, `role`, `telephone`, `actif`) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRole());
            ps.setString(5, u.getTelephone());
            ps.setInt(6, u.getActif());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public void update(User u) throws SQLException {
        String sql = "UPDATE `user` SET `nom`=?, `email`=?, `password`=?, `role`=?, `telephone`=?, `actif`=? " +
                "WHERE `id`=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRole());
            ps.setString(5, u.getTelephone());
            ps.setInt(6, u.getActif());
            ps.setInt(7, u.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM `user` WHERE `id`=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<User> show() throws SQLException {
        String sql = "SELECT * FROM `user`";
        List<User> list = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM `user` WHERE `id`=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setNom(rs.getString("nom"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setTelephone(rs.getString("telephone"));
        u.setActif(rs.getInt("actif"));

        // dates (si elles existent / compatibles)
        Timestamp c = rs.getTimestamp("date_creation");
        Timestamp m = rs.getTimestamp("date_modification");
        if (c != null) u.setDateCreation(c.toLocalDateTime());
        if (m != null) u.setDateModification(m.toLocalDateTime());

        return u;
    }
}