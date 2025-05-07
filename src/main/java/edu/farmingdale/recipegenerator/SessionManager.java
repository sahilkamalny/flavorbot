package edu.farmingdale.recipegenerator;


/**
 * A singleton class to manage the session of the currently logged-in user.
 * This class is responsible for storing the logged-in user and providing access to the user's session data.
 */
public class SessionManager {
    // --- 1) single shared instance ---
    private static final SessionManager INSTANCE = new SessionManager();

    // --- 2) the “logged in” user ---
    private volatile User currentUser;

    // private ctor → no outside instantiation
    private SessionManager() { }

    /**
     * @return the one-and-only SessionManager
     */
    public static SessionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the currently logged-in user.
     * This method should be called after the user's credentials are validated.
     *
     * @param user the User object representing the currently logged-in user
     */
    public synchronized void setCurrentUser(User user) {
        this.currentUser = user;
    }


    /**
     * Retrieves the user who is currently logged in.
     *
     * @return the User object representing the logged-in user, or null if no user is logged in
     */
    public synchronized User getCurrentUser() {
        return currentUser;
    }

    /**
     * Clears the current session, effectively logging the user out.
     */
    public synchronized void clearSession() {
        this.currentUser = null;
    }

    /**
     * Checks if there is a user currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public synchronized boolean isLoggedIn() {
        return currentUser != null;
    }
}