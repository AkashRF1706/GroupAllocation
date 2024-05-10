<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.Connection, java.sql.PreparedStatement, java.sql.ResultSet, java.sql.SQLException, java.util.List, 
java.util.ArrayList, database.MySQLConnection, java.util.HashMap, java.util.Map, java.time.LocalDateTime, model.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Student Home</title>
    <!-- Bootstrap CSS for consistency -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="css/landing.css">
    
    <style>
        body {
            background-color: #f5f5f5;
        }
        .container {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        table {
            border-collapse: collapse;
            width: 100%;
        }
        th, td {
            border: 1px solid #dddddd;
            text-align: center;
            padding: 8px;
        }
        th {
            background-color: #f2f2f2;
        }
        .btn-custom, .btn-reset {
            margin-top: 10px;
            border-radius: 20px;
        }
        .btn-custom {
            background-color: #4CAF50; /* Green */
            color: white;
        }
        .btn-reset {
            background-color: #f44336; /* Red */
            color: white;
        }
        
        .logout-icon {
            position: absolute;
            top: 10px;
            right: 10px;
            cursor: pointer;
        }
        .logout-text {
            display: none;
            position: absolute;
            top: 40px;
            right: -10px;
            background-color: #fff;
            padding: 5px;
            border: 1px solid #ccc;
            cursor: pointer;
        }
        .logout-icon img {
            width: 30px; 
            height: auto;
        }
    </style>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
</head>
<body>
<% if (request.getParameter("failed") != null) {%>
    <script>alert('Preferences cannot be saved. Please try again or contact the admin...');</script>  
<% } %>

<%
    String userName = session.getAttribute("username").toString();
    String name = session.getAttribute("Name").toString();
    String department = session.getAttribute("department").toString();
    String groupId = null;
    if(session.getAttribute("groupId") != null){
    	groupId = session.getAttribute("groupId").toString();	
    }
    LocalDateTime studentDeadline = null;
    LocalDateTime now = LocalDateTime.now();
    boolean isDeadlinePassed = false;
    		
    
    Map<Integer, String> topics = new HashMap<>();
    try (Connection conn = MySQLConnection.getConnection()) {
    	PreparedStatement ptst = conn.prepareStatement("Select student_deadline from deadlines where department = ?");
        PreparedStatement stmt = conn.prepareStatement("SELECT topic_id, topic_name FROM topics WHERE is_available = 'Y' AND department = ?");
        ptst.setString(1, department);
        stmt.setString(1, department);
        
        ResultSet rs1 = ptst.executeQuery();
        if(rs1.next()){
        	 studentDeadline = rs1.getTimestamp("student_deadline").toLocalDateTime();
        }
        isDeadlinePassed = now.isAfter(studentDeadline);
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            topics.put(rs.getInt("topic_id"), rs.getString("topic_name"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
%>

<div id="wrapper">
<!-- Sidebar -->
<a href="#" class="btn btn-secondary" id="menu-toggle"><i class="fas fa-bars"></i></a>
<div id="sidebar-wrapper">
    <ul class="sidebar-nav">
        <li><a href="studentHome.jsp" class="active">Home</a></li>
        <li><a href="studentSavedPreferences.jsp">Saved Preferences</a></li>
        <%if(groupId != null){ %>
        <li><a href="groupChat.jsp">Chat Home</a></li>
        <%} %>
        <li><a href="#" data-toggle="modal" onclick="showLogoutModal()">Logout</a></li>
    </ul>
</div>

<!-- Page Content -->
<div id="page-content-wrapper">
    <div class="container">
        <h3>Welcome <%= name %></h3>
        <% if(!isDeadlinePassed){ %>
        <h2>Topics and Preferences</h2>
        <% if (topics != null && !topics.isEmpty()) { %>
        <form action="savePreferencesServlet" id="preferencesForm" method="post">
            <table class="table table-bordered">
                <thead class="thead-light">
                    <tr>
                        <th>Topic</th>
                        <th>Preference 1</th>
                        <th>Preference 2</th>
                        <th>Preference 3</th>
                        <th>Preference 4</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Map.Entry<Integer, String> topic : topics.entrySet()) { %>
                    <tr>
                        <td><%= topic.getValue() %></td>
                        <td><input type="radio" name="<%= topic.getKey() %>" value="1" data-col="1" onchange="checkPreferences()"></td>
                        <td><input type="radio" name="<%= topic.getKey() %>" value="2" data-col="2" onchange="checkPreferences()"></td>
                        <td><input type="radio" name="<%= topic.getKey() %>" value="3" data-col="3" onchange="checkPreferences()"></td>
                        <td><input type="radio" name="<%= topic.getKey() %>" value="4" data-col="4" onchange="checkPreferences()"></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <div style="text-align: center;">
                <button type="submit" class="btn btn-custom" id="saveButton" disabled>Save Preferences</button>
                <p id="message" style="color: red; display: none">Please select 4 preferences</p></div>
                <div style="text-align: center;">
                <button type="button" class="btn btn-reset" onclick="window.location.reload();">Reset</button></div>
            </div>
        </form>
        <% } else { %>
        <h3 style="color: grey; text-align: center">No topics available</h3>
        <% } } else{ %>
        <h3 style="color: grey; text-align: center">Submission deadline passed. You are no longer able to submit your preferences.</h3>
        <%} %>
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
<script>
    // Preference validation and submission script
    $("input[type=radio]").click(function() {
        el = $(this);
        col = el.data("col");
        $("input[data-col=" + col + "]").prop("checked", false);
        el.prop("checked", true);
    });

    function checkPreferences() {
        var topics = document.querySelectorAll('input[type="radio"]');
        var pref1Count = 0;
        var pref2Count = 0;
        var pref3Count = 0;
        var pref4Count = 0;
        topics.forEach(function(topic) {
            if (topic.checked) {
                var preferenceValue = topic.value;
                if (preferenceValue === "1") pref1Count++;
                else if (preferenceValue === "2") pref2Count++;
                else if (preferenceValue === "3") pref3Count++;
                else if (preferenceValue === "4") pref4Count++;
            }
        });
        var saveButton = document.getElementById('saveButton');
        saveButton.disabled = !(pref1Count === 1 && pref2Count === 1 && pref3Count === 1 && pref4Count ===1);
    }
    
    // Function to display message when hovering over disabled button
    document.getElementById('saveButton').addEventListener('mouseover', function(event) {
        var saveButton = document.getElementById('saveButton');
        if (saveButton.disabled) {
            var message = document.getElementById('message');
            message.style.display = 'block';
            // Prevent the message from disappearing when mouse moves away from the button
            saveButton.addEventListener('mouseout', function() {
                message.style.display = 'none';
            });
        }
    });        
</script>
</body>
</html>
