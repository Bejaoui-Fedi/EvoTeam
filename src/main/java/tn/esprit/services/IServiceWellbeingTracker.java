package tn.esprit.services;

import tn.esprit.entities.WellbeingTracker;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IServiceWellbeingTracker {
    void ajouter(WellbeingTracker tracker) throws SQLException;
    void modifier(WellbeingTracker tracker) throws SQLException;
    void supprimer(int id) throws SQLException;
    List<WellbeingTracker> afficher() throws SQLException;
    List<WellbeingTracker> getByUserId(int userId) throws SQLException;
    List<WellbeingTracker> getByUserIdAndDateRange(int userId, Date startDate, Date endDate) throws SQLException;
    WellbeingTracker getTodayEntry(int userId) throws SQLException;
    Map<String, Double> getStatistics(int userId) throws SQLException;
    double getAverageMood(int userId) throws SQLException;
}