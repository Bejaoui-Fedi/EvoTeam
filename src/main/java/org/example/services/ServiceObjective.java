package org.example.services;

import org.example.entities.Objective;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServiceObjective implements IService <Objective> {

   private Connection connection;

   public ServiceObjective() {
       connection= MyDataBase.getInstance().getMyConnection();
    }


    @Override
    public void insert(Objective objective) throws SQLException {
        String sql = "INSERT INTO `objective`(`user_id`, `title`, `description`, `icon`, `color`, `level`, `isPublished`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, objective.getUserId());
            ps.setString(2, objective.getTitle());
            ps.setString(3, objective.getDescription());
            ps.setString(4, objective.getIcon());
            ps.setString(5, objective.getColor());
            ps.setString(6, objective.getLevel());
            ps.setInt(7, objective.isPublished());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) objective.setIdObjective(rs.getLong(1));
            }
        }
    }

    @Override
    public void update(Objective objective) throws SQLException {
        String sql = "UPDATE `objective` SET `user_id`=?, `title`=?, `description`=?, `icon`=?, `color`=?, `level`=?, `isPublished`=? " +
                "WHERE `id_objective`=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, objective.getUserId());
            stmt.setString(2, objective.getTitle());
            stmt.setString(3, objective.getDescription());
            stmt.setString(4, objective.getIcon());
            stmt.setString(5, objective.getColor());
            stmt.setString(6, objective.getLevel());
            stmt.setInt(7, objective.isPublished());
            stmt.setLong(8, objective.getIdObjective());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
       String sql="DELETE FROM `objective` WHERE `id_objective`=?";
       PreparedStatement stmt = connection.prepareStatement(sql);
       stmt.setInt(1, id);
       stmt.executeUpdate();

    }

    @Override
    public List<Objective> show() throws SQLException {
       List<Objective> objectives = new ArrayList<>();
       String sql="SELECT * FROM `objective`";
       Statement stmt = connection.createStatement();
       ResultSet rs = stmt.executeQuery(sql);
       while (rs.next()) {
           Objective O= new Objective();
           O.setIdObjective(rs.getLong("id_objective"));
           O.setTitle(rs.getString("title"));
           O.setDescription(rs.getString("description"));
           O.setIcon(rs.getString("icon"));
           O.setColor(rs.getString("color"));
           O.setLevel(rs.getString("level"));
           O.setPublished(rs.getInt("isPublished"));
           O.setUserId(rs.getInt("user_id"));
           objectives.add(O);

       }
        return objectives ;
    }
}
