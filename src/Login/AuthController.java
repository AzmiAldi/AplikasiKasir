package Login;

import database.DBConnection;
import java.sql.*;

public class AuthController {
    public static boolean login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // true jika ditemukan
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean register(String username, String password, String email) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password); // atau hash password
            ps.setString(3, email);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Registrasi gagal: " + e.getMessage());
            return false;
        }
    }
    public static boolean resetPassword(String email, String newPassword) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE users SET password = ? WHERE email = ?")) {

            ps.setString(1, newPassword);
            ps.setString(2, email);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
