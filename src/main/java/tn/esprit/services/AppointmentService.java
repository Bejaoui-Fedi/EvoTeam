package tn.esprit.services;

import tn.esprit.entities.Appointment;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService implements IService<Appointment> {

    private Connection connection;

    public AppointmentService() {
        connection = DBConnection.getInstance().getConnection();
    }

    @Override
    public void add(Appointment a) throws SQLException {
        // ✅ AJOUTE user_id dans la requête
        String sql = "INSERT INTO appointment(date_rdv, heure_rdv, statut, motif, type_rdv, user_id) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setDate(1, Date.valueOf(a.getDateRdv()));
        ps.setTime(2, Time.valueOf(a.getHeureRdv()));
        ps.setString(3, a.getStatut());
        ps.setString(4, a.getMotif());
        ps.setString(5, a.getTypeRdv());
        // ✅ AJOUTE CETTE LIGNE
        ps.setInt(6, a.getUserId());  // ← TRÈS IMPORTANT !

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) a.setId(rs.getInt(1));

        System.out.println("RDV ajouté : " + a + " | user_id: " + a.getUserId());
    }

    @Override
    public List<Appointment> getAll() throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointment";  // Vérifie que user_id est sélectionné

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

            // ✅ AJOUTE CETTE LIGNE
            a.setUserId(rs.getInt("user_id"));  // ← TRÈS IMPORTANT !

            list.add(a);
        }
        return list;
    }

    @Override
    public void update(Appointment a) throws SQLException {
        String sql = "UPDATE appointment SET date_rdv=?, heure_rdv=?, statut=?, motif=?, type_rdv=? WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setDate(1, Date.valueOf(a.getDateRdv()));
        ps.setTime(2, Time.valueOf(a.getHeureRdv()));
        ps.setString(3, a.getStatut());
        ps.setString(4, a.getMotif());
        ps.setString(5, a.getTypeRdv());
        ps.setInt(6, a.getId());

        ps.executeUpdate();
        System.out.println("RDV modifié : " + a);
    }

    @Override
    public void delete(Appointment a) throws SQLException {
        String sql = "DELETE FROM appointment WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, a.getId());
        ps.executeUpdate();

        System.out.println("RDV supprimé !");
    }
}
