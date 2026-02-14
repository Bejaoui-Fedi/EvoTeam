package org.example.tn.esprit.mains;

import org.example.tn.esprit.utils.MyDataBase;
import java.sql.*;

public class VerifyMediaUrls {
    public static void main(String[] args) {
        Connection conn = MyDataBase.getInstance().getMyConnection();

        try (java.io.PrintWriter out = new java.io.PrintWriter("media_verification.txt", "UTF-8")) {
            out.println("=== OBJECTIVES WITH MEDIA URLs ===");
            Statement stmt1 = conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery("SELECT id_objective, title, icon FROM objective LIMIT 10");

            while (rs1.next()) {
                int id = rs1.getInt("id_objective");
                String title = rs1.getString("title");
                String icon = rs1.getString("icon");
                out.println(id + ". " + title);
                out.println("   Icon: " + (icon != null && !icon.isEmpty() ? icon : "NO IMAGE"));
                out.println();
            }

            out.println("\n=== EXERCISES WITH MEDIA URLs ===");
            Statement stmt2 = conn.createStatement();
            ResultSet rs2 = stmt2.executeQuery("SELECT id_exercise, title, mediaUrl FROM exercise LIMIT 10");

            while (rs2.next()) {
                int id = rs2.getInt("id_exercise");
                String title = rs2.getString("title");
                String mediaUrl = rs2.getString("mediaUrl");
                out.println(id + ". " + title);
                out.println("   Media: " + (mediaUrl != null && !mediaUrl.isEmpty() ? mediaUrl : "NO IMAGE"));
                out.println();
            }

            System.out.println("Verification written to media_verification.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
