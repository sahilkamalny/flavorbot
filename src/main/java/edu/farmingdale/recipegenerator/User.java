package edu.farmingdale.recipegenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a user in the application.
 * This class encapsulates the user's details, including their unique ID, username, email,
 * hashed password, and user preferences stored as a JSON blob.
 */
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int    userID;
    private String username;
    private String email;
    private String hashedPassword;
    private String preferencesJson;  // JSON blob of user prefs

    /**
     * Default constructor for frameworks and serialization.
     */
    public User() {
        // no-arg constructor for frameworks/serialization
    }

    /**
     * Constructs a User with the specified details.
     *
     * @param userID          The user's unique identifier.
     * @param username        The user's username.
     * @param email           The user's email address.
     * @param hashedPassword  The user's hashed password.
     * @param preferencesJson The JSON string representing the user's preferences.
     */
    public User(int userID, String username, String email, String hashedPassword, String preferencesJson) {
        this.userID          = userID;
        this.username        = username;
        this.email           = email;
        this.hashedPassword  = hashedPassword;
        this.preferencesJson = preferencesJson;
    }

    // ─── Getters & Setters ──────────────────────────────────────────────────────
    /**
     * Gets the user's unique ID.
     *
     * @return The user ID.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Sets the user's unique ID.
     *
     * @param userID The user ID to set.
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * Gets the user's username.
     *
     * @return The user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's username.
     *
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the user's email.
     *
     * @return The user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's hashed password.
     *
     * @return The user's hashed password.
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * Sets the user's hashed password.
     *
     * @param hashedPassword The hashed password to set.
     */
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    /**
     * Gets the user's preferences stored as a JSON string.
     *
     * @return The user's preferences in JSON format.
     */
    public String getPreferencesJson() {
        return preferencesJson;
    }

    /**
     * Sets the user's preferences as a JSON string.
     *
     * @param preferencesJson The preferences in JSON format.
     */
    public void setPreferencesJson(String preferencesJson) {
        this.preferencesJson = preferencesJson;
    }

    // ─── equals(), hashCode(), toString() ─────────────────────────────────────

    /**
     * Compares this User object to another.
     *
     * @param o The object to compare to.
     * @return true if the two User objects are equal; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return userID == user.userID &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                Objects.equals(hashedPassword, user.hashedPassword) &&
                Objects.equals(preferencesJson, user.preferencesJson);
    }

    /**
     * Returns a hash code value for the User object.
     *
     * @return The hash code for this User.
     */
    @Override
    public int hashCode() {
        return Objects.hash(userID, username, email, hashedPassword, preferencesJson);
    }

    /**
     * Returns a string representation of the User object.
     *
     * @return A string representation of the User.
     */
    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", preferencesJson='" + preferencesJson + '\'' +
                '}';
    }
}