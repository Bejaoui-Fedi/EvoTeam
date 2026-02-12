package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/eventdb?useSSL=false";
            String user = "root";
            String password = "";

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion MySQL réussie !");
        } catch (Exception e) {
            System.out.println("Erreur connexion : " + e.getMessage());
        }
    }

    public static DBConnection getInstance() {
        if (instance == null)
            instance = new DBConnection();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
