package org.example.tn.esprit.mains;

import org.example.tn.esprit.entities.Exercise;
import org.example.tn.esprit.entities.Objective;
import org.example.tn.esprit.entities.User;
import org.example.tn.esprit.services.ServiceExercise;
import org.example.tn.esprit.services.ServiceObjective;
import org.example.tn.esprit.utils.Session;

import java.sql.SQLException;

public class DatabaseSyncTester {

    public static void main(String[] args) {
        System.out.println("=== Starting Database Synchronization Verification ===\n");

        // 1. Mock Admin Session (needed for Admin-only CRUD operations)
        User admin = new User();
        admin.setRole("ADMIN");
        admin.setId(1);
        Session.currentUser = admin;
        System.out.println("[Step 1] Mocked Admin Session (ID: 1)");

        ServiceObjective serviceObjective = new ServiceObjective();
        ServiceExercise serviceExercise = new ServiceExercise();

        try {
            // --- OBJECTIVE CRUD TEST ---
            System.out.println("\n--- Testing ServiceObjective ---");
            Objective testObj = new Objective(1, "Test Sync Objective", "Description for Sync Test", "Debutant", 0);

            // Insert
            serviceObjective.insert(testObj);
            Long objId = testObj.getIdObjective();
            System.out.println("[Objective] Inserted ID: " + objId);

            // Verify Insert
            Objective retrievedObj = serviceObjective.getById(objId.intValue());
            if (retrievedObj != null && "Test Sync Objective".equals(retrievedObj.getTitle())) {
                System.out.println("[Objective] SUCCESS: Inserted and verified in DB.");
            } else {
                System.err.println("[Objective] FAILURE: Could not verify insertion.");
                return;
            }

            // Update
            retrievedObj.setTitle("Updated Sync Title");
            serviceObjective.update(retrievedObj);

            // Verify Update
            Objective updatedObj = serviceObjective.getById(objId.intValue());
            if (updatedObj != null && "Updated Sync Title".equals(updatedObj.getTitle())) {
                System.out.println("[Objective] SUCCESS: Updated and verified in DB.");
            } else {
                System.err.println("[Objective] FAILURE: Could not verify update.");
                return;
            }

            // --- EXERCISE CRUD TEST ---
            System.out.println("\n--- Testing ServiceExercise ---");
            Exercise testEx = new Exercise();
            testEx.setUserId(1);
            testEx.setObjectiveId(objId.intValue());
            testEx.setTitle("Test Sync Exercise");
            testEx.setDescription("Description for Exercise Sync Test");
            testEx.setType("Cardio");
            testEx.setDurationMinutes(20);
            testEx.setDifficulty("Facile");
            testEx.setMediaUrl("https://example.com/media.jpg");
            testEx.setSteps("Step 1, Step 2");
            testEx.setIsPublished(0);

            // Insert
            serviceExercise.insert(testEx);
            int exId = testEx.getIdExercise();
            System.out.println("[Exercise] Inserted ID: " + exId);

            // Verify Insert
            Exercise retrievedEx = serviceExercise.getById(exId);
            if (retrievedEx != null && "Test Sync Exercise".equals(retrievedEx.getTitle())) {
                System.out.println("[Exercise] SUCCESS: Inserted and verified in DB.");
            } else {
                System.err.println("[Exercise] FAILURE: Could not verify insertion.");
                return;
            }

            // Update
            retrievedEx.setDurationMinutes(35);
            serviceExercise.update(retrievedEx);

            // Verify Update
            Exercise updatedEx = serviceExercise.getById(exId);
            if (updatedEx != null && updatedEx.getDurationMinutes() == 35) {
                System.out.println("[Exercise] SUCCESS: Updated and verified in DB.");
            } else {
                System.err.println("[Exercise] FAILURE: Could not verify update.");
                return;
            }

            // --- CLEANUP (Delete) ---
            System.out.println("\n--- Cleaning Up ---");
            serviceExercise.delete(exId);
            if (serviceExercise.getById(exId) == null) {
                System.out.println("[Exercise] SUCCESS: Deleted from DB.");
            } else {
                System.err.println("[Exercise] FAILURE: Could not verify deletion.");
            }

            serviceObjective.delete(objId.intValue());
            if (serviceObjective.getById(objId.intValue()) == null) {
                System.out.println("[Objective] SUCCESS: Deleted from DB.");
            } else {
                System.err.println("[Objective] FAILURE: Could not verify deletion.");
            }

            System.out.println("\n=== ALL DATABASE SYNC TESTS PASSED! ===");

        } catch (SQLException e) {
            System.err.println("\nERROR during DB sync test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
