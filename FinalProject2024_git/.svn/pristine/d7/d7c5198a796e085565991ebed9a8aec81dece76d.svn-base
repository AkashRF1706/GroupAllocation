<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.Message"%>
<%@ page import="database.MessagesDAO"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Group Chat</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="css/landing.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
</head>
<body>
<%
    String groupId = (String) session.getAttribute("groupId");
	String role = session.getAttribute("role").toString();
	System.out.println(role);
    if (groupId == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String name = (String) session.getAttribute("Name");
    List<Message> messages = MessagesDAO.getMessagesForGroup(groupId);
%>
    <div id="wrapper">
        <!-- Sidebar -->
        <a href="#" class="btn btn-secondary" id="menu-toggle"><i class="fas fa-bars"></i></a>
        <div id="sidebar-wrapper">
            <ul class="sidebar-nav">
                <li><a href=<%=role.equals("S") ? "studentHome.jsp" : "supervisorHome.jsp" %>>Home</a></li>
                <li><a href=<%=role.equals("S") ? "studentSavedPreferences.jsp" : "supervisorSavedPreferences.jsp"%>>Saved Preferences</a></li>
                <li><a href="groupChat.jsp" class="active">Chat Home</a></li>
                <li><a href="logout.jsp">Logout</a></li>
            </ul>
        </div>

        <!-- Page Content -->
        <div id="page-content-wrapper">
            <div class="chat-box" id="chat-messages">
                <% for (Message message : messages) { %>
                    <div class="p-2 border-bottom"><%= message.getUsername() %>: <%= message.getMessage() %></div>
                <% } %>
            </div>
            <div class="input-group mb-3">
                <input type="text" id="message-input" class="form-control" placeholder="Type your message...">
                <input type="hidden" id="username" value="<%= name %>">
                <div class="input-group-append">
                    <button class="btn btn-outline-secondary" type="button" onclick="sendMessage()">Send</button>
                </div>
            </div>
        </div>
    </div>

    <script src="js/sidebar.js"></script>
    <script>
    function fetchMessages() {
    	$.ajax({
    	    url: 'FetchMessagesServlet',
    	    method: 'GET',
    	    data: { groupId: '<%= groupId %>' },
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

    function sendMessage() {
        var messageInput = document.getElementById('message-input');
        var username = document.getElementById('username').value;
        var message = messageInput.value.trim();
        if (message !== "" && username !== "") {
            $.ajax({
                url: 'MessageServlet',
                method: 'POST',
                data: JSON.stringify({username: username, message: message, groupId: "<%= groupId %>"}),
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
</script>

</body>
</html>
