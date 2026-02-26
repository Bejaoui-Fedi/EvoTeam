package tn.esprit.services;

import tn.esprit.entities.Appointment;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService implements IUService<Appointment> {

    private Connection connection;

    public AppointmentService() {
        connection = DBConnection.getInstance().getConnection();
    }

    @Override
    public void add(Appointment a) throws SQLException {
        String sql = "INSERT INTO appointment(date_rdv, heure_rdv, statut, motif, type_rdv, user_id) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setDate(1, Date.valueOf(a.getDateRdv()));
        ps.setTime(2, Time.valueOf(a.getHeureRdv()));
        ps.setString(3, a.getStatut());
        ps.setString(4, a.getMotif());
        ps.setString(5, a.getTypeRdv());
        ps.setInt(6, a.getUserId());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) a.setId(rs.getInt(1));
    }

    @Override
    public List<Appointment> getAll() throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointment ORDER BY date_rdv DESC, heure_rdv DESC";

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Appointment a = new Appointment();
            a.setId(rs.getInt("id"));
            a.setDateRdv(rs.getDate("date_rdv").toLocalDate());
            a.setHeureRdv(rs.getTime("heure_rdv").toLocalTime());
            a.setStatut(rs.getString("statut"));
            a.setMotif(rs.getString("motif"));
            a.setTypeRdv(rs.getString("type_rdv"));
            a.setUserId(rs.getInt("user_id"));

            list.add(a);
        }
        return list;
    }

    @Override
    public void update(Appointment a) throws SQLException {
        String sql = "UPDATE appointment SET date_rdv=?, heure_rdv=?, statut=?, motif=?, type_rdv=?, user_id=? WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setDate(1, Date.valueOf(a.getDateRdv()));
        ps.setTime(2, Time.valueOf(a.getHeureRdv()));
        ps.setString(3, a.getStatut());
        ps.setString(4, a.getMotif());
        ps.setString(5, a.getTypeRdv());
        ps.setInt(6, a.getUserId());
        ps.setInt(7, a.getId());

        ps.executeUpdate();
    }

    @Override
    public void delete(Appointment a) throws SQLException {
        String sql = "DELETE FROM appointment WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, a.getId());
        ps.executeUpdate();
    }

    public List<Appointment> getByUserId(int userId) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointment WHERE user_id = ? ORDER BY date_rdv DESC, heure_rdv DESC";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Appointment a = new Appointment();
            a.setId(rs.getInt("id"));
            a.setDateRdv(rs.getDate("date_rdv").toLocalDate());
            a.setHeureRdv(rs.getTime("heure_rdv").toLocalTime());
            a.setStatut(rs.getString("statut"));
            a.setMotif(rs.getString("motif"));
            a.setTypeRdv(rs.getString("type_rdv"));
            a.setUserId(rs.getInt("user_id"));

            list.add(a);
        }
        return list;
    }
}