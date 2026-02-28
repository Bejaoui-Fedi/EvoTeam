package tn.esprit.entities;

import java.util.Objects;

public class Event {

    private int eventId;
    private String name;
    private String startDate;
    private String endDate;
    private int maxParticipants;
    private String description;
    private int fee;
    private String location; // nouvel attribut saisi par l'utilisateur

    private double latitude;
    private double longitude;


    // Constructeur avec ID
    public Event(int eventId, String name, String startDate, String endDate, int maxParticipants,
                 String description, int fee, String location) {
        this.eventId = eventId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxParticipants = maxParticipants;
        this.description = description;
        this.fee = fee;
        this.location = location;
    }

    // Constructeur sans ID (pour l'ajout)
    public Event(String name, String startDate, String endDate, int maxParticipants,
                 String description, int fee, String location) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxParticipants = maxParticipants;
        this.description = description;
        this.fee = fee;
        this.location = location;
    }

    // Getters et Setters
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }



    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getFee() { return fee; }
    public void setFee(int fee) { this.fee = fee; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", maxParticipants=" + maxParticipants +
                ", description='" + description + '\'' +
                ", fee=" + fee +
                ", location='" + location + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return eventId == event.eventId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
}
