<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.ArrayList, model.Message, model.Group, database.MessagesDAO, database.GroupsDAO" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Group Chat</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="css/landing.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
</head>
<body>
<%
    String groupId = (String) session.getAttribute("groupId");
    String role = session.getAttribute("role").toString();
    List<String> groupIds = new ArrayList<>();
    if (role.equals("P") && groupId != null && !groupId.isEmpty()) {
        String[] groupIdParts = groupId.split(",");
        for (String group : groupIdParts) {
            groupIds.add(group.trim());
        }
    }
    String selectedGroupId = request.getParameter("selectedGroupId");
    if(role.equals("P") && selectedGroupId != null && !selectedGroupId.isEmpty() && !selectedGroupId.equalsIgnoreCase("Choose a group...")){
    	groupId = selectedGroupId;
    }
    String name = (String) session.getAttribute("Name");
    List<Message> messages = groupId != null ? MessagesDAO.getMessagesForGroup(groupId) : null;
%>
    <div id="wrapper">
        <!-- Sidebar -->
        <a href="#" class="btn btn-secondary" id="menu-toggle"><i class="fas fa-bars"></i></a>
        <div id="sidebar-wrapper">
            <ul class="sidebar-nav">
                <li><a href=<%=role.equals("S") ? "studentHome.jsp" : "supervisorHome.jsp"%>>Home</a></li>
                <li><a href=<%=role.equals("S") ? "studentSavedPreferences.jsp" : "supervisorSavedPreferences.jsp"%>>Saved Preferences</a></li>
                <li><a href="groupChat.jsp" class="active">Chat Home</a></li>
                <li><a href="#" data-toggle="modal" onclick="showLogoutModal()">Logout</a></li>
            </ul>
        </div>

        <!-- Page Content -->
        <div id="page-content-wrapper">
            <% if ("P".equals(role)) { %>
            <!-- Group Selection for 'P' Role -->
            <div class="input-group mb-3">
                <select id="group-select" name="selectedGroupId" class="custom-select">
                    <option selected>Choose a group...</option>
                    <% for (String group : groupIds) { %>
                        <option value="<%= group %>" <%= (group.equals(selectedGroupId)) ? "selected" : "" %>><%= group %></option>
                    <% } %>
                </select>
                <div class="input-group-append">
                    <button class="btn btn-primary" type="button" onclick="applyFilter()">Filter</button>
                </div>
            </div>
            <% } %>

            
                <div class="chat-box" id="chat-messages" style="display:none">
                    <% for (Message message : messages) { %>
                        <div class="p-2 border-bottom"><%= message.getUsername() %>: <%= message.getMessage() %></div>
                    <% } %>
                </div>
                <div class="input-group mb-3" id="chat-input" style="display:none">
                    <input type="text" id="message-input" class="form-control" placeholder="Type your message...">
                    <input type="hidden" id="username" value="<%= name %>">
                    <div class="input-group-append">
                        <button class="btn btn-outline-secondary" type="button" onclick="sendMessage()">Send</button>
                    </div>
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
                <div class="modal-body">Are you sure you want to log out?</div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" onclick="logout()">Logout</button>
                </div>
            </div>
        </div>
    </div>

    <script>

    $(document).ready(function() {
    	var role = '<%=role%>';
    	if(role === 'P'){
    		$('#chat-messages').hide();
        	$('#chat-input').hide();
        	}else{
        		$('#chat-messages').show();
            	$('#chat-input').show();
            	}
        });

    function applyFilter(){
    	$('#chat-messages').show();
    	$('#chat-input').show();
    	fetchMessages();
        }

    function sendMessage() {
        var messageInput = document.getElementById('message-input');
        var username = document.getElementById('username').value;
        var message = messageInput.value.trim();
        var groupId = null;
        var role = '<%=role%>';
        if(role === 'P'){
        	groupId = document.getElementById('group-select').value;
            }
        else{
        	groupId = '<%=groupId%>';
            }
        
        if (message !== "" && username !== "") {
            $.ajax({
                url: 'MessageServlet',
                method: 'POST',
                data: JSON.stringify({username: username, message: message, groupId: groupId}),
                contentType: "application/json; charset=utf-8",
                success: function() {
                    messageInput.value = '';
                    fetchMessages();
                },
                error: function() {
                    alert('Error sending message');
                }
            });
        }
    }

    function fetchMessages() {
    	var groupId = null;
    	var role = '<%=role%>';
        if(role === 'P'){
        	groupId = document.getElementById('group-select').value;
            }
        else{
			groupId = '<%=groupId%>';
            }
        console.log(groupId);
        $.ajax({
            url: 'FetchMessagesServlet',
            method: 'GET',
            data: { groupId: groupId },
            dataType: 'json',
            success: function(messages) {
                const messageList = document.getElementById('chat-messages');
                messageList.innerHTML = ''; // Clear current messages
                messages.forEach(function(msg) {
                    const messageElement = '<div class="p-2 border-bottom">' + msg.username + ': ' + msg.message + '</div>';
                    messageList.innerHTML += messageElement;
                });
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log('Error fetching messages:', textStatus, errorThrown);
            }
        });
    }

    setInterval(fetchMessages, 5000); // Fetch messages every 5 seconds
    </script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
    <script src="js/sidebar.js"></script>
    <script src="js/modal.js"></script>
</body>
</html>
