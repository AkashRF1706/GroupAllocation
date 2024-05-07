package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Group;

public class GroupsDAO {
    
    private Connection connection;
    
    public GroupsDAO() {
        try {
			connection = MySQLConnection.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public List<Group> getAllGroups(String department) {
        List<Group> groups = new ArrayList<>();
        
        try {
            String query = "SELECT g.group_name, "
            		+ " (SELECT GROUP_CONCAT(s.student_name ORDER BY FIND_IN_SET(s.student_id, g.students) ASC SEPARATOR ', ') "
            		+ "  FROM students s "
            		+ "  WHERE FIND_IN_SET(s.student_id, g.students) > 0 AND s.department = ?) AS StudentNames, "
            		+ " (SELECT staff_name FROM staff WHERE staff_id = g.second_marker) AS SecondMarker, "
            		+ "  (SELECT staff_name FROM staff WHERE staff_id = g.supervisor_id) AS Supervisor, "
            		+ "  t.topic_name "
            		+ "	FROM discussiongroups g "
            		+ " LEFT JOIN topics t ON t.topic_id = CAST(SUBSTRING(g.group_name FROM 2 FOR POSITION('-' IN g.group_name) - 2) AS UNSIGNED) "
            		+ " WHERE EXISTS (SELECT 1 FROM students s WHERE FIND_IN_SET(s.student_id, g.students) > 0 AND s.department = ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, department);
            statement.setString(2, department);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                String groupName = resultSet.getString("group_name");
                String secondMarkerName = resultSet.getString("SecondMarker");
                String studentNames = resultSet.getString("StudentNames");
                String supervisorName = resultSet.getString("Supervisor");
                String topicName = resultSet.getString("topic_name");
                String[] students = studentNames.split(", ");
                
                Group group = new Group(groupName, supervisorName, secondMarkerName, topicName, students);
                groups.add(group);
            }
            
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error in executing query: " +e.getMessage());
        }
        
        return groups;
    }
    
    public List<String> getStudentAndSupervisorEmails (String department){
    	List<String> emailList = new ArrayList<String>();
    	
    	try {
    		
    		String sql = "(SELECT email FROM students WHERE group_id IS NOT NULL AND department = ?) UNION (SELECT email FROM staff WHERE group_id IS NOT NULL AND department = ?)";
    		PreparedStatement ptst = connection.prepareStatement(sql);
    		ptst.setString(1, department);
    		ptst.setString(2, department);
    		ResultSet rs = ptst.executeQuery();
    		
    		while(rs.next()) {
    			String email = rs.getString("email");
    			emailList.add(email);
    		}
    		
    	} catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
    	return emailList;
    }
}