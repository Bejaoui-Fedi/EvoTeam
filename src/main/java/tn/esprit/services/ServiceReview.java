package tn.esprit.services;

import tn.esprit.entities.Review;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReview {

    private Connection connection = DBConnection.getInstance().getConnection();

    // ================== CREATE (avec userId) ==================
    public void ajouter(Review review) {
        String sql = "INSERT INTO review (rating, comment, reviewDate, title, eventId, userId) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, review.getRating());
            pst.setString(2, review.getComment());
            pst.setString(3, review.getReviewDate());
            pst.setString(4, review.getTitle());
            pst.setInt(5, review.getEventId());
            pst.setInt(6, review.getUserId());
            pst.executeUpdate();
            System.out.println("Review ajoutee avec succes");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== READ ALL ==================
    public List<Review> getAll() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM review";
        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                reviews.add(mapReview(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // ================== READ BY EVENT ==================
    public List<Review> getByEventId(int eventId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM review WHERE eventId=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, eventId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                reviews.add(mapReview(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // ================== READ BY EVENT + USER (NOUVEAU) ==================
    // Retourne les reviews d'un event specifique faites par un user specifique
    public List<Review> getByEventIdAndUserId(int eventId, int userId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM review WHERE eventId=? AND userId=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, eventId);
            pst.setInt(2, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                reviews.add(mapReview(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // ================== VERIFIER SI UNE REVIEW EXISTE DEJA (NOUVEAU) ==================
    // Retourne true si l'utilisateur a deja une review pour cet evenement
    public boolean existsReviewForUserAndEvent(int userId, int eventId) {
        String sql = "SELECT COUNT(*) FROM review WHERE userId=? AND eventId=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, userId);
            pst.setInt(2, eventId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // true si count > 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================== UPDATE ==================
    public void update(Review review) {
        String sql = "UPDATE review SET rating=?, comment=?, reviewDate=?, title=?, eventId=?, userId=? WHERE reviewId=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, review.getRating());
            pst.setString(2, review.getComment());
            pst.setString(3, review.getReviewDate());
            pst.setString(4, review.getTitle());
            pst.setInt(5, review.getEventId());
            pst.setInt(6, review.getUserId());
            pst.setInt(7, review.getReviewId());
            pst.executeUpdate();
            System.out.println("Review modifiee");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== DELETE ==================
    public void delete(int reviewId) {
        String sql = "DELETE FROM review WHERE reviewId=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, reviewId);
            pst.executeUpdate();
            System.out.println("Review supprimee");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== HELPER : mapper un ResultSet en Review ==================
    private Review mapReview(ResultSet rs) throws SQLException {
        return new Review(
                rs.getInt("reviewId"),
                rs.getInt("rating"),
                rs.getString("comment"),
                rs.getString("reviewDate"),
                rs.getString("title"),
                rs.getInt("eventId"),
                rs.getInt("userId")  // NOUVEAU
        );
    }
}
