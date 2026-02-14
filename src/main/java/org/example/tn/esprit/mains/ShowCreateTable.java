package org.example.tn.esprit.mains;

import org.example.tn.esprit.utils.MyDataBase;
import java.sql.*;

public class ShowCreateTable {
    public static void main(String[] args) {
        Connection conn = MyDataBase.getInstance().getMyConnection();
        try (java.io.PrintWriter out = new java.io.PrintWriter("create_table.txt", "UTF-8")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE exercise");
            if (rs.next()) {
                String createStatement = rs.getString(2);
                out.println(createStatement);
                System.out.println("Written to create_table.txt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
