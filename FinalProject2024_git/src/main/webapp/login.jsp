<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-image: linear-gradient(rgba(0,0,0,0),rgba(0,0,0,0)), url(images/UoL.jpg);
			background-repeat: no-repeat;
			background-size: cover;
			background-position: center;
        }
        .container {
            width: 300px;
            margin: 0 auto;
            margin-top: 220px;
        }
        input[type=text], input[type=password] {
            width: 100%;
            padding: 12px 20px;
            margin: 8px 0;
            display: inline-block;
            border: 1px solid #ccc;
            box-sizing: border-box;
        }
        button {
            background-color: red;
            color: white;
            padding: 14px 20px;
            margin: 8px 0;
            border: none;
            cursor: pointer;
            width: 100%;
        }
        button:hover {
            opacity: 0.7;
        }
        .error {
            color: white;
            display: none;
            font-weight: bold;
        }
        .logo {
            position: absolute;
            top: 20px;
            left: 20px;
            width: 150px;
            height: auto;
        }
    </style>
</head>
<body>
    <div class="container">
    <img alt="logo" class="logo" src="images/Logo.jpg">
        <h2 style="color:white">Login</h2>
        <form id="loginForm" onsubmit="return validateForm()" action="LoginServlet" method="post">
            <input type="text" placeholder="Enter Username" id="username" name="username">

            <input type="password" placeholder="Enter Password" id="password" name="password">

            <button type="submit">Login</button>

            <p id="errorMsg" class="error">${error}</p>
        </form>
    </div>

    <script>

    var errorMsg = document.getElementById("errorMsg");
    if (errorMsg.innerHTML.trim() !== "") {
        errorMsg.style.display = "block";
    }
        function validateForm() {
            var username = document.getElementById("username").value;
            var password = document.getElementById("password").value;

            if (username.trim() === "") {
                document.getElementById("errorMsg").innerHTML = "Please enter username";
                document.getElementById("errorMsg").style.display = "block";
                return false;
            } else if(password.trim() === ""){
            	document.getElementById("errorMsg").innerHTML = "Please enter password";
                document.getElementById("errorMsg").style.display = "block";
                return false;
            }

            return true;
        }
    </script>
</body>
</html>
