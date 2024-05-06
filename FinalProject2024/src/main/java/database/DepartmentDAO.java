package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public List<String> getAllDepartmentsWithPreferences() {
        List<String> departments = new ArrayList<>();
        
        String query = "SELECT DISTINCT s.department FROM students s JOIN preferences p ON s.student_id = p.student_id";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                departments.add(rs.getString("department"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }
    
    public List<String> getAllDepartmentsForTopic(){

        List<String> departments = new ArrayList<>();
        
        String query = "SELECT DISTINCT department FROM topics";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                departments.add(rs.getString("department"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    
    }
}
