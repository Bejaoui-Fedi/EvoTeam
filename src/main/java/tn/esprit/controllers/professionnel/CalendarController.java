package tn.esprit.controllers.professionnel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import tn.esprit.entities.Appointment;
import tn.esprit.services.AppointmentService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class CalendarController {

    @FXML
    private Label monthYearLabel;
    @FXML
    private GridPane calendarGrid;

    private AppointmentService service = new AppointmentService();
    private tn.esprit.services.AIService aiService = new tn.esprit.services.AIService();
    private YearMonth currentYearMonth;
    private List<Appointment> allAppointments;

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        loadAppointments();
        drawCalendar();
    }

    private void loadAppointments() {
        try {
            allAppointments = service.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void drawCalendar() {
        calendarGrid.getChildren().clear();
        monthYearLabel.setText(currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH).toUpperCase()
                + " " + currentYearMonth.getYear());

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue(); // 1 (Mon) to 7 (Sun)
        int offset = (dayOfWeekValue == 7) ? 0 : dayOfWeekValue;

        LocalDate firstDateToShow = firstOfMonth.minusDays(offset);

        // Group appointments by date
        Map<LocalDate, List<Appointment>> appointmentsByDate = allAppointments.stream()
                .collect(Collectors.groupingBy(Appointment::getDateRdv));

        int row = 0;
        int col = 0;

        // Always show 6 rows (42 days) for a consistent grid look
        for (int i = 0; i < 42; i++) {
            LocalDate date = firstDateToShow.plusDays(i);
            boolean isCurrentMonth = YearMonth.from(date).equals(currentYearMonth);

            VBox cell = createDayCell(date, isCurrentMonth, appointmentsByDate.get(date));
            calendarGrid.add(cell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createDayCell(LocalDate date, boolean isCurrentMonth, List<Appointment> dayAppointments) {
        VBox vBox = new VBox(5);
        vBox.setPrefSize(155, 145);
        vBox.setMinSize(155, 145);
        vBox.setPadding(new Insets(10));

        String bgStyle = isCurrentMonth ? "rgba(255, 255, 255, 0.95)" : "rgba(240, 240, 240, 0.4)";
        String borderStyle = isCurrentMonth ? "#d1dbd6" : "#e0e0e0";
        vBox.setStyle("-fx-background-color: " + bgStyle + "; -fx-background-radius: 12; " +
                "-fx-border-color: " + borderStyle + "; -fx-border-width: 1; -fx-border-radius: 12;");
        vBox.setAlignment(Pos.TOP_LEFT);

        Label dayLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        dayLabel.setTextFill(isCurrentMonth ? Color.web("#2c3e50") : Color.web("#bdc3c7"));
        vBox.getChildren().add(dayLabel);

        if (isCurrentMonth && dayAppointments != null && !dayAppointments.isEmpty()) {
            VBox appointmentsBox = new VBox(4);
            appointmentsBox.setStyle("-fx-background-color: transparent;");

            for (Appointment app : dayAppointments) {
                String labelText = app.getHeureRdv() + " " + app.getMotif();
                if (aiService.isUrgent(app.getMotif())) {
                    labelText = "🚨 " + labelText;
                }
                Label appLabel = new Label(labelText);
                appLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
                appLabel.setPadding(new Insets(3, 10, 3, 10));
                appLabel.setMaxWidth(130);
                appLabel.setEllipsisString("...");

                String baseStyle = "-fx-background-radius: 50; ";
                String style = baseStyle;
                switch (app.getStatut().toLowerCase()) {
                    case "confirmé":
                        style += "-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32; -fx-border-color: #c8e6c9; -fx-border-width: 1;";
                        break;
                    case "annulé":
                        style += "-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-border-color: #ffcdd2; -fx-border-width: 1;";
                        break;
                    case "terminé":
                        style += "-fx-background-color: #e3f2fd; -fx-text-fill: #1565c0; -fx-border-color: #bbdefb; -fx-border-width: 1;";
                        break;
                    default:
                        style += "-fx-background-color: #fff8e1; -fx-text-fill: #f57f17; -fx-border-color: #ffecb3; -fx-border-width: 1;";
                        break;
                }

                // Extra styling for urgent appointments
                if (aiService.isUrgent(app.getMotif())) {
                    style += "-fx-border-color: #ff0000; -fx-border-width: 2; -fx-border-radius: 50;";
                }

                appLabel.setStyle(style);
                appLabel.setCursor(javafx.scene.Cursor.HAND);

                // Show details on click
                appLabel.setOnMouseClicked(e -> showAppointmentDetails(app));

                appointmentsBox.getChildren().add(appLabel);
            }

            ScrollPane scrollPane = new ScrollPane(appointmentsBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");
            scrollPane.setPrefHeight(100);

            vBox.getChildren().add(scrollPane);
        }

        // Highlight today
        if (date.equals(LocalDate.now())) {
            vBox.setStyle(vBox.getStyle()
                    + "-fx-border-width: 2.5; -fx-border-color: #396f5b; -fx-background-color: #f7fbf8;");
        }

        // Simpler hover: use a scale transition or just a drop shadow
        vBox.setOnMouseEntered(e -> vBox.setEffect(new javafx.scene.effect.DropShadow(10, Color.color(0, 0, 0, 0.1))));
        vBox.setOnMouseExited(e -> vBox.setEffect(null));

        return vBox;
    }

    private void showAppointmentDetails(Appointment app) {
        boolean isUrgent = aiService.isUrgent(app.getMotif());
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                isUrgent ? javafx.scene.control.Alert.AlertType.WARNING
                        : javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du Rendez-vous");
        alert.setHeaderText("Fiche du Rendez-vous #" + app.getId() + (isUrgent ? " [🚨 URGENT]" : ""));

        StringBuilder details = new StringBuilder();
        if (isUrgent) {
            details.append("⚠️ ATTENTION : L'IA a détecté une situation d'URGENCE !\n");
            details.append("--------------------------------------------------\n\n");
        }

        details.append(String.format(
                "📅 Date : %s\n" +
                        "⏰ Heure : %s\n" +
                        "👤 Patient ID : %d\n" +
                        "📝 Motif : %s\n" +
                        "🏥 Type : %s\n" +
                        "📊 Statut : %s",
                app.getDateRdv(),
                app.getHeureRdv(),
                app.getUserId(),
                app.getMotif(),
                app.getTypeRdv(),
                app.getStatut()));

        alert.setContentText(details.toString());

        // Custom styling for the alert (optional but nice)
        javafx.scene.layout.Region region = (javafx.scene.layout.Region) alert.getDialogPane().lookup(".content.label");
        if (region != null)
            region.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14;");

        alert.showAndWait();
    }

    @FXML
    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        drawCalendar();
    }

    @FXML
    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        drawCalendar();
    }

    @FXML
    private void backToList() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/professionnel/pro_gestion_rdv.fxml"));
            Stage stage = (Stage) calendarGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Rendez-vous - EvoTeam");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
