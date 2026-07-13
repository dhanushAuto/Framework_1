package utilities.dao;

import utilities.common_utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmployeeDAO {

    private EmployeeDAO() {
        throw new IllegalStateException("Utility class");
    }
    public static String getEmployeeNameById(int id) throws Exception {
        String query = "SELECT name FROM employees WHERE id = ?";
        try (Connection conn = DBUtils.getConnection("mysql");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        return null;
    }

    public static void updateEmployeeNameById(int id, String newName) throws Exception {
        String query = "UPDATE employees SET name = ? WHERE id = ?";
        try (Connection conn = DBUtils.getConnection("mysql");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newName);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
}
