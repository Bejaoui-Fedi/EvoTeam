package tn.esprit.services;

import java.util.Arrays;
import java.util.List;

public class AIService {

    // Liste de mots-clés indiquant une urgence possible
    private static final List<String> EMERGENCY_KEYWORDS = Arrays.asList(
            "douleur", "intense", "respirer", "respiration", "poitrine", "sang", "saignement",
            "fracture", "accident", "brûlure", "inconscient", "crise", "étouffement",
            "malaise", "grave", "urgent", "immédiat", "fort", "insupportable", "perte");

    /**
     * Analyse le motif d'un rendez-vous pour déterminer s'il s'agit d'une urgence.
     * 
     * @param motif Le texte saisi par le patient
     * @return true si une urgence est détectée, false sinon.
     */
    public boolean isUrgent(String motif) {
        if (motif == null || motif.trim().isEmpty()) {
            return false;
        }

        String lowerMotif = motif.toLowerCase();

        // Comptage des occurrences de mots-clés
        long count = EMERGENCY_KEYWORDS.stream()
                .filter(keyword -> lowerMotif.contains(keyword))
                .count();

        // On considère que c'est urgent si au moins 2 mots-clés sont présents
        // ou si un mot très fort comme "urgent" ou "grave" est présent.
        boolean hasStrongKeyword = lowerMotif.contains("urgent") || lowerMotif.contains("grave")
                || lowerMotif.contains("immédiat");

        return count >= 2 || hasStrongKeyword;
    }
}
