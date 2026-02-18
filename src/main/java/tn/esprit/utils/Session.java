package tn.esprit.utils;

import tn.esprit.entities.User;

/**
 * Global Session management for Evolia.
 * This class serves as the bridge between modules.
 *
 * INTEGRATION TIP: For the teammate in charge of User Management:
 * Simply set 'Session.currentUser = authenticatedUser;' after a successful
 * login.
 * The Exercise module will automatically adapt its UI and permissions based on
 * the role.
 */
public class Session {

    /**
     * The currently logged in user.
     * Accessible by all modules to check permissions or user data.
     */
    public static User currentUser;

    /**
     * Checks if the current session belongs to an ADMIN.
     */
    public static boolean isAdmin() {
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Checks if the current session belongs to a regular USER.
     */
    public static boolean isUser() {
        return currentUser != null && "USER".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Returns the role string or "NONE" if no user is logged in.
     */
    public static String role() {
        return currentUser == null ? "NONE" : currentUser.getRole();
    }
}