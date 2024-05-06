<%@page import="com.mysql.cj.protocol.ResultStreamer"%>
<%@page import="java.sql.*, java.util.*, java.util.ArrayList"%>
<%@page import="database.MySQLConnection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Supervisor Saved Preferences</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="css/landing.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <style>
        body { background-color: #f5f5f5; }
        .container { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
    </style>
</head>
<body>
<%
String groupId = null;
if(session.getAttribute("groupId") != null){
	groupId = session.getAttribute("groupId").toString();
}
if (request.getParameter("saved") != null) {
    out.println("<script>alert('Preferences saved successfully');</script>");  
} %>
<div id="wrapper">
<a href="#" class="btn btn-secondary" id="menu-toggle"><i class="fas fa-bars"></i></a>
    <!-- Sidebar -->
    <div id="sidebar-wrapper">
        <ul class="sidebar-nav">
            <li><a href="supervisorHome.jsp">Home</a></li>
            <li><a href="supervisorSavedPreferences.jsp" class="active">Saved Preferences</a></li>
            <%if(groupId != null){ %>
        <li><a href="groupChat.jsp">Chat Home</a></li>
        <%} %>
            <li><a href="logout.jsp">Logout</a></li>
        </ul>
    </div>
    <!-- Page content -->
    <div id="page-content-wrapper">
        <div class="container">
            <h1>Saved Preferences</h1>
<%
String username = session.getAttribute("username").toString();
    List<Integer> topicsList = new ArrayList<>();
    Map<Integer, String> topicNames = new HashMap<Integer, String>();
    String numGroups = "0";
    try (Connection conn = MySQLConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement("SELECT numgroups, topics FROM supervisor_prefs WHERE username = ?")) {
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            numGroups = rs.getString("numgroups");
           	String topics = rs.getString("topics");
           	String[] ids = topics.split(",");
           	for(String id : ids){
           		topicsList.add(Integer.parseInt(id.trim()));
           	}
        }
        
        String topicId = null;
        if(groupId != null){
        	String[] groupIdSplit = groupId.split("-");
        	if(groupIdSplit.length > 1){
        		topicId = groupIdSplit[0].substring(1);
        	}
        }
        
        StringBuilder sql = new StringBuilder("SELECT topic_id, topic_name FROM topics WHERE topic_id IN (");
        for (int i = 0; i < topicsList.size(); i++) {
            sql.append("?");
            if (i < topicsList.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        try (PreparedStatement pstmt2 = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < topicsList.size(); i++) {
                pstmt2.setInt(i + 1, topicsList.get(i));
            }

            ResultSet rs2 = pstmt2.executeQuery();
            while (rs2.next()) {
                topicNames.put(rs2.getInt("topic_id"), rs2.getString("topic_name"));
            }
        } 
        System.out.println(topicId);
        System.out.println(topicNames.get(Integer.parseInt(topicId)));
%>
                        <table class="table">
                <thead>
                    <tr>
                        <th>Number of Groups</th>
                        <th>Topics</th>
                        <%if(topicId != null){ %>
							<th>Allocated Topic Group</th>
							<%} %>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><%= numGroups %></td>
                        <td>
                            <ul>
                            <% for(Map.Entry<Integer, String> topic : topicNames.entrySet()) { %>
                                <li><%= topic.getValue() %></li>
                            <% } %>
                            </ul>
                        </td>
                        <%if(topicId != null){ %>
                        <td><%=groupId %> - <%=topicNames.get(Integer.parseInt(topicId)) %></td>
                        <%} %>
                    </tr>
                </tbody>
            </table>
            <%} catch (SQLException e) {
                e.printStackTrace();
            } %>
                    </div>
    </div>
</div>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
<script src="js/sidebar.js"></script>
</body>
</html>
            