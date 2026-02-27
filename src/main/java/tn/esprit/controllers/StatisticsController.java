package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Appointment;
import tn.esprit.services.AppointmentService;

import java.sql.SQLException;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsController {

    @FXML
    private BarChart<String, Number> appointmentsBarChart;
    @FXML
    private PieChart statusPieChart;
    @FXML
    private Label totalLabel;
    @FXML
    private Label todayLabel;
    @FXML
    private Label pendingLabel;
    @FXML
    private Label confirmedLabel;

    private AppointmentService service = new AppointmentService();

    @FXML
    public void initialize() {
        loadStatistics();
    }

    @FXML
    private void handleBack() {
        if (totalLabel.getScene() != null && totalLabel.getScene().getWindow() != null) {
            totalLabel.getScene().getWindow().hide();
        }
    }

    private void loadStatistics() {
        try {
            List<Appointment> allAppointments = service.getAll();
            java.time.LocalDate today = java.time.LocalDate.now();

            int total = allAppointments.size();
            int todayCount = 0;
            int pending = 0;
            int confirmed = 0;

            // 1. Data for BarChart (Appointments per Month)
            Map<Month, Integer> appointmentsPerMonth = new HashMap<>();
            // 2. Data for PieChart (Status Distribution)
            Map<String, Integer> statusCounts = new HashMap<>();

            for (Appointment a : allAppointments) {
                // KPI Calculations
                if (a.getDateRdv().equals(today))
                    todayCount++;
                if ("EN ATTENTE".equalsIgnoreCase(a.getStatut()))
                    pending++;
                if ("CONFIRMÉ".equalsIgnoreCase(a.getStatut()))
                    confirmed++;

                // Month stats
                Month month = a.getDateRdv().getMonth();
                appointmentsPerMonth.put(month, appointmentsPerMonth.getOrDefault(month, 0) + 1);

                // Status stats
                String status = a.getStatut();
                statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
            }

            // Update KPI Labels
            totalLabel.setText(String.valueOf(total));
            todayLabel.setText(String.valueOf(todayCount));
            pendingLabel.setText(String.valueOf(pending));
            confirmedLabel.setText(String.valueOf(confirmed));

            // Populate BarChart (Limited to last 12 months or just all months)
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Rendez-vous par mois");
            for (Month m : Month.values()) {
                int count = appointmentsPerMonth.getOrDefault(m, 0);
                if (count > 0) {
                    series.getData().add(new XYChart.Data<>(m.name(), count));
                }
            }
            appointmentsBarChart.getData().clear();
            appointmentsBarChart.getData().add(series);

            // Populate PieChart
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
            statusPieChart.setData(pieChartData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
