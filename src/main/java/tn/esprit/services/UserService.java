package tn.esprit.services;

import tn.esprit.entities.User;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private Connection cnx;

    public UserService() {
        cnx = DBConnection.getInstance().getMyConnection();
    }

    // ✅ CREATE - Retourne l'ID généré
    public int add(User user) throws SQLException {
        String sql = "INSERT INTO user (nom, email, password, role, telephone, actif) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, user.getNom());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setString(4, user.getRole());
        ps.setString(5, user.getTelephone());
        ps.setBoolean(6, user.isActif());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }

    // ✅ READ - Tous les utilisateurs
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY id DESC";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setNom(rs.getString("nom"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            user.setTelephone(rs.getString("telephone"));
            user.setActif(rs.getBoolean("actif"));
            users.add(user);
        }
        return users;
    }

    // ✅ READ - Par email
    public User getByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setNom(rs.getString("nom"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            user.setTelephone(rs.getString("telephone"));
            user.setActif(rs.getBoolean("actif"));
            return user;
        }
        return null;
    }

    // ✅ READ - Par ID
    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setNom(rs.getString("nom"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            user.setTelephone(rs.getString("telephone"));
            user.setActif(rs.getBoolean("actif"));
            return user;
        }
        return null;
    }

    // ✅ UPDATE
    public void update(User user) throws SQLException {
        String sql = "UPDATE user SET nom = ?, telephone = ?, role = ?, actif = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, user.getNom());
        ps.setString(2, user.getTelephone());
        ps.setString(3, user.getRole());
        ps.setBoolean(4, user.isActif());
        ps.setInt(5, user.getId());
        ps.executeUpdate();
    }

    // ✅ UPDATE avec mot de passe
    public void updateWithPassword(User user) throws SQLException {
        String sql = "UPDATE user SET nom = ?, password = ?, telephone = ?, role = ?, actif = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, user.getNom());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getTelephone());
        ps.setString(4, user.getRole());
        ps.setBoolean(5, user.isActif());
        ps.setInt(6, user.getId());
        ps.executeUpdate();
    }

    // ✅ DELETE
    public void delete(User user) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, user.getId());
        ps.executeUpdate();
    }

    // ================== GET ALL USER IDs ==================
    public List<Integer> getAllUsersIds() throws SQLException {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT id FROM user ORDER BY id";

        try (PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                userIds.add(rs.getInt("id"));
            }
        }
        return userIds;
    }
}