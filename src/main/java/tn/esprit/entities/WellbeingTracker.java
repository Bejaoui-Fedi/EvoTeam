package tn.esprit.entities;

import java.util.Objects;

public class WellbeingTracker {
    private int id;
    private int userId;          // FK vers User
    private int routineTaskId;   // FK vers DailyRoutineTask
    private String date;         // YYYY-MM-DD
    private int mood;           // 1-5
    private int stress;         // 1-5
    private int energy;         // 1-5
    private double sleepHours;  // 0-24
    private String note;        // optionnel
    private String createdAt;   // timestamp

    // Constructeur avec ID (pour getAll, update)
    public WellbeingTracker(int id, int userId, int routineTaskId, String date,
                            int mood, int stress, int energy, double sleepHours,
                            String note, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.routineTaskId = routineTaskId;
        this.date = date;
        this.mood = mood;
        this.stress = stress;
        this.energy = energy;
        this.sleepHours = sleepHours;
        this.note = note;
        this.createdAt = createdAt;
    }

    // Constructeur sans ID (pour ajouter)
    public WellbeingTracker(int userId, int routineTaskId, String date,
                            int mood, int stress, int energy, double sleepHours,
                            String note) {
        this.userId = userId;
        this.routineTaskId = routineTaskId;
        this.date = date;
        this.mood = mood;
        this.stress = stress;
        this.energy = energy;
        this.sleepHours = sleepHours;
        this.note = note;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRoutineTaskId() { return routineTaskId; }
    public void setRoutineTaskId(int routineTaskId) { this.routineTaskId = routineTaskId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getMood() { return mood; }
    public void setMood(int mood) { this.mood = mood; }

    public int getStress() { return stress; }
    public void setStress(int stress) { this.stress = stress; }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }

    public double getSleepHours() { return sleepHours; }
    public void setSleepHours(double sleepHours) { this.sleepHours = sleepHours; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "WellbeingTracker{" +
                "id=" + id +
                ", userId=" + userId +
                ", routineTaskId=" + routineTaskId +
                ", date='" + date + '\'' +
                ", mood=" + mood +
                ", stress=" + stress +
                ", energy=" + energy +
                ", sleepHours=" + sleepHours +
                ", note='" + note + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WellbeingTracker)) return false;
        WellbeingTracker that = (WellbeingTracker) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}