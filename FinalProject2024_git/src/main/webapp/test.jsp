<%@page import="database.MySQLConnection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.DriverManager"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Supervisor Home</title>
    <!-- Bootstrap CSS for styling -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <!-- jQuery for simple DOM manipulation -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <style>
        body {
            background-color: #f5f5f5;
            padding-top: 20px;
        }
        .container {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .btn-primary, .btn-outline-secondary {
            margin-top: 10px;
            border-radius: 20px;
        }
        .hidden {
            display: none;
        }
    </style>
</head>
<body>
<%
//Retrieve username from session
String userName = session.getAttribute("username").toString();
String department = session.getAttribute("department").toString();
    %>
<h3>Hi <%=userName %></h3>
<div class="container">
    <h2 class="text-center">Supervisor Preferences</h2>
    <form action="savePreferencesServlet" method="post">
        <div class="form-group">
            <label for="numGroups">Number of Groups to Supervise</label>
            <select id="numGroups" name="numGroups" class="form-control">
                <option value="">Select Number of Groups</option>
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="3">3</option>
                <option value="4">4</option>
                <option value="5">5</option>
            </select>
        </div>
        <div id="topicsContainer" class="hidden">
            <div class="form-group">
                <label>Select Topics:</label>
                <% 
                // Initialize and fetch topics from the database
                List<String> topics = new ArrayList<>();
                try {
                    Connection conn = MySQLConnection.getConnection();
                    String sql = "SELECT topic_name FROM topics where department = '"+department+"'"; // Adjust based on your table schema
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    ResultSet rs = pstmt.executeQuery();
                    while(rs.next()) {
                        topics.add(rs.getString("topic_name"));
                    }
                    rs.close();
                    pstmt.close();
                    conn.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                %>
                <% for(String topic: topics) { %>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="topics[]" value="<%= topic %>">
                        <label class="form-check-label"><%= topic %></label>
                    </div>
                <% } %>
            </div>
        <button type="submit" id="submitBtn" class="btn btn-primary" disabled>Submit Preferences</button>
            <div id="disabledButtonTooltip" class="disabled-button-tooltip"></div>
        </div>
    </form>
</div>

<script>
$(document).ready(function() {
    $('#numGroups').change(function() {
        $('#topicsContainer').toggleClass('hidden', !this.value);
        checkTopics();
    });

    // Check topics and enable/disable submit button
    function checkTopics() {
        var topics = $('input[type="checkbox"][name="topics[]"]');
        var anyChecked = topics.is(':checked');
        
        // Enable submit button if any checkbox is checked, otherwise disable
        $('#submitBtn').prop('disabled', !anyChecked);
    }

    // Event handler for checkbox state change
    $('body').on('change', 'input[type="checkbox"][name="topics[]"]', function() {
        checkTopics();
    });

    // Tooltip display logic for disabled submit button
    $('#submitBtn').hover(function() {
        if ($(this).is(':disabled')) {
            showTooltip($(this), "Please select at least one topic");
        }
    }, function() {
        $('#disabledButtonTooltip').hide();
    });

    function showTooltip(element, message) {
        var offset = element.offset();
        $('#disabledButtonTooltip')
            .text(message)
            .css({top: offset.top + 20, left: offset.left})
            .show();
    }
});
</script>


</body>
</html>
