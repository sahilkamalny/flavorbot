package edu.farmingdale.recipegenerator;

import java.sql.*;
import java.util.*;
import edu.farmingdale.recipegenerator.User;
import edu.farmingdale.recipegenerator.SessionManager;

/**
 * AzureDBConnector is responsible for interacting with the Azure MySQL database
 * used by the Recipe Generator application. It handles user authentication,
 * preference storage, fridge inventory snapshots, and user management.
 *
 * This class uses JDBC to connect to the Azure MySQL database and perform SQL operations.
 *
 * Tables involved:
 * - users
 * - fridge_configs
 *
 * NOTE: Ensure that the `SessionManager` and `User` classes are correctly defined.
 */
public class AzureDBConnector {
    private static final String DB_URL      =
            "jdbc:mysql://csc311.mysql.database.azure.com/flavor_test?useSSL=true";
    private static final String USERNAME    = "super_admin";
    private static final String PASSWORD    = "ThisIsAPassword1";


    /**
     * Establishes and returns a live database connection to the Azure MySQL server.
     *
     * @return Connection object to the database
     * @throws SQLException if connection fails
     */
    // 1) Single place to get a live Connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }


    /**
     * Retrieves user information by username.
     *
     * @param username the username to search
     * @return Map of user fields or null if not found
     */
    // 3) Fetch a single user by username
    public Map<String,Object> getUserByUsername(String username) {
        String sql = ""
                + "SELECT userID, username, email, hashed_password, preferences "
                + "  FROM users "
                + " WHERE username = ?";
        try (Connection        conn = getConnection();
             PreparedStatement ps   = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String,Object> user = new HashMap<>();
                    user.put("userID",         rs.getInt("userID"));
                    user.put("username",       rs.getString("username"));
                    user.put("email",          rs.getString("email"));
                    user.put("hashed_password",rs.getString("hashed_password"));
                    user.put("preferences",    rs.getString("preferences"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Lists all users in the database.
     *
     * @return List of user maps containing user fields
     */
    // 4) List all users
    public List<Map<String,Object>> listAllUsers() {
        String sql = ""
                + "SELECT userID, username, email, hashed_password, preferences "
                + "  FROM users";
        List<Map<String,Object>> users = new ArrayList<>();
        try (Connection        conn = getConnection();
             PreparedStatement ps   = conn.prepareStatement(sql);
             ResultSet          rs   = ps.executeQuery()) {
            while (rs.next()) {
                Map<String,Object> u = new HashMap<>();
                u.put("userID",         rs.getInt("userID"));
                u.put("username",       rs.getString("username"));
                u.put("email",          rs.getString("email"));
                u.put("hashed_password",rs.getString("hashed_password"));
                u.put("preferences",    rs.getString("preferences"));
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Inserts a new user into the users table.
     *
     * @param username       the username
     * @param email          the user's email
     * @param hashedPassword the hashed password
     */
    // 5) Insert a brand‑new user
    public void insertUser(String username, String email, String hashedPassword) {
        String sql = ""
                + "INSERT INTO users(username, email, hashed_password) "
                + "VALUES (?,?,?)";
        try (Connection        conn = getConnection();
             PreparedStatement ps   = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hashedPassword);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // —— Preference Methods —— //

    /**
     * Retrieves the JSON preferences for a given user.
     *
     * @param userId ID of the user
     * @return JSON string of preferences
     */
    // 6) Load the JSON blob of prefs for one user
    public String getUserPreferences(int userId) {
        String sql = "SELECT preferences FROM users WHERE userID = ?";
        try (Connection        conn = getConnection();
             PreparedStatement ps   = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("preferences");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "{}";
    }


    /**
     * Updates the user preferences in JSON format.
     *
     * @param userId    ID of the user
     * @param prefsJson JSON string of preferences
     */
    public void updateUserPreferences(int userId, String prefsJson) {
        String sql = ""
                + "UPDATE users "
                + "   SET preferences = ? "
                + " WHERE userID      = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps   = conn.prepareStatement(sql)) {
            ps.setString(1, prefsJson);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        refreshSession();
    }

    // —— Fridge‑config Methods (JSON‑blob approach) —— //

    /**
     * Lists all fridge configurations for a given user.
     *
     * @param userId ID of the user
     * @return List of fridge config maps (id, name, created_at)
     */
    // 8) Create a new “saved fridge” snapshot and return its generated ID
    public int createFridgeConfig(int userId, String name, String itemsJson) {
        String sql = ""
                + "INSERT INTO fridge_configs(user_id, name, items) "
                + "VALUES (?,?,?)";
        try (Connection           conn = getConnection();
             PreparedStatement    ps   = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setString(3, itemsJson);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Lists all fridge configurations for a given user.
     *
     * @param userId ID of the user
     * @return List of fridge config maps (id, name, created_at)
     */
    // 9) List all fridge‑snapshots for a user (id + name + timestamp)
    public List<Map<String,Object>> listFridgeConfigs(int userId) {
        String sql = ""
                + "SELECT id, name, created_at "
                + "  FROM fridge_configs "
                + " WHERE user_id = ?";
        List<Map<String,Object>> configs = new ArrayList<>();
        try (Connection        conn = getConnection();
             PreparedStatement ps   = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String,Object> c = new HashMap<>();
                    c.put("id",         rs.getInt("id"));
                    c.put("name",       rs.getString("name"));
                    c.put("created_at", rs.getTimestamp("created_at"));
                    configs.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return configs;
    }

    /**
     * Retrieves the items JSON blob for a given fridge configuration.
     *
     * @param configId ID of the fridge configuration
     * @return JSON string of fridge items
     */
    // 10) Fetch the JSON “items” blob for one saved fridge
    public String getFridgeConfigItems(int configId) {
        String sql = "SELECT items FROM fridge_configs WHERE id = ?";
        try (Connection        conn = getConnection();
             PreparedStatement ps   = conn.prepareStatement(sql)) {
            ps.setInt(1, configId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("items");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "[]";
    }

    /**
     * Authenticates the user using hashed password and sets the session if valid.
     *
     * @param username             the username
     * @param providedPasswordHash the hashed password to check
     * @return true if authentication is successful
     */
    public boolean authenticateAndSetSession(String username, String providedPasswordHash) {
        // 1) fetch DB record as a Map
        Map<String,Object> userMap = getUserByUsername(username);
        if (userMap == null) {
            return false;  // user not found
        }

        // 2) compare hashes
        String storedHash = (String) userMap.get("hashed_password");
        if (!storedHash.equals(providedPasswordHash)) {
            return false;  // bad password
        }

        // 3) build a User object and set it in the session
        User user = new User(
                (Integer)   userMap.get("userID"),
                (String)    userMap.get("username"),
                (String)    userMap.get("email"),
                storedHash,
                (String)    userMap.get("preferences")
        );
        SessionManager.getInstance().setCurrentUser(user);
        return true;

    }

    /**
     * Refreshes the current session with the latest data from the database.
     *
     * @return true if session is refreshed, false otherwise
     */
    public boolean refreshSession() {
        // 1) Grab the current user from session
        User current = SessionManager.getInstance().getCurrentUser();
        if (current == null) {
            return false;     // no session to refresh
        }

        // 2) Fetch the fresh record by userID
        String userID = current.getUsername();
        Map<String,Object> userMap = getUserByUsername(userID);
        if (userMap == null) {
            return false;     // user no longer exists?
        }

        // 3) Rebuild and replace the User object
        User updated = new User(
                (Integer) userMap.get("userID"),
                (String)  userMap.get("username"),
                (String)  userMap.get("email"),
                (String)  userMap.get("hashed_password"),
                (String)  userMap.get("preferences")
        );
        SessionManager.getInstance().setCurrentUser(updated);
        return true;
    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                // if we get any row back, the username is taken
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // you may want to rethrow or handle more gracefully
            return false;
        }
    }

    /**
     * Retrieves all fridge item names for a user.
     *
     * @param userId ID of the user
     * @return List of item names
     */
    public List<String> getFridgeItems(int userId) {
        String sql = "SELECT name FROM fridge_configs WHERE user_id = ?";
        List<String> items = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Adds a fridge item (by name) for a user.
     *
     * @param userId ID of the user
     * @param name   item name
     * @return true if item is added successfully
     */
    public boolean addFridgeItem(int userId, String name) {

        String sql = "INSERT INTO fridge_configs (user_id, name, created_at) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();

             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0; // true if it worked

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Deletes a fridge item by name for a user.
     *
     * @param userId ID of the user
     * @param name   item name to delete
     * @return true if deletion was successful
     */
    public boolean deleteFridgeItemByName(int userId, String name) {
        String sql = "DELETE FROM fridge_configs WHERE user_id = ? AND name = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, name);

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Updates a fridge item name for a user.
     *
     * @param userId  ID of the user
     * @param oldName current item name
     * @param newName new item name
     * @return true if update was successful
     */
    public boolean updateFridgeItemByName(int userId, String oldName, String newName) {
        String sql = "UPDATE fridge_configs SET name = ?, created_at = ? WHERE user_id = ? AND name = ?";
        Timestamp newCreatedAt = new Timestamp(System.currentTimeMillis());

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newName);
            ps.setTimestamp(2, newCreatedAt);
            ps.setInt(3, userId);
            ps.setString(4, oldName);

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}