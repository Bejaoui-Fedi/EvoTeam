package org.example.tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {


    private final String URL="jdbc:mysql://localhost:3306/evolia";
    private final String USERNAME="root";
    private final String PASSWORD="";

    private Connection myConnection;

    private static MyDataBase instance;
    public  MyDataBase()
    {
        try {
            myConnection= DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("Connected to database successfully");;
        } catch (SQLException e) {
            System.out.println(e.getMessage());;
        }
    }
    public static MyDataBase getInstance()
    {
        if(instance==null)
        {
            instance=new MyDataBase();
        }
        return instance;
    }

    public Connection getMyConnection() {
        return myConnection;
    }
}
