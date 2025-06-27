import java.sql.*;
import java.io.File;

public class DatabaseManager {
    private static final String DB_FILENAME = "indrive.db";
    private static final String DB_PATH = System.getProperty("user.dir") + File.separator + DB_FILENAME;
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    public static void initializeDatabase() {
        System.out.println("🟡 Connecting to SQLite DB at: " + DB_PATH);
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            if (conn != null) {
                System.out.println("✅ Connected to the SQLite database.");

                String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT NOT NULL UNIQUE," +
                        "password TEXT NOT NULL);";

                String createDriversTable = "CREATE TABLE IF NOT EXISTS drivers (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT NOT NULL UNIQUE," +
                        "password TEXT NOT NULL);";

                stmt.execute(createUsersTable);
                stmt.execute(createDriversTable);
                System.out.println("✅ Tables 'users' and 'drivers' created or already exist.");
            }
        } catch (SQLException e) {
            System.out.println("❌ DB Error: " + e.getMessage());
        }
    }

    public static boolean registerUser(String table, String username, String password) {
        String sql = "INSERT INTO " + table + " (username, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("✅ Registered " + table + " successfully: " + username);
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean login(String table, String username, String password) {
        String sql = "SELECT * FROM " + table + " WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            boolean found = rs.next();
            if (found) {
                System.out.println("✅ Login successful: " + username);
            } else {
                System.out.println("⚠️ Login failed: " + username);
            }
            return found;
        } catch (SQLException e) {
            System.out.println("❌ Login error: " + e.getMessage());
            return false;
        }
    }
}
