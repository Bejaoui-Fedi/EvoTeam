package tn.esprit.services;

import tn.esprit.entities.Review;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReview {

    private Connection connection = DBConnection.getInstance().getMyConnection();

    // ================== CREATE ==================
    public void ajouter(Review review) {

        String sql = "INSERT INTO review (rating, comment, reviewDate, title, eventId) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setInt(1, review.getRating());
            pst.setString(2, review.getComment());
            pst.setString(3, review.getReviewDate());
            pst.setString(4, review.getTitle());
            pst.setInt(5, review.getEventId());

            pst.executeUpdate();
            System.out.println("Review ajoutée avec succès ✅");

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
                reviews.add(new Review(
                        rs.getInt("reviewId"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getString("reviewDate"),
                        rs.getString("title"),
                        rs.getInt("eventId")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    // ================== UPDATE ==================
    public void update(Review review) {

        String sql = "UPDATE review SET rating=?, comment=?, reviewDate=?, title=?, eventId=? WHERE reviewId=?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setInt(1, review.getRating());
            pst.setString(2, review.getComment());
            pst.setString(3, review.getReviewDate());
            pst.setString(4, review.getTitle());
            pst.setInt(5, review.getEventId());
            pst.setInt(6, review.getReviewId());

            pst.executeUpdate();
            System.out.println("Review modifiée ✅");

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
            System.out.println("Review supprimée ✅");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================== READ BY EVENT ==================
    public List<Review> getByEventId(int eventId) {

        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM review WHERE eventId=?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setInt(1, eventId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                reviews.add(new Review(
                        rs.getInt("reviewId"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getString("reviewDate"),
                        rs.getString("title"),
                        rs.getInt("eventId")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }
}
