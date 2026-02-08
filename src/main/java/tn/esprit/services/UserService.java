package tn.esprit.services;

import tn.esprit.entities.User;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {
    private Connection connection = DBConnection.getInstance().getConnection();

    @Override
    public void add(User u) throws SQLException {
        String sql = "INSERT INTO `user` (nom, email, password, role, telephone, actif) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, u.getNom());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getPassword());
        ps.setString(4, u.getRole());
        ps.setString(5, u.getTelephone());
        ps.setBoolean(6, u.isActif());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) u.setId(rs.getInt(1));

        System.out.println("User ajouté : " + u);
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM `user`";

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setNom(rs.getString("nom"));
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            u.setRole(rs.getString("role"));
            u.setTelephone(rs.getString("telephone"));
            u.setActif(rs.getBoolean("actif"));
            list.add(u);
        }
        return list;
    }

    @Override
    public void update(User u) throws SQLException {
        String sql = "UPDATE `user` SET nom=?, email=?, password=?, role=?, telephone=?, actif=? WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, u.getNom());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getPassword());
        ps.setString(4, u.getRole());
        ps.setString(5, u.getTelephone());
        ps.setBoolean(6, u.isActif());
        ps.setInt(7, u.getId());

        ps.executeUpdate();
        System.out.println("User mis à jour : " + u);
    }

    @Override
    public void delete(User u) throws SQLException {
        String sql = "DELETE FROM `user` WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, u.getId());
        ps.executeUpdate();

        System.out.println("User supprimé : " + u.getId());
    }
}