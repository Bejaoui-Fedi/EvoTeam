package tn.esprit.services;

import tn.esprit.entities.UserProfile;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserProfileService implements IService<UserProfile> {
    private Connection connection = DBConnection.getInstance().getConnection();

    @Override
    public void add(UserProfile p) throws SQLException {

        String sql = "INSERT INTO user_profile (user_id, avatar, bio, date_naissance, langue, notifications_email, notifications_sms, parametres_confidentialite) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, p.getUserId());
        ps.setString(2, p.getAvatar());
        ps.setString(3, p.getBio());
        ps.setDate(4, p.getDateNaissance() != null ? Date.valueOf(p.getDateNaissance()) : null);
        ps.setString(5, p.getLangue());
        ps.setBoolean(6, p.isNotificationsEmail());
        ps.setBoolean(7, p.isNotificationsSms());
        ps.setString(8, p.getParametresConfidentialite());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) p.setId(rs.getInt(1));

        System.out.println("Profil ajouté : " + p);
    }

    @Override
    public List<UserProfile> getAll() throws SQLException {
        List<UserProfile> list = new ArrayList<>();
        String sql = "SELECT * FROM user_profile";

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            UserProfile p = new UserProfile();
            p.setId(rs.getInt("id"));
            p.setUserId(rs.getInt("user_id"));
            p.setAvatar(rs.getString("avatar"));
            p.setBio(rs.getString("bio"));
            p.setLangue(rs.getString("langue"));
            list.add(p);
        }

        return list;
    }

    @Override
    public void update(UserProfile p) throws SQLException {
        String sql = "UPDATE user_profile SET avatar=?, bio=?, langue=?, notifications_email=?, notifications_sms=?, parametres_confidentialite=? WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getAvatar());
        ps.setString(2, p.getBio());
        ps.setString(3, p.getLangue());
        ps.setBoolean(4, p.isNotificationsEmail());
        ps.setBoolean(5, p.isNotificationsSms());
        ps.setString(6, p.getParametresConfidentialite());
        ps.setInt(7, p.getId());

        ps.executeUpdate();
        System.out.println("Profil mis à jour : " + p);
    }

    @Override
    public void delete(UserProfile p) throws SQLException {
        String sql = "DELETE FROM user_profile WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, p.getId());
        ps.executeUpdate();

        System.out.println("Profil supprimé : " + p.getId());
    }
}