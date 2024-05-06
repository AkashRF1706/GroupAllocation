package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Student;

public class StudentsDAO {

	public List<Student> getAllStudents(String department) {
	    List<Student> studentsList = new ArrayList<>();
	    Map<String, String> topicIdToNameMap = new HashMap<>();

	    try {
	        Connection conn = MySQLConnection.getConnection();

	        // First, fetch all topic names and their IDs
	        String topicsQuery = "SELECT topic_id, topic_name FROM topics";
	        PreparedStatement topicsStmt = conn.prepareStatement(topicsQuery);
	        ResultSet topicsRs = topicsStmt.executeQuery();
	        while (topicsRs.next()) {
	            topicIdToNameMap.put(topicsRs.getString("topic_id"), topicsRs.getString("topic_name"));
	        }

	        // Now, fetch all students and their preferences
	        String query = "SELECT s.student_name, s.username, s.department, p.preferences "
	                + " FROM students s "
	                + " JOIN preferences p ON s.student_id = p.student_id "
	                + " WHERE s.department = ? AND s.student_id != 3 "
	                + " ORDER BY s.student_id";
	        PreparedStatement ptst = conn.prepareStatement(query);
	        ptst.setString(1, department);
	        ResultSet rs = ptst.executeQuery();

	        while(rs.next()) {
	            String userName = rs.getString("username");
	            String studentName = rs.getString("student_name");
	            String studentDepartment = rs.getString("department");
	            String preferences = rs.getString("preferences");

	            // Split the preferences string into an array
	            String[] preferenceIds = preferences.split(",");

	            // Convert preference IDs to names using the map
	            List<String> preferenceNames = new ArrayList<>();
	            for (String id : preferenceIds) {
	                if (topicIdToNameMap.containsKey(id)) {
	                    preferenceNames.add(topicIdToNameMap.get(id));
	                } else {
	                    preferenceNames.add("Unknown Topic");  // Handle missing topics
	                }
	            }

	            // Create a new Student object and set preferences
	            Student student = new Student(userName, studentName, studentDepartment);
	            student.setPreferences(preferenceNames);
	            studentsList.add(student);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return studentsList;
	}

}
