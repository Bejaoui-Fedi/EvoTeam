package org.example.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Objective {

    private Long idObjective;
    private String title;
    private String description;
    private String icon;
    private String color;              //  #70C070
    private String level;              // level  debutant/moyen/avance/global
    private int isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int userId;





    public Objective(Long idObjective, String title, String description, String icon, String color,
                     String level, int isPublished) {
        this.idObjective = idObjective;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.level = level;
        this.isPublished = isPublished;

    }

    public Objective( String title, String description, String icon, String color, String level, int isPublished) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.level = level;
        this.isPublished = isPublished;
    }

    public Objective() {

    }

    public Objective(int userId, String title, String description, String icon, String color, String level, int isPublished) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.level = level;
        this.isPublished = isPublished;
    }

    public Objective(Long idObjective, int userId, String title, String description, String icon, String color,
                     String level, int isPublished) {
        this.idObjective = idObjective;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.level = level;
        this.isPublished = isPublished;
    }
    // ---------------- Getters/Setters ----------------

    public Long getIdObjective() {
        return idObjective;
    }

    public void setIdObjective(Long idObjective) {
        this.idObjective = idObjective;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int isPublished() {
        return isPublished;
    }

    public void setPublished(int published) {
        isPublished = published;
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

    // ---------------- equals/hashCode/toString ----------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Objective)) return false;
        Objective objective = (Objective) o;
        return Objects.equals(idObjective, objective.idObjective);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idObjective);
    }

    @Override
    public String toString() {
        return "Objective{" +
                "idObjective=" + idObjective +
                ", title='" + title + '\'' +
                ", level='" + level + '\'' +
                ", isPublished=" + isPublished +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}