package tn.esprit.entities;

import java.time.LocalDate;

public class Consultation {
    private int id;
    private int appointmentId;
    private LocalDate dateConsultation;
    private String diagnostic;
    private String observation;
    private String traitement;
    private String ordonnance;
    private int duree;
    private String statutConsultation;

    public Consultation() {}

    public Consultation(int appointmentId, LocalDate dateConsultation, String diagnostic, String observation,
                        String traitement, String ordonnance, int duree, String statutConsultation) {
        this.appointmentId = appointmentId;
        this.dateConsultation = dateConsultation;
        this.diagnostic = diagnostic;
        this.observation = observation;
        this.traitement = traitement;
        this.ordonnance = ordonnance;
        this.duree = duree;
        this.statutConsultation = statutConsultation;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public LocalDate getDateConsultation() { return dateConsultation; }
    public void setDateConsultation(LocalDate dateConsultation) { this.dateConsultation = dateConsultation; }

    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }

    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }

    public String getTraitement() { return traitement; }
    public void setTraitement(String traitement) { this.traitement = traitement; }

    public String getOrdonnance() { return ordonnance; }
    public void setOrdonnance(String ordonnance) { this.ordonnance = ordonnance; }

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    public String getStatutConsultation() { return statutConsultation; }
    public void setStatutConsultation(String statutConsultation) { this.statutConsultation = statutConsultation; }

    @Override
    public String toString() {
        return "Consultation #" + id + " - RDV #" + appointmentId + " - " + diagnostic;
    }
}