package servlet;

import javax.servlet.*;
import javax.servlet.http.*;

import database.MySQLConnection;

import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/GetEmailsServlet")
public class GetEmailServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String department = request.getParameter("department");
        List<String> emails = getEmailsForDepartment(department);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new com.google.gson.Gson().toJson(emails));
    }

    private List<String> getEmailsForDepartment(String department) {
        List<String> emailList = new ArrayList<String>();
        
        try {
        	Connection conn = MySQLConnection.getConnection();
        	String query = "(SELECT email FROM students WHERE department = ?) UNION (SELECT email FROM staff WHERE department = ?)";
        	PreparedStatement ptst = conn.prepareStatement(query);
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
