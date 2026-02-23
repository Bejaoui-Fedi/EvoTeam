package tn.esprit.services;

import tn.esprit.entities.UserProfile;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserProfileService {

    private Connection connection;

    public UserProfileService() {
        this.connection =DBConnection.getInstance().getMyConnection();
    }

    // Récupérer le profil par user_id
    public UserProfile getByUserId(int userId) {
        String query = "SELECT * FROM user_profile WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUserProfile(rs);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur getByUserId: " + e.getMessage());
        }
        return null;
    }

    // Créer un profil par défaut
    public UserProfile createDefaultProfile(int userId) {
        String query = "INSERT INTO user_profile (user_id, langue, notifications_email, notifications_sms, parametres_confidentialite, date_creation, date_modification) VALUES (?, 'FR', false, false, 'PUBLIC', NOW(), NOW())";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return getByUserId(userId);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur createDefaultProfile: " + e.getMessage());
        }
        return null;
    }

    // Mettre à jour le profil
    public boolean update(UserProfile profile) {
        String query = "UPDATE user_profile SET avatar = ?, bio = ?, date_naissance = ?, langue = ?, notifications_email = ?, notifications_sms = ?, parametres_confidentialite = ?, date_modification = NOW() WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, profile.getAvatar());
            stmt.setString(2, profile.getBio());
            stmt.setDate(3, profile.getDateNaissance() != null ? Date.valueOf(profile.getDateNaissance()) : null);
            stmt.setString(4, profile.getLangue());
            stmt.setBoolean(5, profile.isNotificationsEmail());
            stmt.setBoolean(6, profile.isNotificationsSms());
            stmt.setString(7, profile.getParametresConfidentialite());
            stmt.setInt(8, profile.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Erreur update: " + e.getMessage());
            return false;
        }
    }

    // Extraire UserProfile du ResultSet
    private UserProfile extractUserProfile(ResultSet rs) throws SQLException {
        UserProfile profile = new UserProfile();
        profile.setId(rs.getInt("id"));
        profile.setUserId(rs.getInt("user_id"));
        profile.setAvatar(rs.getString("avatar"));
        profile.setBio(rs.getString("bio"));

        Date dateNaissance = rs.getDate("date_naissance");
        if (dateNaissance != null) {
            profile.setDateNaissance(dateNaissance.toLocalDate());
        }

        profile.setLangue(rs.getString("langue"));
        profile.setNotificationsEmail(rs.getBoolean("notifications_email"));
        profile.setNotificationsSms(rs.getBoolean("notifications_sms"));
        profile.setParametresConfidentialite(rs.getString("parametres_confidentialite"));
        profile.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        profile.setDateModification(rs.getTimestamp("date_modification").toLocalDateTime());

        return profile;
    }
}