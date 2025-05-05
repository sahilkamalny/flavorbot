package edu.farmingdale.recipegenerator;

import java.sql.*;
import java.util.*;
import edu.farmingdale.recipegenerator.User;
import edu.farmingdale.recipegenerator.SessionManager;


public class AzureDBConnector {
    private static final String DB_URL      =
            "jdbc:mysql://csc311.mysql.database.azure.com/flavor_test?useSSL=true";
    private static final String USERNAME    = "super_admin";
    private static final String PASSWORD    = "ThisIsAPassword1";

    // 1) Single place to get a live Connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }


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
}