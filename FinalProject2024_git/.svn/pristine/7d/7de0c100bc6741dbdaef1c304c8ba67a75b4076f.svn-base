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
            
            StringJoiner topicIdList = new StringJoiner(",");
            for(String id : uniqueTopicIds) {
            	topicIdList.add(id);
            }
            
            String updateQuery = "Update topics set is_available = 'Y' where topic_id in (" + topicIdList + ") and is_available = 'P'";
            PreparedStatement updatePtst = conn.prepareStatement(updateQuery);
            int updateCount = updatePtst.executeUpdate();
            
            if(updateCount > 0) {
            	response.sendRedirect("adminHome.jsp?ReleasedStudentTopics");
            }else {
            	response.sendRedirect("adminHome.jsp?ReleaseStudentFailed");
            }
            
        } else if ("releaseSupervisorTopics".equals(action)) {
            //Supervisor topic release logic
            String updateQuery = "Update topics set is_available = 'P' where is_available = 'N'";
            PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
            int updateCount = updateStatement.executeUpdate();
            
            if(updateCount > 0) {
            	response.sendRedirect("adminHome.jsp?ReleasedSupervisorTopics");
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
