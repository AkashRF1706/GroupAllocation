package servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson; // Google's JSON library

import database.MySQLConnection;

@WebServlet("/AddNewTopicServlet")
public class AddNewTopicServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String topicName = request.getParameter("topicName");
        String department = session.getAttribute("department").toString();
        String supervisorName = request.getParameter("supervisorName");
        PrintWriter out = response.getWriter();

        if (topicName == null || topicName.isEmpty()) {
            out.print("{\"message\": \"Topic name cannot be empty\"}");
            return;
        }

        try (Connection conn = MySQLConnection.getConnection()) {
            if (!topicExists(conn, topicName, department)) {
                addNewTopic(conn, topicName, department, supervisorName);
                List<String> topics = fetchTopics(conn, department);
                System.out.println(topics.toString());
                String json = new Gson().toJson(topics);
                out.print(json);
            } else {
                out.print("{\"message\": \"Topic already exists\"}");
            }
        } catch (Exception e) {
            out.print("{\"message\": \"Error adding new topic: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    private List<String> fetchTopics(Connection conn, String department) throws SQLException {
        List<String> topics = new ArrayList<>();
        String query = "SELECT topic_name FROM topics WHERE is_available = 'P' and department = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        	pstmt.setString(1, department);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                topics.add(rs.getString("topic_name"));
            }
        }
        return topics;
    }

    private boolean topicExists(Connection conn, String topicName, String department) throws SQLException {
        String query = "SELECT COUNT(*) FROM topics WHERE topic_name = ? AND DEPARTMENT = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, topicName);
            pstmt.setString(2, department);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    private void addNewTopic(Connection conn, String topicName, String department, String supervisorName) throws SQLException {
        String insertQuery = "INSERT INTO topics (topic_name, is_available, department, created_by) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, topicName);
            pstmt.setString(2, "S");
            pstmt.setString(3, department);
            pstmt.setString(4, supervisorName);
            pstmt.executeUpdate();
        }
    }
}
