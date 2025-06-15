package database;

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
                    password TEXT NOT NULL,
                    role TEXT DEFAULT 'cashier'
                )
                """;

            String createMenuItemsTable = """
                CREATE TABLE IF NOT EXISTS menu_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    price REAL NOT NULL,
                    stock INTEGER DEFAULT 0
                )
                """;

            String createSalesTable = """
                CREATE TABLE IF NOT EXISTS sales (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    item_id INTEGER,
                    quantity INTEGER,
                    sale_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (item_id) REFERENCES menu_items(id)
                )
                """;

            String createShiftsTable = """
                CREATE TABLE IF NOT EXISTS shifts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    start_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                    end_time DATETIME,
                    cashier TEXT,
                    total_sales REAL DEFAULT 0.0
                )
                """;

            stmt.execute(createUsersTable);
            stmt.execute(createMenuItemsTable);
            stmt.execute(createSalesTable);
            stmt.execute(createShiftsTable);

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
                    stmt.execute("INSERT INTO menu_items (name, price, stock) VALUES ('Espresso', 2.50, 10)");
                    stmt.execute("INSERT INTO menu_items (name, price, stock) VALUES ('Latte', 3.00, 5)");
                    stmt.execute("INSERT INTO menu_items (name, price, stock) VALUES ('Cappuccino', 3.00, 0)");
                    stmt.execute("INSERT INTO menu_items (name, price, stock) VALUES ('Mocha', 3.50, 3)");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}