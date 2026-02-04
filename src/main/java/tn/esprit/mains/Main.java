package tn.esprit.mains;

import tn.esprit.entities.User;
import tn.esprit.services.UserService;
import tn.esprit.utils.DBConnection;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Test de la connexion à la base de données
        DBConnection DB = DBConnection.getInstance();

        System.out.println("=== Test de connexion à la base de données ===\n");

        // Création d'une instance de UserService
        UserService userService = new UserService();

        try {
            // ========== TEST 1: CREATE (Ajouter des utilisateurs) ==========
            System.out.println("========== TEST CREATE ==========");

            User user1 = new User("Ahmed Ben Ali", "ahmed@example.com", "password123", "Admin");
            User user2 = new User("Fatma Trabelsi", "fatma@example.com", "pass456", "User");
            User user3 = new User("Mohamed Saidi", "mohamed@example.com", "securepass", "Moderator");

            userService.add(user1);
            userService.add(user2);
            userService.add(user3);

            System.out.println();

            // ========== TEST 2: READ (Afficher tous les utilisateurs) ==========
            System.out.println("========== TEST READ (Tous les utilisateurs) ==========");

            List<User> users = userService.getAll();

            if (users.isEmpty()) {
                System.out.println("Aucun utilisateur trouvé dans la base de données.");
            } else {
                System.out.println("Liste de tous les utilisateurs :");
                for (User user : users) {
                    System.out.println(user);
                }
            }

            System.out.println();

            // ========== TEST 3: READ BY ID (Rechercher un utilisateur par ID) ==========
            System.out.println("========== TEST READ BY ID ==========");

            if (!users.isEmpty()) {
                int searchId = users.get(0).getId();
                User foundUser = userService.getUserById(searchId);

                if (foundUser != null) {
                    System.out.println("Utilisateur trouvé avec l'ID " + searchId + " : " + foundUser);
                } else {
                    System.out.println("Aucun utilisateur trouvé avec l'ID " + searchId);
                }
            }

            System.out.println();

            // ========== TEST 4: READ BY EMAIL (Rechercher un utilisateur par email) ==========
            System.out.println("========== TEST READ BY EMAIL ==========");

            String searchEmail = "fatma@example.com";
            User foundByEmail = userService.getUserByEmail(searchEmail);

            if (foundByEmail != null) {
                System.out.println("Utilisateur trouvé avec l'email '" + searchEmail + "' : " + foundByEmail);
            } else {
                System.out.println("Aucun utilisateur trouvé avec l'email '" + searchEmail + "'");
            }

            System.out.println();

            // ========== TEST 5: UPDATE (Mettre à jour un utilisateur) ==========
            System.out.println("========== TEST UPDATE ==========");

            if (!users.isEmpty()) {
                User userToUpdate = users.get(0);
                System.out.println("Avant mise à jour : " + userToUpdate);

                userToUpdate.setNom("Ahmed Ben Ali (Modifié)");
                userToUpdate.setEmail("ahmed.updated@example.com");
                userToUpdate.setRole("SuperAdmin");

                userService.update(userToUpdate);

                User updatedUser = userService.getUserById(userToUpdate.getId());
                System.out.println("Après mise à jour : " + updatedUser);
            }

            System.out.println();

            // ========== TEST 6: DELETE (Supprimer un utilisateur) ==========
            System.out.println("========== TEST DELETE ==========");

            List<User> allUsers = userService.getAll();

            if (allUsers.size() > 1) {
                User userToDelete = allUsers.get(allUsers.size() - 1);
                System.out.println("Suppression de l'utilisateur : " + userToDelete);

                userService.delete(userToDelete);

                System.out.println("\nListe après suppression :");
                List<User> remainingUsers = userService.getAll();
                for (User user : remainingUsers) {
                    System.out.println(user);
                }
            }

            System.out.println();

            // ========== AFFICHAGE FINAL ==========
            System.out.println("========== LISTE FINALE DES UTILISATEURS ==========");
            List<User> finalUsers = userService.getAll();
            System.out.println("Nombre total d'utilisateurs : " + finalUsers.size());
            for (User user : finalUsers) {
                System.out.println(user);
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors des tests : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Tests terminés ===");
    }
}