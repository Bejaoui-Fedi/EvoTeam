package tn.esprit.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class BadWordsService {

    private static Set<String> badWords = new HashSet<>();

    // Chargement automatique au démarrage
    static {
        loadBadWords();
    }

    private static void loadBadWords() {
        try {
            InputStream is = BadWordsService.class.getResourceAsStream("/badwords.txt");
            if (is == null) {
                System.err.println("❌ Fichier badwords.txt non trouvé dans resources");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty()) {
                    badWords.add(line);
                    count++;
                }
            }
            reader.close();
            System.out.println("✅ " + count + " mots interdits chargés");

        } catch (Exception e) {
            System.err.println("❌ Erreur chargement mots interdits: " + e.getMessage());
        }
    }

    /**
     * Vérifie si un texte contient des gros mots
     */
    public static boolean containsBadWords(String text) {
        if (text == null || text.isEmpty()) return false;

        String lowerText = text.toLowerCase();
        for (String badWord : badWords) {
            if (lowerText.contains(badWord)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Censure les gros mots (remplace par des *)
     */
    public static String censor(String text) {
        if (text == null) return text;

        String result = text;
        for (String badWord : badWords) {
            String stars = "*".repeat(badWord.length());
            result = result.replaceAll("(?i)" + badWord, stars);
        }
        return result;
    }

    /**
     * Retourne la liste des mots trouvés (pour affichage)
     */
    public static Set<String> getFoundBadWords(String text) {
        Set<String> found = new HashSet<>();
        if (text == null) return found;

        String lowerText = text.toLowerCase();
        for (String badWord : badWords) {
            if (lowerText.contains(badWord)) {
                found.add(badWord);
            }
        }
        return found;
    }
}