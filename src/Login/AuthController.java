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
}
