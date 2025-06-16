package database;

import java.sql.*;
import java.util.*;

import static database.DBConnection.getConnection;

public class ShiftDatabaseHelper {

    public static List<Map<String, Object>> getAllShifts() {
        List<Map<String, Object>> shifts = new ArrayList<>();

        String query = "SELECT * FROM shifts ORDER BY start_time DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("start_time", rs.getTimestamp("start_time"));
                row.put("end_time", rs.getTimestamp("end_time"));
                row.put("cashier", rs.getString("cashier"));
                row.put("total_sales", rs.getDouble("total_sales"));
                shifts.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return shifts;
    }

    public static Map<String, Object> getActiveShift(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, cashier, start_time FROM shifts WHERE end_time IS NULL")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> shift = new HashMap<>();
                shift.put("id", rs.getInt("id"));
                shift.put("cashier", rs.getString("cashier"));
                shift.put("start_time", rs.getString("start_time"));
                return shift;
            }
            return null;
        }
    }

    public static boolean startNewShift(Connection conn, String cashierName) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO shifts (start_time, cashier) VALUES (datetime('now', 'localtime'), ?)")) {
            ps.setString(1, cashierName);
            return ps.executeUpdate() > 0;
        }
    }

    public static int getActiveShiftId(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM shifts WHERE end_time IS NULL")) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }

    public static boolean isShiftExist(Connection conn, int shiftId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM shifts WHERE id = ?")) {
            ps.setInt(1, shiftId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public static void updateShiftSales(Connection conn, int shiftId, double amount) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE shifts SET total_sales = total_sales + ? WHERE id = ?")) {
            ps.setDouble(1, amount);
            ps.setInt(2, shiftId);
            ps.executeUpdate();
        }
    }

    public static boolean hasActiveShift(Connection conn) throws SQLException {
        return getActiveShiftId(conn) != -1;
    }

    public static double endShift(Connection conn, int shiftId) throws SQLException {
        try {
            // Dapatkan total penjualan sebelum mengakhiri shift
            double totalSales = 0;
            try (PreparedStatement psGetTotal = conn.prepareStatement(
                    "SELECT total_sales FROM shifts WHERE id = ?")) {
                psGetTotal.setInt(1, shiftId);
                ResultSet rs = psGetTotal.executeQuery();
                if (rs.next()) {
                    totalSales = rs.getDouble("total_sales");
                }
            }

            // Update end_time untuk shift ini
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE shifts SET end_time = datetime('now', 'localtime') WHERE id = ?")) {
                ps.setInt(1, shiftId);
                ps.executeUpdate();
            }

            return totalSales;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
}
