package tn.esprit.entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private int id;
    private LocalDate dateRdv;
    private LocalTime heureRdv;
    private String statut;
    private String motif;
    private String typeRdv;
    private int userId;

    public Appointment() {}

    public Appointment(LocalDate dateRdv, LocalTime heureRdv, String statut, String motif, String typeRdv) {
        this.dateRdv = dateRdv;
        this.heureRdv = heureRdv;
        this.statut = statut;
        this.motif = motif;
        this.typeRdv = typeRdv;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDateRdv() { return dateRdv; }
    public void setDateRdv(LocalDate dateRdv) { this.dateRdv = dateRdv; }

    public LocalTime getHeureRdv() { return heureRdv; }
    public void setHeureRdv(LocalTime heureRdv) { this.heureRdv = heureRdv; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public String getTypeRdv() { return typeRdv; }
    public void setTypeRdv(String typeRdv) { this.typeRdv = typeRdv; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String toString() {
        return String.format("RDV #%d - %s %s - %s", id, dateRdv, heureRdv, motif);
    }
}