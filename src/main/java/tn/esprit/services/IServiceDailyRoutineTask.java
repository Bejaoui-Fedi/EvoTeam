package tn.esprit.services;

import tn.esprit.entities.DailyRoutineTask;

import java.sql.SQLException;
import java.util.List;

public interface IServiceDailyRoutineTask {
    void ajouter(DailyRoutineTask task) throws SQLException;
    void modifier(DailyRoutineTask task) throws SQLException;
    void supprimer(int id) throws SQLException;
    List<DailyRoutineTask> afficher() throws SQLException;
    List<DailyRoutineTask> getTasksByUser(int userId) throws SQLException;
    void markTaskCompleted(int taskId) throws SQLException;
    List<DailyRoutineTask> getCompletedTasks(int userId) throws SQLException;
    List<DailyRoutineTask> getPendingTasks(int userId) throws SQLException;
}