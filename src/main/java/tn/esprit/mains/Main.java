package tn.esprit.mains;

import tn.esprit.entities.*;
import tn.esprit.services.*;

import java.time.LocalDate;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        UserService userService = new UserService();
        UserProfileService profileService = new UserProfileService();

        try {
            // === CREATE ===
            User u = new User("Fadi", "fadi@example.com", "pass123", "USER", "123456");
            userService.add(u);

            UserProfile p = new UserProfile(
                    u.getId(),
                    "https://example.com/avatar.png",
                    "Bio simple",
                    LocalDate.of(2000, 1, 1),
                    "FR", true, false,
                    "PUBLIC"
            );
            profileService.add(p);

            // === UPDATE USER ===
            System.out.println("\n=== TEST UPDATE USER ===");
            u.setNom("Fedi Mis à Jour");
            u.setTelephone("99999999");
            userService.update(u);

            // === UPDATE PROFILE ===
            System.out.println("\n=== TEST UPDATE USER PROFILE ===");
            p.setBio("Nouvelle biographie pour test update");
            p.setLangue("EN");
            profileService.update(p);

            // === DELETE PROFILE ===
            System.out.println("\n=== TEST DELETE USER PROFILE ===");
            profileService.delete(p);

            // === DELETE USER ===
            System.out.println("\n=== TEST DELETE USER ===");
            userService.delete(u);

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }
}