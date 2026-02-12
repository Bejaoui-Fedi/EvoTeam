package tn.esprit.services;

import tn.esprit.entities.UserProfile;
import tn.esprit.utils. DBConnection;

import java.sql.*;

public class UserProfileService {

    private Connection cnx;

    public UserProfileService() {
        cnx = DBConnection.getInstance().getConnection();
        System.out.println("✅ UserProfileService initialisé");
    }

    // ✅ CREATE - Créer un profil par défaut pour un utilisateur
    public void createDefaultProfile(int userId) throws SQLException {
        System.out.println("🚀 Tentative de création de profil pour l'utilisateur ID: " + userId);

        // Vérifier si un profil existe déjà
        if (profileExists(userId)) {
            System.out.println("⚠️ Un profil existe déjà pour l'utilisateur ID: " + userId);
            return;
        }

        String sql = "INSERT INTO user_profile (user_id, langue, notifications_email, notifications_sms, parametres_confidentialite) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, "FR");              // Langue par défaut
            ps.setBoolean(3, true);             // Notifications email par défaut
            ps.setBoolean(4, false);            // Notifications SMS par défaut
            ps.setString(5, "PUBLIC");          // Confidentialité par défaut

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅✅✅ Profil utilisateur CRÉÉ avec succès pour l'ID: " + userId);
            } else {
                System.out.println("❌❌❌ Échec de création du profil pour l'ID: " + userId);
            }
        } catch (SQLException e) {
            System.out.println("❌❌❌ ERREUR SQL lors de la création du profil: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // ✅ Vérifier si un profil existe
    public boolean profileExists(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_profile WHERE user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // ✅ READ - Récupérer le profil par user_id
    public UserProfile getByUserId(int userId) throws SQLException {
        System.out.println("🔍 Recherche du profil pour l'utilisateur ID: " + userId);

        String sql = "SELECT * FROM user_profile WHERE user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
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

                System.out.println("✅ Profil TROUVÉ pour l'utilisateur ID: " + userId);
                return profile;
            } else {
                System.out.println("⚠️ Aucun profil trouvé pour l'utilisateur ID: " + userId);
            }
        }
        return null;
    }

    // ✅ UPDATE - Mettre à jour le profil
    public void update(UserProfile profile) throws SQLException {
        String sql = "UPDATE user_profile SET avatar = ?, bio = ?, date_naissance = ?, langue = ?, " +
                "notifications_email = ?, notifications_sms = ?, parametres_confidentialite = ? WHERE user_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, profile.getAvatar());
            ps.setString(2, profile.getBio());
            ps.setDate(3, profile.getDateNaissance() != null ? Date.valueOf(profile.getDateNaissance()) : null);
            ps.setString(4, profile.getLangue());
            ps.setBoolean(5, profile.isNotificationsEmail());
            ps.setBoolean(6, profile.isNotificationsSms());
            ps.setString(7, profile.getParametresConfidentialite());
            ps.setInt(8, profile.getUserId());

            int rowsAffected = ps.executeUpdate();
            System.out.println("✅ Profil mis à jour pour l'utilisateur: " + profile.getUserId() + " (" + rowsAffected + " ligne(s) affectée(s))");
        }
    }

    // ✅ DELETE - Supprimer le profil
    public void deleteByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM user_profile WHERE user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            System.out.println("✅ Profil supprimé pour l'utilisateur: " + userId + " (" + rowsAffected + " ligne(s) affectée(s))");
        }
    }
}