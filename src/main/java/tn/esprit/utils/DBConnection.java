package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private final String URL = "jdbc:mysql://localhost:3306/pi_java";
    private final String USER = "root";
    private final String PSW = "";

    private Connection myConnection;
    private static DBConnection instance;

    private DBConnection() {
        try {
            // For MySQL 8.0+
            Class.forName("com.mysql.cj.jdbc.Driver");

            // For older MySQL 5.x (if needed)
            // Class.forName("com.mysql.jdbc.Driver");

            myConnection = DriverManager.getConnection(URL, USER, PSW);
            System.out.println("✅ Connected to pi_java database!");

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver missing! Add to pom.xml:");
            System.err.println("<dependency>");
            System.err.println("    <groupId>mysql</groupId>");
            System.err.println("    <artifactId>mysql-connector-java</artifactId>");
            System.err.println("    <version>8.0.33</version>");
            System.err.println("</dependency>");

        } catch (SQLException e) {
            System.err.println("Connection failed! Error: " + e.getMessage());
            System.err.println("Try: Start XAMPP -> Apache & MySQL");
            System.err.println("Or use: jdbc:mysql://localhost:3307/pi_java");
        }
    }

    public Connection getMyConnection() {
        return myConnection;
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
}