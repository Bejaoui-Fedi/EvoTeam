package tn.esprit.entities;

import java.time.LocalDate;

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

    public UserProfile() {}

    public UserProfile(int userId, String avatar, String bio, LocalDate dateNaissance,
                       String langue, boolean notificationsEmail, boolean notificationsSms,
                       String parametresConfidentialite) {
        this.userId = userId;
        this.avatar = avatar;
        this.bio = bio;
        this.dateNaissance = dateNaissance;
        this.langue = langue;
        this.notificationsEmail = notificationsEmail;
        this.notificationsSms = notificationsSms;
        this.parametresConfidentialite = parametresConfidentialite;
    }

    // Getters / Setters
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
    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }
    public boolean isNotificationsEmail() { return notificationsEmail; }
    public void setNotificationsEmail(boolean notificationsEmail) { this.notificationsEmail = notificationsEmail; }
    public boolean isNotificationsSms() { return notificationsSms; }
    public void setNotificationsSms(boolean notificationsSms) { this.notificationsSms = notificationsSms; }
    public String getParametresConfidentialite() { return parametresConfidentialite; }
    public void setParametresConfidentialite(String parametresConfidentialite) { this.parametresConfidentialite = parametresConfidentialite; }

    @Override
    public String toString() {
        return "UserProfile{id=" + id + ", userId=" + userId + ", bio='" + bio + "'}";
    }
}