package org.example.tn.esprit.mains;

import org.example.tn.esprit.utils.MyDataBase;
import java.sql.*;

public class ExerciseTableInspector {
    public static void main(String[] args) {
        Connection conn = MyDataBase.getInstance().getMyConnection();
        try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.OutputStreamWriter(
                new java.io.FileOutputStream("columns_utf8.txt"), java.nio.charset.StandardCharsets.UTF_8))) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "exercise", null);
            out.println("Columns in 'exercise' table:");
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String typeName = rs.getString("TYPE_NAME");
                out.println(" - " + columnName + " (" + typeName + ")");
            }
            System.out.println("Columns written to columns_utf8.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
