package actions;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import database.MySQLConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@WebServlet("/releaseTopics")
public class ReleaseTopics extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the action parameter from the POST request
    	response.setContentType("text/html");
        String action = request.getParameter("action");
        String department = request.getParameter("department");
        String dateTimeInput = request.getParameter("dateTime");
        
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeInput, inputFormatter);
        
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(outputFormatter);        
        System.out.println(formattedDateTime);
        try {
        	Connection conn = MySQLConnection.getConnection();        

        if ("releaseStudentTopics".equals(action)) {
            //Student topic release logic
        	Set<String> uniqueTopicIds = new HashSet<String>();
        	String topicQuery = "Select topics from supervisor_prefs";
        	
            PreparedStatement ptst = conn.prepareStatement(topicQuery);
            ResultSet rs = ptst.executeQuery();
            
            while(rs.next()) {
            	String topicIds = rs.getString("topics");
            	String[] ids = topicIds.split(",");
            	for(String id : ids) {
            		uniqueTopicIds.add(id.trim());
            	}
            }
            
            if (!uniqueTopicIds.isEmpty()) {
                StringJoiner topicIdList = new StringJoiner("','", "'", "'");
                uniqueTopicIds.forEach(topicIdList::add);

                String updateQuery = "UPDATE topics SET is_available = 'Y' WHERE topic_id IN (" + topicIdList + ") AND is_available = 'P' AND department = ?";
                PreparedStatement updatePtst = conn.prepareStatement(updateQuery);
                updatePtst.setString(1, department);
                int updateCount = updatePtst.executeUpdate();

                // Checking the update count to see if the rows were successfully updated
                if (updateCount > 0) {
                    // If topics are updated successfully, then update the deadline table
                    String deadlineUpdateQuery = "INSERT INTO deadlines (department, student_deadline) VALUES (?, ?) ON DUPLICATE KEY UPDATE student_deadline = ?";
                    PreparedStatement deadlineStmt = conn.prepareStatement(deadlineUpdateQuery);
                    deadlineStmt.setString(1, department);
                    deadlineStmt.setString(2, formattedDateTime); 
                    deadlineStmt.setString(3, formattedDateTime);
                    int deadlineUpdateCount = deadlineStmt.executeUpdate();

                    if (deadlineUpdateCount > 0) {
                        response.sendRedirect("adminHome.jsp?ReleasedStudentTopics");
                    } else {
                        response.sendRedirect("adminHome.jsp?ReleaseStudentFailed");
                    }
                } else {
                    response.sendRedirect("adminHome.jsp?SupTopicsNotReleased");
                }
            } else {
                response.sendRedirect("adminHome.jsp?SupNotSaved");
            }

            
        } else if ("releaseSupervisorTopics".equals(action)) {
            //Supervisor topic release logic
            String updateQuery = "Update topics set is_available = 'P' where is_available = 'N'";
            PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
            int updateCount = updateStatement.executeUpdate();
            
            if(updateCount > 0) {
            	String deadlineUpdateQuery = "INSERT INTO deadlines (department, staff_deadline) VALUES (?, ?) ON DUPLICATE KEY UPDATE staff_deadline = ?";
            	PreparedStatement deadlineStmt = conn.prepareStatement(deadlineUpdateQuery);
                deadlineStmt.setString(1, department);
                deadlineStmt.setString(2, formattedDateTime); 
                deadlineStmt.setString(3, formattedDateTime);
                int deadlineUpdateCount = deadlineStmt.executeUpdate();
                
                if(deadlineUpdateCount > 0) {
                	response.sendRedirect("adminHome.jsp?ReleasedSupervisorTopics");
                } else {
                	response.sendRedirect("adminHome.jsp?ReleaseSupervisorFailed");
				}
            }else {
            	response.sendRedirect("adminHome.jsp?ReleaseSupervisorFailed");
            }            
        }
        } catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
    }
}
