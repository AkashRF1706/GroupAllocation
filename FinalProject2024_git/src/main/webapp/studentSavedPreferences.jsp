<%@page import="com.mysql.cj.protocol.Resultset"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page
	import="java.sql.Connection, java.sql.PreparedStatement, java.sql.ResultSet, java.util.ArrayList, database.MySQLConnection, java.util.HashMap, java.util.Map, model.User, java.util.List, 
java.util.ArrayList, java.util.Arrays, java.util.Collections"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Saved Preferences</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
<link rel="stylesheet" href="css/landing.css">
<style>
body {
	background-color: #f5f5f5;
}

.container {
	background: white;
	padding: 20px;
	border-radius: 8px;
	box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

table {
	width: 100%;
	margin-top: 20px;
	border-collapse: collapse;
}

th, td {
	padding: 8px;
	text-align: left;
	border-bottom: 1px solid #ddd;
}

th {
	background-color: #f2f2f2;
}
</style>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
</head>
<body>
<%String group_id = null;
if(session.getAttribute("groupId")  != null){
	group_id = session.getAttribute("groupId").toString();
}

%>
	<% if (request.getParameter("saved") != null) {%>
	<script>alert('Preferences saved successfully');</script>
	<% } %>
	<div id="wrapper">
		<a href="#" class="btn btn-secondary" id="menu-toggle"><i
			class="fas fa-bars"></i></a>
		<!-- Sidebar -->
		<div id="sidebar-wrapper">
			<ul class="sidebar-nav">
				<li><a href="studentHome.jsp">Home</a></li>
				<li><a href="studentSavedPreferences.jsp" class="active"%>Saved
						Preferences</a></li>
						<%if(group_id != null){ %>
				<li><a href="groupChat.jsp">Chat Home</a></li>
				<%} %>
				<li><a href="#" data-toggle="modal" onclick="showLogoutModal()">Logout</a></li>
			</ul>
		</div>

		<!-- Page Content -->
		<div id="page-content-wrapper">
			<div class="container">
				<h1>Your Saved Preferences</h1>
				<% 
            int student_id = (Integer) session.getAttribute("id");
            String preferenceString = "";
                
                try (Connection conn = MySQLConnection.getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement("SELECT preferences FROM preferences WHERE student_id=?");
                    stmt.setInt(1, student_id); 
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        preferenceString = rs.getString("preferences");
                    }
                    
                    String topicId = null;
                    String supervisorName = "";
                    if(group_id != null){
                    	PreparedStatement ptst = conn.prepareStatement("Select staff_name from staff where group_id = ?");
                    	ptst.setString(1, group_id);
                    	ResultSet rst = ptst.executeQuery();
                    	if(rst.next()){
                    		supervisorName = rst.getString("staff_name");
                    	}
                    	
                    	String[] splitGroupId = group_id.split("-");
                    	if (splitGroupId.length > 1) {
                            topicId = splitGroupId[0].substring(1); // Extracts the numeric part
                        }
                    }
                    
                    String topicName = null;
                    if (topicId != null) {
                        PreparedStatement pst2 = conn.prepareStatement("SELECT topic_name FROM topics WHERE topic_id = ?");
                        pst2.setInt(1, Integer.parseInt(topicId));
                        ResultSet rs2 = pst2.executeQuery();
                        if (rs2.next()) {
                            topicName = rs2.getString("topic_name");
                        }
                    }
                    
                    if (!preferenceString.isEmpty()) {
                    List<String> topicIds = Arrays.asList(preferenceString.split(","));
                    String placeholders = String.join(",", Collections.nCopies(topicIds.size(), "?"));
                    String fieldOrder = String.join(", ", topicIds);
                    stmt.close();
                    stmt = conn.prepareStatement(
                            "SELECT topic_id, topic_name FROM topics WHERE topic_id IN (" + placeholders + ") "
                            + " ORDER BY FIELD(topic_id, " + fieldOrder + ")"
                        );
                        for (int i = 0; i < topicIds.size(); i++) {
                            stmt.setInt(i + 1, Integer.parseInt(topicIds.get(i).trim()));
                        }
                        rs.close();
                        rs = stmt.executeQuery();
                             %>
				<table class="table">
					<thead>
						<tr>
							<th>Preference Rank</th>
							<th>Topic</th>
							<%if(topicName != null){ %>
							<th>Allocated Topic Group</th>
							<%} if(!supervisorName.isEmpty()){ %>
							<th>Supervisor</th>
							<%} %>
						</tr>
					</thead>
					<tbody>

						<% int i = 1;
                        while (rs.next()) {%>
						<tr>
							<td><%= i %></td>
							<td><%= rs.getString("topic_name") %></td>
							<% if (topicName != null && i == 1) { %>
							<td><%= group_id%> - <%=topicName %></td>
							<% } else if (topicName != null) { %>
							<td></td>
							<% } if(!supervisorName.isEmpty() && i==1){ %>
							<td><%=supervisorName %></td>
							<%} else if(!supervisorName.isEmpty()){ %>
							<td></td>
							<%} %>
						</tr>
						<%i++; } %>
					</tbody>
				</table>
				<% } else { %>
				<p>No preferences saved.</p>
				<% } }catch (Exception e) {
                    e.printStackTrace();
                }
            %>
			</div>
		</div>
		
		<!-- Logout Modal -->
<div class="modal fade" id="logoutModal" tabindex="-1" role="dialog" aria-labelledby="logoutModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="logoutModalLabel">Confirm Logout</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                Are you sure you want to log out?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" onclick="logout()">Logout</button>
            </div>
        </div>
    </div>
</div>
	</div>
	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js"></script>
	<script src="js/sidebar.js"></script>
	<script src="js/modal.js"></script>
</body>
</html>
