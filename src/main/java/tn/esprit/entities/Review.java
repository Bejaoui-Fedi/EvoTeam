package tn.esprit.entities;

import java.util.Objects;

public class Review {
    private int reviewId;
    private int rating;
    private String comment;
    private String reviewDate; // String pour rester comme ton style (sinon LocalDate)
    private String title;
    private int eventId; // FK

    public Review(int reviewId, int rating, String comment, String reviewDate, String title, int eventId) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.title = title;
        this.eventId = eventId;
    }

    // pour ajout
    public Review(int rating, String comment, String reviewDate, String title, int eventId) {
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.title = title;
        this.eventId = eventId;
    }

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
}
