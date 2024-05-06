<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Error Occurred</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <style>
        .error-container {
            margin-top: 50px;
            text-align: center;
        }
    </style>
</head>
<body>
    <div class="container error-container">
        <h1>Oops! Something went wrong.</h1>
        <div class="alert alert-danger" role="alert">
            <strong>Error Details:</strong> <%= exception.getMessage() %>
        </div>
        <p>Please try again later or contact support if the problem persists.</p>
        <a href="login.jsp" class="btn btn-primary">Login</a>
    </div>
</body>
</html>
