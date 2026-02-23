package tn.esprit.entities;

import java.util.Objects;

public class DailyRoutineTask {
    private int id;
    private int userId;              // FK vers User
    private String title;           // ex: "Méditation", "Sport"
    private boolean isCompleted;    // true/false
    private String completedAt;     // timestamp (nullable)
    private String createdAt;       // timestamp

    // REMOVED: wellbeingTrackerId - it doesn't exist in your database!

    // Constructeur avec ID
    public DailyRoutineTask(int id, int userId, String title,
                            boolean isCompleted, String completedAt, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.isCompleted = isCompleted;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
    }

    // Constructeur sans ID (pour ajouter)
    public DailyRoutineTask(int userId, String title) {
        this.userId = userId;
        this.title = title;
        this.isCompleted = false;    // par défaut non complété
        this.completedAt = null;     // pas encore complété
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "DailyRoutineTask{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", isCompleted=" + isCompleted +
                ", completedAt='" + completedAt + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyRoutineTask)) return false;
        DailyRoutineTask that = (DailyRoutineTask) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}