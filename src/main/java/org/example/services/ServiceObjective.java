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
        if (connection == null) {
            throw new IllegalStateException("Connexion BD = NULL. Verify MyDataBase (URL/user/password).");        }
    String sql= "INSERT INTO `objective`(`title`, `description`, `icon`, `color`, `level`, `isPublished`) " +
            "VALUES ('"+ objective.getTitle()+"','"+ objective.getDescription()+"','"+ objective.getIcon()+"','"+ objective.getColor()+"','"+ objective.getLevel()+"','"+ objective.isPublished()+"')";
    Statement stmt = connection.createStatement();
    stmt.executeUpdate(sql);
    }

    @Override
    public void update(Objective objective) throws SQLException {
      String sql="UPDATE `objective` SET `title`=?,`description`=?,`icon`=?,`color`=?,`level`=?,`isPublished`=? WHERE `id_objective`=?"  ;
   PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, objective.getTitle());
        stmt.setString(2, objective.getDescription());
        stmt.setString(3, objective.getIcon());
        stmt.setString(4, objective.getColor());
        stmt.setString(5, objective.getLevel());
        stmt.setInt(6, objective.isPublished());
        stmt.setLong(7, objective.getIdObjective());
        stmt.executeUpdate();

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
           objectives.add(O);

       }
        return objectives ;
    }
}
