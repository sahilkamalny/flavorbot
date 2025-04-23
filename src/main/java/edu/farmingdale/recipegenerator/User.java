package edu.farmingdale.recipegenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an application user.
 */
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int    userID;
    private String username;
    private String email;
    private String hashedPassword;
    private String preferencesJson;  // JSON blob of user prefs

    public User() {
        // no-arg ctor for frameworks/serialization
    }

    public User(int userID, String username, String email, String hashedPassword, String preferencesJson) {
        this.userID          = userID;
        this.username        = username;
        this.email           = email;
        this.hashedPassword  = hashedPassword;
        this.preferencesJson = preferencesJson;
    }

    // ─── Getters & Setters ──────────────────────────────────────────────────────

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getPreferencesJson() {
        return preferencesJson;
    }

    public void setPreferencesJson(String preferencesJson) {
        this.preferencesJson = preferencesJson;
    }

    // ─── equals(), hashCode(), toString() ─────────────────────────────────────

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

    @Override
    public int hashCode() {
        return Objects.hash(userID, username, email, hashedPassword, preferencesJson);
    }

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