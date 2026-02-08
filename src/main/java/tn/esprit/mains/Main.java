package tn.esprit.mains;

import tn.esprit.entities.*;
import tn.esprit.services.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {

        AppointmentService appointmentService = new AppointmentService();
        ConsultationService consultationService = new ConsultationService();

        try {
            // ===== CREATE Appointment =====
            Appointment a = new Appointment(
                    LocalDate.now(),
                    LocalTime.of(10, 30),
                    "CONFIRME",
                    "Consultation psy",
                    "EN_LIGNE"
            );
            appointmentService.add(a);

            // ===== CREATE Consultation =====
            Consultation c = new Consultation(
                    a.getId(),
                    LocalDate.now(),
                    "Stress",
                    "Patient anxieux",
                    "Relaxation",
                    "Médicament X",
                    45,
                    "TERMINEE"
            );
            consultationService.add(c);

            // ===== READ =====
            System.out.println("Liste RDV : " + appointmentService.getAll());
            System.out.println("Liste Consultations : " + consultationService.getAll());

            // ===== UPDATE =====
            a.setStatut("ANNULE");
            appointmentService.update(a);

            c.setDiagnostic("Dépression légère");
            consultationService.update(c);

            // ===== DELETE Consultation =====
            System.out.println("\n--- Suppression consultation ---");
            consultationService.delete(c);

            // ===== DELETE Appointment =====
            System.out.println("\n--- Suppression rendez-vous ---");
            appointmentService.delete(a);

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
