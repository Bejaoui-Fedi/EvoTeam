package tn.esprit.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tn.esprit.entities.Event;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExportService {

    public static void exporterEvenements(List<Event> events, String cheminFichier) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Événements");

        // Style d'en-tête
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Créer l'en-tête
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Nom", "Date début", "Date fin", "Participants max", "Prix (DT)", "Lieu", "Description"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // Remplir les données
        int rowNum = 1;
        for (Event e : events) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(e.getEventId());
            row.createCell(1).setCellValue(e.getName());
            row.createCell(2).setCellValue(e.getStartDate());
            row.createCell(3).setCellValue(e.getEndDate());
            row.createCell(4).setCellValue(e.getMaxParticipants());
            row.createCell(5).setCellValue(e.getFee());
            row.createCell(6).setCellValue(e.getLocation());
            row.createCell(7).setCellValue(e.getDescription());
        }

        // Ajuster les colonnes
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Sauvegarder
        try (FileOutputStream fileOut = new FileOutputStream(cheminFichier)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
}