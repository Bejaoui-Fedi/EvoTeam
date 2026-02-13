package tn.esprit.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserProfile {
    private int id;
    private int userId;
    private String avatar;
    private String bio;
    private LocalDate dateNaissance;
    private String langue;
    private boolean notificationsEmail;
    private boolean notificationsSms;
    private String parametresConfidentialite;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Constructeurs
    public UserProfile() {}

    public UserProfile(int userId) {
        this.userId = userId;
        this.langue = "FR";
        this.notificationsEmail = false;
        this.notificationsSms = false;
        this.parametresConfidentialite = "PUBLIC";
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getDateNaissanceFormatted() {
        if (dateNaissance != null) {
            return dateNaissance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return null;
    }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }

    public boolean isNotificationsEmail() { return notificationsEmail; }
    public void setNotificationsEmail(boolean notificationsEmail) { this.notificationsEmail = notificationsEmail; }

    public boolean isNotificationsSms() { return notificationsSms; }
    public void setNotificationsSms(boolean notificationsSms) { this.notificationsSms = notificationsSms; }

    public String getParametresConfidentialite() { return parametresConfidentialite; }
    public void setParametresConfidentialite(String parametresConfidentialite) { this.parametresConfidentialite = parametresConfidentialite; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", userId=" + userId +
                ", langue='" + langue + '\'' +
                ", confidentialite='" + parametresConfidentialite + '\'' +
                '}';
    }
}