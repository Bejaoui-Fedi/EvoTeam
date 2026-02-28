package tn.esprit.services;

import tn.esprit.entities.User;
import tn.esprit.utils.Session;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoriqueService {

    private static final String FICHIER = "historique_evenements.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void ajouter(String action, String details) {
        User user = Session.currentUser;
        String userInfo = (user != null) ? user.getRole() + " " + user.getNom() : "Inconnu";

        String ligne = String.format("[%s] %s : %s → %s%n",
                LocalDateTime.now().format(formatter),
                userInfo,
                action,
                details);

        try (FileWriter fw = new FileWriter(FICHIER, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(ligne);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> lireTout() {
        List<String> lignes = new ArrayList<>();
        File f = new File(FICHIER);
        if (!f.exists()) return lignes;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                lignes.add(ligne);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lignes;
    }
}