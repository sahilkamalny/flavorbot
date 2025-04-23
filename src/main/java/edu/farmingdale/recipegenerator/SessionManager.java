package edu.farmingdale.recipegenerator;

import edu.farmingdale.recipegenerator.User;

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
     * Set the currently logged-in user.
     * Call after you’ve validated credentials.
     */
    public synchronized void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * @return the user who’s logged in, or null if nobody
     */
    public synchronized User getCurrentUser() {
        return currentUser;
    }

    /**
     * Clears the session (logs out)
     */
    public synchronized void clearSession() {
        this.currentUser = null;
    }

    /**
     * @return true if someone is logged in
     */
    public synchronized boolean isLoggedIn() {
        return currentUser != null;
    }
}