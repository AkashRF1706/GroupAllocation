package servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import actions.EmailNotification;
import database.MySQLConnection;

@WebServlet("/UpdateTopicStatusServlet")
public class UpdateTopicServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected synchronized void doPost(HttpServletRequest req, HttpServletResponse res) {
		int topicId = Integer.parseInt(req.getParameter("topicId"));
		String statusRequest =  req.getParameter("status");
		String status = (statusRequest.equals("Approved")) ? "P" : "R";
		String name = req.getParameter("name");
		String topicName = req.getParameter("topicName");
		
		try {
			Connection conn = MySQLConnection.getConnection();
			String updateQuery = "Update topics set is_available = ? where topic_id = ?";
			PreparedStatement updatePtst = conn.prepareStatement(updateQuery);
			updatePtst.setString(1, status);
			updatePtst.setInt(2, topicId);
			int updateCount = updatePtst.executeUpdate();
			
			if(updateCount > 0) {
				String selectQuery = "Select email from staff where staff_name = ?";
				PreparedStatement ptst = conn.prepareStatement(selectQuery);
				ptst.setString(1, name);
				ResultSet rs = ptst.executeQuery();
				
				if(rs.next()) {
					String emailId = rs.getString("email");
					String subject = "New topic Update";
					String message = "Hi,\n\nYour created topic " +topicName+ " has been " +statusRequest+ " by the admin.\n\nBest Regards,\nMSc. Project Testing";
					
					EmailNotification emailNotification = new EmailNotification();
					emailNotification.sendEmail(emailId, subject, message);
					
					res.sendRedirect("adminHome.jsp?success=Topic " + statusRequest);
                } else {
                    res.sendRedirect("adminHome.jsp?error=No email found for " + name);
                }
            } else {
                res.sendRedirect("adminHome.jsp?error=Unable to update topic status for " + topicName);
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
