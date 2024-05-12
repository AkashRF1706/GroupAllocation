package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.MySQLConnection;

@WebServlet("/supervisorAllocateServlet")
public class SupervisorManualAllocation extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	String username = request.getParameter("userId");
    String groupId = request.getParameter("groupId");
    String supervisorSecondMarker = request.getParameter("supervisorSecondMarker");

    try {
        // Assuming you have a DAO and a method to allocate student
        boolean updated = allocateStaffToGroup(username, groupId, supervisorSecondMarker);
        if (updated) {
            response.getWriter().write("Student allocated successfully");
        } else {
            response.getWriter().write("Allocation failed");
        }
    } catch (Exception e) {
        e.printStackTrace();
        response.getWriter().write("Error in allocation: " + e.getMessage());
    }
	}
	
	private boolean allocateStaffToGroup(String username, String groupId, String supervisorSecondMarker) {
        
		try {
			Connection connection = MySQLConnection.getConnection();
			connection.setAutoCommit(false);
			int staffId = 0;
			String columnName = (supervisorSecondMarker.equals("Supervisor")) ? "supervisor_id" : "second_marker";
			
			// Step 1: Retrieve old groupId for the student
			String sqlGetOldGroupId = "SELECT group_id, staff_id FROM staff WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(sqlGetOldGroupId);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            String oldGroupId = null;
            if (rs.next()) {
                oldGroupId = rs.getString("group_id");
                staffId = rs.getInt("staff_id");
            }
			
         // Step 2: Add the staff to the new group
            if (oldGroupId != null) {
                String sqlUpdateNewGroup = "UPDATE discussiongroups SET "+columnName+" = ? WHERE group_name = ?";
                PreparedStatement stmt1 = connection.prepareStatement(sqlUpdateNewGroup);
                stmt1.setInt(1, staffId);
                stmt1.setString(2, groupId);
                stmt1.executeUpdate();
            }
                
            if(supervisorSecondMarker.equals("Supervisor")) {
             // Step 3: Update the staff groupId in the staff table
                String sqlUpdateStaffGroup = "UPDATE staff SET group_id = CONCAT(IFNULL(TRIM(group_id), ''), IF(TRIM(group_id) = '' OR group_id IS NULL, '', ','), ?) WHERE staff_id = ?";
                PreparedStatement stmt3 = connection.prepareStatement(sqlUpdateStaffGroup);
                stmt3.setString(1, groupId);
                stmt3.setInt(2, staffId);
                stmt3.executeUpdate();
            }  
                
                connection.commit();
                return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
    }

}
