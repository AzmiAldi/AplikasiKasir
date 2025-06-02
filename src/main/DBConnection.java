package main;

import java.sql.*;

public class DBConnection {
    private static Connection conn;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                String url = "jdbc:sqlite:stello_coffee.db";
                conn = DriverManager.getConnection(url);
                initializeDatabase();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    // Initialize tables if not exist
    private static void initializeDatabase() throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                )
                """;

            String createMenuItemsTable = """
                CREATE TABLE IF NOT EXISTS menu_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    price REAL NOT NULL
                )
                """;

            stmt.execute(createUsersTable);
            stmt.execute(createMenuItemsTable);

            // Insert default admin user if none exists
            try (PreparedStatement ps = getConnection().prepareStatement(
                    "INSERT OR IGNORE INTO users (username, password) VALUES (?, ?)")) {
                ps.setString(1, "admin");
                ps.setString(2, "admin123"); // In real app, hash passwords!
                ps.executeUpdate();
            }

            // Insert sample menu items if table is empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM menu_items")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO menu_items (name, price) VALUES ('Espresso', 2.50)");
                    stmt.execute("INSERT INTO menu_items (name, price) VALUES ('Latte', 3.00)");
                    stmt.execute("INSERT INTO menu_items (name, price) VALUES ('Cappuccino', 3.00)");
                    stmt.execute("INSERT INTO menu_items (name, price) VALUES ('Mocha', 3.50)");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}