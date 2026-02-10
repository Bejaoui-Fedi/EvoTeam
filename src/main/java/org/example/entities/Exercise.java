package org.example.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Exercise {


    // ====== Fields (match DB columns) ======
    private int idExercise;
    private int objectiveId;

    private String title;
    private String description;
    private String type;
    private int durationMinutes;
    private String difficulty;
    private String mediaUrl;

    private int isPublished;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int userId;

    // ====== Constructors ======

    /**
     * Empty constructor
     */
    public Exercise() { }

    /**
     * Constructor for INSERT (without id, createdAt, updatedAt - handled by DB or service).
     */
    public Exercise(int objectiveId,
                    String title,
                    String description,
                    String type,
                    int durationMinutes,
                    String difficulty,
                    String mediaUrl,
                    int isPublished) {
        this.objectiveId = objectiveId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.difficulty = difficulty;
        this.mediaUrl = mediaUrl;
        this.isPublished = isPublished;
    }

    /**
     * Constructor for UPDATE/READ (with id).
     */
    public Exercise(int idExercise,
                    int objectiveId,
                    String title,
                    String description,
                    String type,
                    int durationMinutes,
                    String difficulty,
                    String mediaUrl,
                    int isPublished,
                    LocalDateTime createdAt,
                    LocalDateTime updatedAt) {
        this.idExercise = idExercise;
        this.objectiveId = objectiveId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.difficulty = difficulty;
        this.mediaUrl = mediaUrl;
        this.isPublished = isPublished;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Exercise(int userId, int objectiveId, String title, String description, String type,
                    int durationMinutes, String difficulty, String mediaUrl, int isPublished) {
        this.userId = userId;
        this.objectiveId = objectiveId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.difficulty = difficulty;
        this.mediaUrl = mediaUrl;
        this.isPublished = isPublished;
    }

    public Exercise(int idExercise, int userId, int objectiveId, String title, String description, String type,
                    int durationMinutes, String difficulty, String mediaUrl, int isPublished,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.idExercise = idExercise;
        this.userId = userId;
        this.objectiveId = objectiveId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.difficulty = difficulty;
        this.mediaUrl = mediaUrl;
        this.isPublished = isPublished;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ====== Getters & Setters ======

    public int getIdExercise() {
        return idExercise;
    }

    public void setIdExercise(int idExercise) {
        this.idExercise = idExercise;
    }

    public int getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(int objectiveId) {
        this.objectiveId = objectiveId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public int getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(int isPublished) {
        this.isPublished = isPublished;
    }

    // Helpers si tu veux manipuler en boolean côté Java
    public boolean isPublished() {
        return isPublished == 1;
    }

    public void setPublished(boolean published) {
        this.isPublished = published ? 1 : 0;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }



    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    // ====== Utility methods ======

    @Override
    public String toString() {
        return "Exercise{" +
                "idExercise=" + idExercise +
                ", objectiveId=" + objectiveId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", durationMinutes=" + durationMinutes +
                ", difficulty='" + difficulty + '\'' +
                ", isPublished=" + isPublished +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exercise)) return false;
        Exercise exercise = (Exercise) o;
        return idExercise == exercise.idExercise;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idExercise);
    }
}