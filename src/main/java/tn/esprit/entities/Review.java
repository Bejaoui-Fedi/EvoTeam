package tn.esprit.entities;

import java.util.Objects;

public class Review {
    private int reviewId;
    private int rating;
    private String comment;
    private String reviewDate;
    private String title;
    private int eventId;   // FK vers Event
    private int userId;    // FK vers User (NOUVEAU)

    // Constructeur complet (avec userId) -- pour lire depuis la BDD
    public Review(int reviewId, int rating, String comment, String reviewDate, String title, int eventId, int userId) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.title = title;
        this.eventId = eventId;
        this.userId = userId;
    }

    // Constructeur pour ajout (sans reviewId, avec userId)
    public Review(int rating, String comment, String reviewDate, String title, int eventId, int userId) {
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.title = title;
        this.eventId = eventId;
        this.userId = userId;
    }

    // Ancien constructeur (sans userId) -- pour compatibilite avec le code admin existant
    public Review(int reviewId, int rating, String comment, String reviewDate, String title, int eventId) {
        this(reviewId, rating, comment, reviewDate, title, eventId, 0);
    }

    // Ancien constructeur ajout (sans userId)
    public Review(int rating, String comment, String reviewDate, String title, int eventId) {
        this(rating, comment, reviewDate, title, eventId, 0);
    }

    // ============ Getters / Setters ============
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getReviewDate() { return reviewDate; }
    public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        Review review = (Review) o;
        return reviewId == review.reviewId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId);
    }

    @Override
    public String toString() {
        return "Review{reviewId=" + reviewId + ", rating=" + rating + ", title='" + title + "', eventId=" + eventId + ", userId=" + userId + "}";
    }
}
