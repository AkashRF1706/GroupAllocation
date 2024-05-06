package actions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import database.MySQLConnection;
import model.User;

@WebServlet("/LoginServlet")
public class LoginAction extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
        	Connection conn = MySQLConnection.getConnection();
        	
        	String query = "SELECT l.role, l.password, l.status, x.department, x.name, x.id, x.group_id "
        			+ " FROM finalproject.login l "
        			+ " JOIN ( "
        			+ " SELECT 'student' AS role, student_name as name, student_id as id, username, department, group_id FROM finalproject.students "
        			+ " UNION ALL\r\n"
        			+ " SELECT 'staff' AS role, staff_name as name, staff_id as id, username, department, group_id FROM finalproject.staff "
        			+ " ) x ON l.username = x.username "
        			+ " WHERE l.username = ? ";
        	
        	PreparedStatement ptst = conn.prepareStatement(query);
        	
        	ptst.setString(1, username);
        	
        	ResultSet rs = ptst.executeQuery();
        	if(rs.next()) {
        		String role = rs.getString("role");
        		String hashedPassword = rs.getString("password");
        		String status = rs.getString("status");
        		String department = rs.getString("department");
        		String name = rs.getString("name");
        		String groupId = rs.getString("group_id");
        		int id = rs.getInt("id");
        		
        		if(status.equalsIgnoreCase("Active")) {
	        		if(BCrypt.checkpw(password, hashedPassword)) { //Checking user entered password with the hashed password in DB
	        			HttpSession session = request.getSession(true);
						User user = new User(username);
	        			session.setAttribute("username", username);
	        			session.setAttribute("department", department);
	        			session.setAttribute("Name", name);
	        			session.setAttribute("id", id);
	        			session.setAttribute("groupId", groupId);
	        			session.setAttribute("role", role);
	                    
	        			if(role.equalsIgnoreCase("S")) {
	        				response.sendRedirect("studentHome.jsp");
	        			} else if(role.equalsIgnoreCase("P")) {
	        				response.sendRedirect("supervisorHome.jsp");
	        			} else if(role.equalsIgnoreCase("A")) {
	        				response.sendRedirect("adminHome.jsp");
	        			}
	        		} else {
	        			request.setAttribute("error", "Incorrect password"); //If password does not match
	        			request.getRequestDispatcher("login.jsp").forward(request, response);
	        		}
        		} else {
        			request.setAttribute("error", "Your account is inactive. Please contact administrator."); //Inactive status
        			request.getRequestDispatcher("login.jsp").forward(request, response);
        		}
        	} else {
        		request.setAttribute("error", "Username does not exist"); //If username does not exist in DB
        		request.getRequestDispatcher("login.jsp").forward(request, response);
        	}
        	
        } catch (SQLException e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
}
}
