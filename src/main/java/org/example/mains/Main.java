package org.example.mains;

import org.example.entities.Exercise;
import org.example.entities.Objective;
import org.example.entities.User;
import org.example.services.ServiceExercise;
import org.example.services.ServiceObjective;
import org.example.services.ServiceUser;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        ServiceUser su = new ServiceUser();
        ServiceObjective so = new ServiceObjective();
        ServiceExercise se = new ServiceExercise();

        while (true) {
            System.out.println("\n=============================");
            System.out.println("        MENU PRINCIPAL");
            System.out.println("=============================");
            System.out.println("1) Gérer User");
            System.out.println("2) Gérer Objective");
            System.out.println("3) Gérer Exercise");
            System.out.println("0) Quitter");
            int choix = readInt("Choix: ");

            switch (choix) {
                case 1 -> menuUser(su);
                case 2 -> menuObjective(so, su);
                case 3 -> menuExercise(se, so, su);
                case 0 -> {
                    System.out.println(" Au revoir !");
                    return;
                }
                default -> System.out.println(" Choix invalide.");
            }
        }
    }

    // ==========================
    // USER MENU
    // ==========================
    private static void menuUser(ServiceUser su) {
        while (true) {
            System.out.println("\n--- USER (choisir 1 action) ---");
            System.out.println("1) Affichage");
            System.out.println("2) Ajout");
            System.out.println("3) Modification");
            System.out.println("4) Suppression");
            System.out.println("0) Retour");
            int action = readInt("Action: ");

            try {
                switch (action) {
                    case 1 -> afficherUsers(su);
                    case 2 -> ajouterUser(su);
                    case 3 -> modifierUser(su);
                    case 4 -> supprimerUser(su);
                    case 0 -> { return; }
                    default -> System.out.println(" Choix invalide.");
                }
            } catch (SQLException e) {
                System.out.println(" Erreur SQL: " + e.getMessage());
            }
        }
    }

    private static void afficherUsers(ServiceUser su) throws SQLException {
        System.out.println("\n AFFICHAGE USERS");
        List<User> list = su.show();
        if (list.isEmpty()) System.out.println("(vide)");
        else list.forEach(System.out::println);
    }

    private static void ajouterUser(ServiceUser su) throws SQLException {
        System.out.println("\n AJOUT USER");
        String nom = readNonEmpty("Nom: ");
        String email = readNonEmpty("Email: ");
        String password = readNonEmpty("Password (idéalement hash): ");
        String role = readOptional("Role (ex: USER/ADMIN) [vide => USER]: ");
        String telephone = readOptional("Téléphone (optionnel): ");
        int actif = readInt01("Actif (0/1): ");

        if (role.isBlank()) role = "USER";
        if (telephone.isBlank()) telephone = null;

        User u = new User(nom, email, password, role, telephone, actif);
        su.insert(u);

        System.out.println(" User ajouté. ID = " + u.getId());
    }

    private static void modifierUser(ServiceUser su) throws SQLException {
        System.out.println("\n MODIFICATION USER");
        int id = readInt("ID user à modifier: ");

        String nom = readNonEmpty("Nouveau nom: ");
        String email = readNonEmpty("Nouveau email: ");
        String password = readNonEmpty("Nouveau password (hash): ");
        String role = readOptional("Nouveau role (vide => USER): ");
        String telephone = readOptional("Nouveau téléphone (optionnel): ");
        int actif = readInt01("Actif (0/1): ");

        if (role.isBlank()) role = "USER";
        if (telephone.isBlank()) telephone = null;

        User u = new User(id, nom, email, password, role, telephone, actif);
        su.update(u);

        System.out.println(" Modification terminée.");
    }

    private static void supprimerUser(ServiceUser su) throws SQLException {
        System.out.println("\n✅ SUPPRESSION USER");
        int id = readInt("ID user à supprimer: ");
        String confirm = readNonEmpty("Confirmer (oui/non): ");

        if (confirm.equalsIgnoreCase("oui")) {
            su.delete(id);
            System.out.println(" Suppression terminée.");
        } else {
            System.out.println(" Suppression annulée.");
        }
    }

    // ==========================
    // OBJECTIVE MENU
    // ==========================
    private static void menuObjective(ServiceObjective so, ServiceUser su) {
        while (true) {
            System.out.println("\n--- OBJECTIVE (choisir 1 action) ---");
            System.out.println("1) Affichage");
            System.out.println("2) Ajout");
            System.out.println("3) Modification");
            System.out.println("4) Suppression");
            System.out.println("0) Retour");
            int action = readInt("Action: ");

            try {
                switch (action) {
                    case 1 -> afficherObjectives(so);
                    case 2 -> ajouterObjective(so, su);
                    case 3 -> modifierObjective(so, su);
                    case 4 -> supprimerObjective(so);
                    case 0 -> { return; }
                    default -> System.out.println(" Choix invalide.");
                }
            } catch (SQLException e) {
                System.out.println(" Erreur SQL: " + e.getMessage());
            }
        }
    }

    private static void afficherObjectives(ServiceObjective so) throws SQLException {
        System.out.println("\n AFFICHAGE OBJECTIVES");
        List<Objective> list = so.show();
        if (list.isEmpty()) System.out.println("(vide)");
        else list.forEach(System.out::println);
    }

    private static void ajouterObjective(ServiceObjective so, ServiceUser su) throws SQLException {
        System.out.println("\n AJOUT OBJECTIVE");

        System.out.println(" Choisis un userId EXISTANT (Option B).");
        afficherUsers(su);
        int userId = readInt("userId: ");

        String title = readNonEmpty("Title: ");
        String description = readNonEmpty("Description: ");
        String icon = readOptional("Icon (optionnel): ");
        String color = readOptional("Color (optionnel ex #70C070): ");
        String level = readNonEmpty("Level (debutant/moyen/avance/global): ");
        int isPublished = readInt01("isPublished (0/1): ");

        Objective o = new Objective();
        o.setUserId(userId);               //  nouveau champ
        o.setTitle(title);
        o.setDescription(description);
        o.setIcon(icon.isBlank() ? null : icon);
        o.setColor(color.isBlank() ? null : color);
        o.setLevel(level);
        o.setPublished(isPublished);

        so.insert(o);
        System.out.println(" Ajout terminé.");
    }

    private static void modifierObjective(ServiceObjective so, ServiceUser su) throws SQLException {
        System.out.println("\n MODIFICATION OBJECTIVE");
        long id = readLong("ID objective à modifier: ");

        System.out.println(" Choisis un userId EXISTANT (Option B).");
        afficherUsers(su);
        int userId = readInt("Nouveau userId: ");

        String title = readNonEmpty("Nouveau title: ");
        String description = readNonEmpty("Nouvelle description: ");
        String icon = readOptional("Nouvelle icon (optionnel): ");
        String color = readOptional("Nouvelle color (optionnel): ");
        String level = readNonEmpty("Nouveau level: ");
        int isPublished = readInt01("Nouveau isPublished (0/1): ");

        Objective o = new Objective();
        o.setIdObjective(id);
        o.setUserId(userId);               //  nouveau champ
        o.setTitle(title);
        o.setDescription(description);
        o.setIcon(icon.isBlank() ? null : icon);
        o.setColor(color.isBlank() ? null : color);
        o.setLevel(level);
        o.setPublished(isPublished);

        so.update(o);
        System.out.println(" Modification terminée.");
    }

    private static void supprimerObjective(ServiceObjective so) throws SQLException {
        System.out.println("\n SUPPRESSION OBJECTIVE");
        int id = readInt("ID objective à supprimer: ");
        String confirm = readNonEmpty("Confirmer (oui/non): ");
        if (confirm.equalsIgnoreCase("oui")) {
            so.delete(id);
            System.out.println(" Suppression terminée.");
        } else {
            System.out.println(" Suppression annulée.");
        }
    }

    // ==========================
    // EXERCISE MENU
    // ==========================
    private static void menuExercise(ServiceExercise se, ServiceObjective so, ServiceUser su) {
        while (true) {
            System.out.println("\n--- EXERCISE (choisir 1 action) ---");
            System.out.println("1) Affichage (tous)");
            System.out.println("2) Ajout");
            System.out.println("3) Modification");
            System.out.println("4) Suppression");
            System.out.println("5) Afficher par objectiveId");
            System.out.println("6) Afficher par userId");
            System.out.println("0) Retour");
            int action = readInt("Action: ");

            try {
                switch (action) {
                    case 1 -> afficherExercises(se);
                    case 2 -> ajouterExercise(se, so, su);
                    case 3 -> modifierExercise(se, so, su);
                    case 4 -> supprimerExercise(se);
                    case 5 -> afficherExercisesParObjective(se);
                    case 6 -> afficherExercisesParUser(se, su);
                    case 0 -> { return; }
                    default -> System.out.println(" Choix invalide.");
                }
            } catch (SQLException e) {
                System.out.println(" Erreur SQL: " + e.getMessage());
            }
        }
    }

    private static void afficherExercises(ServiceExercise se) throws SQLException {
        System.out.println("\n AFFICHAGE EXERCISES");
        List<Exercise> list = se.getAll();
        if (list.isEmpty()) System.out.println("(vide)");
        else list.forEach(System.out::println);
    }

    private static void afficherExercisesParObjective(ServiceExercise se) throws SQLException {
        System.out.println("\n AFFICHAGE EXERCISES PAR OBJECTIVE");
        int objId = readInt("objectiveId: ");
        List<Exercise> list = se.getByObjectiveId(objId);
        if (list.isEmpty()) System.out.println("(vide)");
        else list.forEach(System.out::println);
    }

    private static void afficherExercisesParUser(ServiceExercise se, ServiceUser su) throws SQLException {
        System.out.println("\n AFFICHAGE EXERCISES PAR USER");
        afficherUsers(su);
        int userId = readInt("userId: ");
        List<Exercise> list = se.getByUserId(userId);
        if (list.isEmpty()) System.out.println("(vide)");
        else list.forEach(System.out::println);
    }

    private static void ajouterExercise(ServiceExercise se, ServiceObjective so, ServiceUser su) throws SQLException {
        System.out.println("\n AJOUT EXERCISE");

        System.out.println(" Choisis un userId EXISTANT (Option B).");
        afficherUsers(su);
        int userId = readInt("userId: ");

        System.out.println(" Choisis un objectiveId EXISTANT (il doit appartenir à ce userId).");
        afficherObjectives(so);
        int objectiveId = readInt("objectiveId: ");

        String title = readNonEmpty("Title: ");
        String description = readOptional("Description (optionnel): ");
        String type = readOptional("Type (respiration/journaling/meditation/cbt/challenge/relaxation): ");
        int duration = readInt("durationMinutes: ");
        String difficulty = readNonEmpty("difficulty (debutant/moyen/avance/global): ");
        String mediaUrl = readOptional("mediaUrl (optionnel): ");
        int isPublished = readInt01("isPublished (0/1): ");

        Exercise e = new Exercise();
        e.setUserId(userId);                 //  nouveau champ
        e.setObjectiveId(objectiveId);
        e.setTitle(title);
        e.setDescription(description.isBlank() ? null : description);
        e.setType(type.isBlank() ? null : type);
        e.setDurationMinutes(duration);
        e.setDifficulty(difficulty);
        e.setMediaUrl(mediaUrl.isBlank() ? null : mediaUrl);
        e.setIsPublished(isPublished);

        se.insert(e);
        System.out.println(" Ajout terminé. ID généré = " + e.getIdExercise());
    }

    private static void modifierExercise(ServiceExercise se, ServiceObjective so, ServiceUser su) throws SQLException {
        System.out.println("\n MODIFICATION EXERCISE");
        int idExercise = readInt("id_exercise à modifier: ");

        System.out.println(" Choisis un userId EXISTANT (Option B).");
        afficherUsers(su);
        int userId = readInt("Nouveau userId: ");

        System.out.println(" Si tu changes objectiveId, il doit exister ET appartenir au même userId.");
        afficherObjectives(so);
        int objectiveId = readInt("Nouveau objectiveId: ");

        String title = readNonEmpty("Nouveau title: ");
        String description = readOptional("Nouvelle description (optionnel): ");
        String type = readOptional("Nouveau type (optionnel): ");
        int duration = readInt("Nouvelle durée (minutes): ");
        String difficulty = readNonEmpty("Nouvelle difficulty: ");
        String mediaUrl = readOptional("Nouveau mediaUrl (optionnel): ");
        int isPublished = readInt01("Nouveau isPublished (0/1): ");

        Exercise e = new Exercise();
        e.setIdExercise(idExercise);
        e.setUserId(userId);                 //  nouveau champ
        e.setObjectiveId(objectiveId);
        e.setTitle(title);
        e.setDescription(description.isBlank() ? null : description);
        e.setType(type.isBlank() ? null : type);
        e.setDurationMinutes(duration);
        e.setDifficulty(difficulty);
        e.setMediaUrl(mediaUrl.isBlank() ? null : mediaUrl);
        e.setIsPublished(isPublished);

        se.update(e);
        System.out.println(" Modification terminée.");
    }

    private static void supprimerExercise(ServiceExercise se) throws SQLException {
        System.out.println("\n SUPPRESSION EXERCISE");
        int id = readInt("id_exercise à supprimer: ");
        String confirm = readNonEmpty("Confirmer (oui/non): ");
        if (confirm.equalsIgnoreCase("oui")) {
            se.delete(id);
            System.out.println(" Suppression terminée.");
        } else {
            System.out.println(" Suppression annulée.");
        }
    }

    // ==========================
    // INPUT HELPERS
    // ==========================
    private static int readInt(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println(" Entrez un entier valide.");
            }
        }
    }

    private static long readLong(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Long.parseLong(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println(" Entrez un long valide.");
            }
        }
    }

    private static int readInt01(String msg) {
        while (true) {
            int v = readInt(msg);
            if (v == 0 || v == 1) return v;
            System.out.println(" Valeur invalide (0 ou 1 seulement).");
        }
    }

    private static String readNonEmpty(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println(" Champ obligatoire.");
        }
    }

    private static String readOptional(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }
}