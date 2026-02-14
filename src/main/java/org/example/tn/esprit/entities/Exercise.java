package org.example.tn.esprit.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Exercise {

    // ====== Fields (match DB columns) ======
    private Integer idExercise;
    private Integer objectiveId;

    private String title;
    private String description;
    private String type;
    private int durationMinutes;
    private String difficulty;
    private String mediaUrl;
    private String steps; // Instruction steps

    private int isPublished;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int userId;

    // ====== Constructors ======

    public Exercise() {
    }

    public Exercise(Integer objectiveId,
            String title,
            String description,
            String type,
            int durationMinutes,
            String difficulty,
            String mediaUrl,
            String steps,
            int isPublished) {
        this.objectiveId = objectiveId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.difficulty = difficulty;
        this.mediaUrl = mediaUrl;
        this.steps = steps;
        this.isPublished = isPublished;
    }

    public Exercise(Integer idExercise,
            Integer objectiveId,
            String title,
            String description,
            String type,
            int durationMinutes,
            String difficulty,
            String mediaUrl,
            String steps,
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
        this.steps = steps;
        this.isPublished = isPublished;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Keep legacy constructor for compatibility with seeders if needed
    public Exercise(int userId, Integer objectiveId, String title, String description, String type,
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

    // ====== Getters & Setters ======

    public Integer getIdExercise() {
        return idExercise;
    }

    public void setIdExercise(Integer idExercise) {
        this.idExercise = idExercise;
    }

    public Integer getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(Integer objectiveId) {
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

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public int getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(int isPublished) {
        this.isPublished = isPublished;
    }

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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
        if (this == o)
            return true;
        if (!(o instanceof Exercise))
            return false;
        Exercise exercise = (Exercise) o;
        return Objects.equals(idExercise, exercise.idExercise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idExercise);
    }
}
