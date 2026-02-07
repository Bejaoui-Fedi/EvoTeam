package org.example.mains;

import org.example.entities.Objective;
import org.example.utils.MyDataBase;
import org.example.services.ServiceObjective;


import java.sql.SQLException;


public class Main {
    public static void main(String[] args) {
        MyDataBase myDataBase = MyDataBase.getInstance();
        ServiceObjective so =new ServiceObjective();
        try {
            so.insert(new Objective("Gestion anxiété","Techniques simples pour apaiser les pensées et reprendre le contrôle.","moon","#4D96FF","global",0));
            System.out.println("Objective is inserted");
            so.update(new Objective(9L,"Gestion anxité","Reprend le controle en gerant tes pensees","moon","#4D96FF","debutant",1));
            System.out.println("Objective is modified");
            so.delete(11);
            System.out.println("Objective is deleted");
            System.out.println(so.show());
        }catch (SQLException e) {
            System.out.println(" Error while inserting objective: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("ErrorCode: " + e.getErrorCode());
            e.printStackTrace();
        }
    }
}
