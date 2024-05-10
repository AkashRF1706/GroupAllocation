<%@page import="database.MySQLConnection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*, java.util.*, java.time.LocalDateTime"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Supervisor Home</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="css/landing.css">
    <style>
        body { background-color: #f5f5f5;}
        .container { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        .btn-primary, .btn-outline-secondary { margin-top: 10px; border-radius: 20px; }
        .hidden { display: none; }
        .logout-icon { position: fixed; top: 10px; right: 10px; cursor: pointer; }
        .logout-text { display: none; position: absolute; top: 40px; right: -10px; background-color: #fff; padding: 5px; border: 1px solid #ccc; cursor: pointer; }
        .logout-icon img { width: 30px; height: auto; }
    </style>
</head>
<body>
<%
    if (request.getParameter("failed") != null) {
        out.println("<script>alert('Preferences cannot be saved. Please try again or contact the admin.');</script>");  
    }
    String userName = session.getAttribute("username").toString();
    String department = session.getAttribute("department").toString();
    String name = session.getAttribute("Name").toString();
    String groupId = null;
    if(session.getAttribute("groupId") != null){
    	groupId = session.getAttribute("groupId").toString();
    }
    LocalDateTime supervisorDeadline = null;
    LocalDateTime now = LocalDateTime.now();
    boolean isDeadlinePassed = false;
        		
    List<String> topics = new ArrayList<>();
    try (Connection conn = MySQLConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement("SELECT topic_name FROM topics where department = ? and is_available = 'P'")) {
    	PreparedStatement ptst = conn.prepareStatement("Select staff_deadline from deadlines where department = ?");
    	
    	pstmt.setString(1, department);
    	ptst.setString(1, department);
    	
    	ResultSet rs1 = ptst.executeQuery();
    	if(rs1.next()){
    		supervisorDeadline = rs1.getTimestamp("staff_deadline").toLocalDateTime();;
    	}
    	isDeadlinePassed = now.isAfter(supervisorDeadline);
    	
        ResultSet rs = pstmt.executeQuery();
        while(rs.next()) {
            topics.add(rs.getString("topic_name"));
        }
    } catch(Exception e) {
        e.printStackTrace();
    }
    
    String numGroupsSelected = request.getParameter("numGroups");
    if (numGroupsSelected == null) {
        numGroupsSelected = (String) session.getAttribute("numGroupsSelected");
    }
%>
<div id="wrapper">
<a href="#" class="btn btn-secondary" id="menu-toggle"><i class="fas fa-bars"></i></a>
    <div id="sidebar-wrapper">
        <ul class="sidebar-nav">
            <li><a href="supervisorHome.jsp" class="active">Home</a></li>
            <li><a href="supervisorSavedPreferences.jsp">Saved Preferences</a></li>
            <%if(groupId != null){ %>
        <li><a href="groupChat.jsp">Chat Home</a></li>
        <%} %>
            <li><a href="#" data-toggle="modal" onclick="showLogoutModal()">Logout</a></li>
        </ul>
    </div>
    <div id="page-content-wrapper">
        <div class="container">
            <h3>Welcome <%=name %></h3>
            <% if(!isDeadlinePassed){ %>
            <h2 class="text-center">Supervisor Preferences</h2>
            <% if (!topics.isEmpty()) { %>
            <form action="savePreferencesServlet" method="post">
                <div class="form-group">
                    <label for="numGroups">Number of Groups to Supervise</label>
                    <select id="numGroups" name="numGroups" class="form-control">
    <option value="">Select Number of Groups</option>
    <option value="1" <%= "1".equals(numGroupsSelected) ? "selected" : "" %>>1</option>
    <option value="2" <%= "2".equals(numGroupsSelected) ? "selected" : "" %>>2</option>
    <option value="3" <%= "3".equals(numGroupsSelected) ? "selected" : "" %>>3</option>
    <option value="4" <%= "4".equals(numGroupsSelected) ? "selected" : "" %>>4</option>
    <option value="5" <%= "5".equals(numGroupsSelected) ? "selected" : "" %>>5</option>
</select>
                </div>
                <div id="topicsContainer" class="hidden">
                    <div class="form-group">
                        <label>Select Topics:</label>
                        <% for (String topic : topics) { %>
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="topics[]" value="<%= topic %>" onchange="toggleSubmitButton()">
                                <label class="form-check-label"><%= topic %></label>
                            </div>
                        <% } %>
                    </div>
                    <button type="button" id="submitBtn" class="btn btn-primary" disabled="disabled" onclick="confirmSubmission()">Submit Preferences</button>
                    <div id="disabledButtonTooltip" class="hidden">Please select at least one topic</div>
                    <button type="button" class="btn btn-outline-secondary" onclick="enableCreationMode()">Create New Topic</button>
    <div id="newTopicForm" class="form-group hidden">
    <input type="text" class="form-control" id="newTopicName" name="newTopicName" placeholder="Enter new topic name">
    <input type="hidden" name="supervisorName" id="supervisorName" value="<%=name %>">
    <div class="btn-group mt-2">
    <button id="submitNewTopicButton" type="button" class="btn btn-primary" onclick="submitNewTopic()">Submit New Topic</button> &nbsp
<button id="cancelNewTopicButton" type="button" class="btn btn-primary" onclick="cancelNewTopic()">Cancel</button>

    </div>
</div>
                </div>
                <% } else { %>
                    <h3 style="color: grey; text-align: center">No topics to display</h3>
                <% } } else{%>
                <h3 style="color: grey; text-align: center">Submission deadline passed. You are no longer able to submit your preferences.</h3>
                <%} %>
            </form>
        </div>
    </div>
    
    <!-- Confirmation Modal -->
<div class="modal fade" id="confirmationModal" tabindex="-1" role="dialog" aria-labelledby="confirmationModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="confirmationModalLabel">Confirm Submission</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        Are you sure you want to submit these preferences?
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" onclick="submitForm()">Confirm</button>
      </div>
    </div>
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
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
<script src="js/sidebar.js"></script>
<script src="js/modal.js"></script>
<script>
$(document).ready(function() {
	var numGroupsValue = $('#numGroups').val();
    toggleTopicsContainer(numGroupsValue);
    
	$('#numGroups').change(function() {
	    var selectedValue = $(this).val();
	    if (selectedValue) {
	        $('#topicsContainer').removeClass('hidden');
	        // Store the selected value in the session
	        $.ajax({
	            url: 'SetNumGroupsServlet',
	            type: 'POST',
	            data: { 'numGroups': selectedValue }
	        });
	    } else {
	        $('#topicsContainer').addClass('hidden');
	    }
	});
});


    $('#submitBtn').hover(
        function() {
            // Only show the tooltip if the button is disabled
            if ($(this).is(':disabled')) {
                $('#disabledButtonTooltip').removeClass('hidden').show();
            }
        }, 
        function() {
            $('#disabledButtonTooltip').hide(); // Always hide the tooltip when not hovering
        }
    );

    function toggleTopicsContainer(value) {
        if (value) {
            $('#topicsContainer').removeClass('hidden');
        } else {
            $('#topicsContainer').addClass('hidden');
        }
    }
    function confirmSubmission() {
        $('#confirmationModal').modal('show');
    }
    function submitForm() {
        $('form').submit();
    }
    function enableCreationMode() {
        $('#topicsContainer input, #topicsContainer button, #numGroups').prop('disabled', true);
        $('#newTopicForm').removeClass('hidden');
        $('#newTopicName').removeAttr('disabled');
        $('#submitNewTopicButton').removeAttr('disabled');
        $('#cancelNewTopicButton').removeAttr('disabled');
    }

    $('#cancelNewTopicButton').click(function() {
        $('#newTopicForm').addClass('hidden');
        $('#newTopicName').val('').prop('disabled', true);
        $(this).prop('disabled', true);
        $('#submitNewTopicButton').prop('disabled', true);
        $('#topicsContainer input, #topicsContainer button, #numGroups').prop('disabled', false);
    });
    function submitNewTopic() {
        var topicName = $('#newTopicName').val().trim();
        var supervisorName = $('#supervisorName').val();
        if (topicName) {
            $.ajax({
                url: 'AddNewTopicServlet',
                type: 'POST',
                data: { 'topicName': topicName, 'supervisorName': supervisorName },
                success: function(data) {
                    if (data.message) {
                        alert(data.message); // Could be success or error message
                    } else {
                        alert('New Topic added successfully');
                        var numGroups = $('#numGroups').val();
                        location.reload(true);
                        var url = window.location.href;
                        if (url.indexOf('?') > -1) {
                            url += '&numGroups=' + numGroups;
                        } else {
                            url += '?numGroups=' + numGroups;
                        }
                        window.location.href = url;
                        /* updateTopicList(data); // Update the topics dropdown */
                    }
                    $('#newTopicForm').addClass('hidden');
                    $('#newTopicName').val('');
                    $('#topicsContainer input, #topicsContainer button, #numGroups').prop('disabled', false);
                },
                error: function(xhr, status, error) {
                    alert('Error adding new topic: ' + xhr.responseText);
                    $('#newTopicName').prop('disabled', false);
                    $('#newTopicForm button').prop('disabled', false);
                }
            });
        } else {
            alert('Please enter a topic name.');
        }
    }

    function updateTopicList(topics) {
        var topicsContainer = $('#topicsContainer form-group');
        topicsContainer.empty(); // Clear existing topics

        $.each(topics, function(topic) {
            topicsContainer.append(
                `<div class="form-check">
                    <input class="form-check-input" type="checkbox" name="topics[]" value="${topic}" onchange="toggleSubmitButton()">
                    <label class="form-check-label">${topic}</label>
                </div>`
            );
        });
    }
   
    function toggleSubmitButton() {
        var isChecked = $('input[type="checkbox"][name="topics[]"]:checked').length > 0;
        $('#submitBtn').prop('disabled', !isChecked);
        $('#disabledButtonTooltip').toggleClass('hidden', isChecked);
    }

</script>
</body>
</html>
