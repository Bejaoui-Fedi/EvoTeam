package tn.esprit.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import tn.esprit.entities.Consultation;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PDFService {

    public void generateOrdonnance(Consultation consultation, String patientName, String doctorName)
            throws IOException {
        String fileName = "Ordonnance_" + consultation.getId() + ".pdf";
        String userHome = System.getProperty("user.home");
        String filePath = userHome + "/Desktop/" + fileName;

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Header Bar (Green EvoTeam)
                contentStream.setNonStrokingColor(57 / 255f, 111 / 255f, 91 / 255f); // #396f5b
                contentStream.addRect(0, 750, 612, 50);
                contentStream.fill();

                // Header Title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 22);
                contentStream.setNonStrokingColor(1f, 1f, 1f);
                contentStream.newLineAtOffset(180, 768);
                contentStream.showText("ORDONNANCE MÉDICALE");
                contentStream.endText();

                // Reset color for text
                contentStream.setNonStrokingColor(0f, 0f, 0f);

                // Doctor Info Block
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(50, 710);
                contentStream.showText("Docteur :");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(60, 0);
                contentStream.showText(doctorName);
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(420, 710);
                contentStream.showText("Date :");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(40, 0);
                contentStream
                        .showText(consultation.getDateConsultation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                contentStream.endText();

                // Patient Info Block
                contentStream.setNonStrokingColor(240 / 255f, 240 / 255f, 240 / 255f);
                contentStream.addRect(50, 650, 512, 35);
                contentStream.fill();
                contentStream.setNonStrokingColor(0f, 0f, 0f);

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLineAtOffset(65, 662);
                contentStream.showText("PATIENT : " + patientName.toUpperCase());
                contentStream.endText();

                // Separator Line
                contentStream.setLineWidth(1f);
                contentStream.moveTo(50, 630);
                contentStream.lineTo(562, 630);
                contentStream.stroke();

                // Prescription Title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 13);
                contentStream.newLineAtOffset(50, 600);
                contentStream.showText("PRESCRIPTIONS :");
                contentStream.endText();

                // Prescription Content
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(70, 570);
                contentStream.setLeading(20f);

                String ordonnanceText = consultation.getOrdonnance();
                if (ordonnanceText != null && !ordonnanceText.isEmpty()) {
                    String[] lines = ordonnanceText.split("\n");
                    for (String line : lines) {
                        contentStream.showText("• " + line);
                        contentStream.newLine();
                    }
                } else {
                    contentStream.showText("Aucune prescription spécifiée.");
                }
                contentStream.endText();

                // Signature Line
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(400, 200);
                contentStream.showText("Signature & Cachet");
                contentStream.endText();

                contentStream.setLineWidth(0.5f);
                contentStream.moveTo(400, 195);
                contentStream.lineTo(550, 195);
                contentStream.stroke();

                // Footer
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                contentStream.setNonStrokingColor(100 / 255f, 100 / 255f, 100 / 255f);
                contentStream.newLineAtOffset(50, 50);
                contentStream.showText("Ce document est généré électroniquement par EvoTeam Santé le "
                        + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
                contentStream.endText();
            }

            document.save(filePath);
            System.out.println("PDF generated at: " + filePath);

            try {
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
                }
            } catch (Exception e) {
                System.out.println("Impossible d'ouvrir le PDF automatiquement : " + e.getMessage());
            }
        }
    }
}
