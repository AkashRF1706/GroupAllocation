package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Supervisor;

public class SupervisorsDAO {

	public List<Supervisor> getAllSupervisors(String department) {
	    List<Supervisor> supervisorsList = new ArrayList<>();
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

	        // Fetching all supervisors and their preferences
	        String query = "SELECT s.staff_name, s.username, s.department, sp.numgroups, sp.topics "
	                + " FROM staff s "
	                + " JOIN supervisor_prefs sp ON s.staff_id = sp.staff_id "
	                + " WHERE s.department = ? AND s.staff_id != 1 "
	                + " ORDER BY s.staff_id";
	        PreparedStatement ptst = conn.prepareStatement(query);
	        ptst.setString(1, department);
	        ResultSet rs = ptst.executeQuery();

	        while(rs.next()) {
	            String userName = rs.getString("username");
	            String staffName = rs.getString("staff_name");
	            String staffDepartment = rs.getString("department");
	            int numGroups = rs.getInt("numgroups");
	            String preferences = rs.getString("topics");

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

	            // Create a new Supervisor object and set preferences
	            Supervisor supervisor = new Supervisor(userName, staffName, staffDepartment);
	            supervisor.setNumGroups(numGroups);
	            supervisor.setPreferences(preferenceNames);
	            supervisorsList.add(supervisor);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return supervisorsList;
	}

}
