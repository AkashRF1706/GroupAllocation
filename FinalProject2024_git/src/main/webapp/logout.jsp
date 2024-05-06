<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Logout Page</title>
</head>
<body>
    <h1>Logout Page</h1>
    
    <%
        // Invalidating the session to logout the user
        session.invalidate();
    %>
    <jsp:forward page="login.jsp"/>
</body>
</html>
