package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/pidevusermanagement";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static DBConnection instance;

    private DBConnection(){
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion MySQL réussie");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    public Connection getConnection() {
        return connection;
    }
    public static DBConnection getInstance(){
        if (instance == null){
            instance = new DBConnection();
        }
        return instance;
    }
}
