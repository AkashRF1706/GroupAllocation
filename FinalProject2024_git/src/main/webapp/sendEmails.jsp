<%@ page import="java.util.List, database.DepartmentDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Send Emails to Students</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="css/landing.css">
    <style>
        .form-section { background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin-top: 20px; }
        select, textarea { margin-top: 10px; }
    </style>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<% DepartmentDAO dd = new DepartmentDAO();
   List<String> departments = dd.getAllDepartmentsForTopic(); %>
    <div id="wrapper">
        <!-- Sidebar -->
        <div id="sidebar-wrapper">
            <ul class="sidebar-nav">
                <li><a href="adminHome.jsp">Home Page</a></li>
                <li><a href="studentPreferences.jsp">Student Preferences</a></li>
                <li><a href="supervisorPreferences.jsp">Supervisor Preferences</a></li>
                <li><a href="runAlgorithm.jsp">Run Algorithm</a></li>
                <li><a href="formedGroups.jsp">Formed Groups</a></li>
                <li><a href="sendEmails.jsp" class="active">Send Email</a></li>
                <li><a href="#" data-toggle="modal" onclick="showLogoutModal()">Logout</a></li>
            </ul>
        </div>
        <!-- Page Content -->
        <div id="page-content-wrapper">
            <div class="container-fluid">
            <a href="#" class="btn btn-secondary" id="menu-toggle"><i class="fas fa-bars"></i></a>
                <h1>Send Emails to Students</h1>
                <div class="form-section">
                    <form id="departmentForm">
                        <label for="department">Choose Department:</label>
                        <select name="department" id="department" class="form-control">
                            <option value="">Select a Department</option>
                            <% for (String dept : departments) { %>
                            <option value="<%= dept %>"><%= dept %></option>
                            <% } %>
                        </select>
                        <button type="button" class="btn btn-primary mt-2" onclick="fetchEmails()">Get Student Emails</button>
                    </form>
                </div>
                <div id="emailSelection" class="form-section" style="display:none;">
                    <label for="emails">Select Emails:</label>
                    <select multiple id="emails" class="form-control" style="height:100px;" required="required">
                    </select>
                    <label for="subject">Subject:</label>
                    <input type="text" id="subject" class="form-control" placeholder="Enter your subject here..." required="required">
                    <label for="message">Message:</label>
                    <textarea id="message" class="form-control" rows="4" cols="50" placeholder="Enter your message here..." required="required"></textarea>
                    <button type="button" class="btn btn-success mt-2" onclick="sendEmail()">Send Email</button>
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
    function fetchEmails() {
        var dept = $('#department').val();
        if (dept) {
            $.ajax({
                url: 'GetEmailsServlet',
                type: 'POST',
                data: {department: dept},
                success: function(response) {
                    $('#emails').empty(); // Clear previous options
                    response.forEach(email => {
                        $('#emails').append($('<option>', {
                            value: email,
                            text: email
                        }));
                    });
                    $('#emailSelection').show();
                },
                error: function() {
                    alert('Error fetching emails. Please try again.');
                }
            });
        }
    }

    function sendEmail() {
        var selectedEmails = $('#emails').val();
        var subject = $('#subject').val();
        var message = $('#message').val();
        if (selectedEmails.length > 0 && message.trim() !== "" && subject.trim() !== "") {
            $.ajax({
                url: 'SendEmailServlet',
                type: 'POST',
                data: {
                    emails: JSON.stringify(selectedEmails),
                    subject: subject,
                    message: message
                },
                success: function() {
                    alert('Email sent successfully!');
                },
                error: function() {
                    alert('Failed to send email. Please try again.');
                }
            });
        } else {
            alert('Please select at least one email, fill out the subject and write a message.');
        }
    }
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js"></script>
</body>
</html>
