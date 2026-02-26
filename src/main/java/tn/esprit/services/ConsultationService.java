package tn.esprit.services;

import tn.esprit.entities.Consultation;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationService implements IUService<Consultation> {

    private Connection connection;

    public ConsultationService() {
        connection = DBConnection.getInstance().getConnection();
    }

    @Override
    public void add(Consultation c) throws SQLException {
        String sql = "INSERT INTO consultation(appointment_id, date_consultation, diagnostic, observation, traitement, ordonnance, duree, statut_consultation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, c.getAppointmentId());
        ps.setDate(2, c.getDateConsultation() != null ? Date.valueOf(c.getDateConsultation()) : null);
        ps.setString(3, c.getDiagnostic());
        ps.setString(4, c.getObservation());
        ps.setString(5, c.getTraitement());
        ps.setString(6, c.getOrdonnance());
        ps.setInt(7, c.getDuree());
        ps.setString(8, c.getStatutConsultation());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) c.setId(rs.getInt(1));
    }

    @Override
    public List<Consultation> getAll() throws SQLException {
        List<Consultation> list = new ArrayList<>();
        String sql = "SELECT * FROM consultation ORDER BY id DESC";

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Consultation c = new Consultation();
            c.setId(rs.getInt("id"));
            c.setAppointmentId(rs.getInt("appointment_id"));

            Date date = rs.getDate("date_consultation");
            if (date != null) c.setDateConsultation(date.toLocalDate());

            c.setDiagnostic(rs.getString("diagnostic"));
            c.setObservation(rs.getString("observation"));
            c.setTraitement(rs.getString("traitement"));
            c.setOrdonnance(rs.getString("ordonnance"));
            c.setDuree(rs.getInt("duree"));
            c.setStatutConsultation(rs.getString("statut_consultation"));

            list.add(c);
        }

        return list;
    }

    @Override
    public void update(Consultation c) throws SQLException {
        String sql = "UPDATE consultation SET diagnostic=?, observation=?, traitement=?, ordonnance=?, duree=?, statut_consultation=? WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, c.getDiagnostic());
        ps.setString(2, c.getObservation());
        ps.setString(3, c.getTraitement());
        ps.setString(4, c.getOrdonnance());
        ps.setInt(5, c.getDuree());
        ps.setString(6, c.getStatutConsultation());
        ps.setInt(7, c.getId());

        ps.executeUpdate();
    }

    @Override
    public void delete(Consultation c) throws SQLException {
        String sql = "DELETE FROM consultation WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, c.getId());
        ps.executeUpdate();
    }

    public List<Consultation> getByAppointmentId(int appointmentId) throws SQLException {
        List<Consultation> list = new ArrayList<>();
        String sql = "SELECT * FROM consultation WHERE appointment_id = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, appointmentId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Consultation c = new Consultation();
            c.setId(rs.getInt("id"));
            c.setAppointmentId(rs.getInt("appointment_id"));

            Date date = rs.getDate("date_consultation");
            if (date != null) c.setDateConsultation(date.toLocalDate());

            c.setDiagnostic(rs.getString("diagnostic"));
            c.setObservation(rs.getString("observation"));
            c.setTraitement(rs.getString("traitement"));
            c.setOrdonnance(rs.getString("ordonnance"));
            c.setDuree(rs.getInt("duree"));
            c.setStatutConsultation(rs.getString("statut_consultation"));

            list.add(c);
        }

        return list;
    }
}