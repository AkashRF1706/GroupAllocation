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

@WebServlet("/studentAllocateServlet")
public class StudentManualAllocation extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	String username = request.getParameter("userId");
    String groupId = request.getParameter("groupId");

    try {
        // Assuming you have a DAO and a method to allocate student
        boolean updated = allocateStudentToGroup(username, groupId);
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
	
	private boolean allocateStudentToGroup(String username, String groupId) {
        
		try {
			Connection connection = MySQLConnection.getConnection();
			connection.setAutoCommit(false);
			int studentId = 0;
			
			// Step 1: Retrieve old groupId for the student
			String sqlGetOldGroupId = "SELECT group_id, student_id FROM students WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(sqlGetOldGroupId);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            String oldGroupId = null;
            if (rs.next()) {
                oldGroupId = rs.getString("group_id");
                studentId = rs.getInt("student_id");
            }
			
         // Step 2: Remove the student from the old group
            if (oldGroupId != null) {
                // SQL to remove studentId handling both cases (end or middle of the string)
                String sqlUpdateOldGroup = "UPDATE discussiongroups SET students = " +
                    "TRIM(BOTH ',' FROM REPLACE(CONCAT(',', students, ','), CONCAT(',', ?, ','), ',')) " +
                    "WHERE group_name = ?";
                PreparedStatement stmt1 = connection.prepareStatement(sqlUpdateOldGroup);
                stmt1.setInt(1, studentId);
                stmt1.setString(2, oldGroupId);
                stmt1.executeUpdate();
                
             // Step 2b: Check if the `students` column is empty and delete the row if it is
                String sqlCheckEmpty = "SELECT students FROM discussiongroups WHERE group_name = ?";
                PreparedStatement stmt4 = connection.prepareStatement(sqlCheckEmpty);
                stmt4.setString(1, oldGroupId);
                ResultSet rs1 = stmt4.executeQuery();
                if (rs1.next() && (rs1.getString("students") == null || rs1.getString("students").isEmpty())) {
                    String sqlDeleteGroup = "DELETE FROM discussiongroups WHERE group_name = ?";
                    PreparedStatement stmt5 = connection.prepareStatement(sqlDeleteGroup);
                    stmt5.setString(1, oldGroupId);
                    stmt5.executeUpdate();
                }
            }
            
         // Step 3: Add the student to the new group	
            String sqlUpdateNewGroup = "UPDATE discussiongroups SET students = " +
                    "CONCAT(IF(TRIM(students) != '', CONCAT(TRIM(students), ','), ''), ?) WHERE group_name = ?";
                PreparedStatement stm2 = connection.prepareStatement(sqlUpdateNewGroup);
                stm2.setInt(1, studentId);
                stm2.setString(2, groupId);
                stm2.executeUpdate();
                
             // Step 4: Update the student's groupId in the students table
                String sqlUpdateStudentGroup = "UPDATE students SET group_id = ? WHERE student_id = ?";
                PreparedStatement stmt3 = connection.prepareStatement(sqlUpdateStudentGroup);
                stmt3.setString(1, groupId);
                stmt3.setInt(2, studentId);
                stmt3.executeUpdate();
                
                connection.commit();
                return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
    }

}
