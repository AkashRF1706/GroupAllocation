<%@page import="database.SupervisorsDAO"%>
<%@page import="org.apache.catalina.startup.Catalina"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.ArrayList, model.Student, model.Supervisor, java.sql.Connection, java.sql.PreparedStatement, java.sql.ResultSet, database.MySQLConnection, database.StudentsDAO, database.DepartmentDAO"%>
<%
DepartmentDAO dd = new DepartmentDAO();
List<String> departments = dd.getAllDepartmentsWithPreferences();

String departmentFilter = request.getParameter("department");
String name = session.getAttribute("Name").toString();
List<Student> students = new ArrayList<>();
List<Supervisor> supervisors = new ArrayList<>();
int numStudents = 0;
int numSupervisors = 0;
if (departmentFilter != null && !departmentFilter.isEmpty()) {
    StudentsDAO sd = new StudentsDAO();
    SupervisorsDAO spd = new SupervisorsDAO();
    students = sd.getAllStudents(departmentFilter);
    numStudents = students.size();
    supervisors = spd.getAllSupervisors(departmentFilter);
    numSupervisors = supervisors.size();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Run Algorithm</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="css/landing.css">
</head>
<body>
<%
if(request.getParameter("success") != null){
	out.println("<script>alert('Group allocation done.');</script>");
}
 if (request.getParameter("failed") != null) {
        out.println("<script>alert('Cannot be allocated optimally. Check the preferences and run again');</script>");  
    }
%>
<div id="wrapper">
    <!-- Sidebar -->
    <div id="sidebar-wrapper">
        <ul class="sidebar-nav">
        <li><a href="adminHome.jsp">Home Page</a></li>
        <li><a href="studentPreferences.jsp">Student Preferences</a></li>
        <li><a href="supervisorPreferences.jsp">Supervisor Preferences</a></li>
            <li><a href="runAlgorithm.jsp" class="active">Run Algorithm</a></li>
            <li><a href="formedGroups.jsp">Formed Groups</a></li>
            <li><a href="sendEmails.jsp">Send Email</a></li>
            <li><a href="#" data-toggle="modal" onclick="showLogoutModal()">Logout</a></li>
        </ul>
    </div>
	
    <!-- Page Content -->
    <div id="page-content-wrapper">
        <div class="container-fluid">
            <a href="#" class="btn btn-secondary" id="menu-toggle"><i class="fas fa-bars"></i></a>
            <div class="row">
                <div class="col-lg-12">
                <h1>Run Algorithm</h1>
                <form action="runAlgorithm.jsp" method="get" class="mb-4">
    <div class="form-group">
        <label for="department">Filter by Department:</label>
        <select name="department" id="department" class="form-control">
    <option value="">All Departments</option>
    <% for(String dept : departments) { 
        String selected = dept.equals(departmentFilter) ? "selected" : "";
    %>
    <option value="<%= dept %>" <%= selected %>><%= dept %></option>
    <% } %>
</select>
    </div>
    <button type="submit" class="btn btn-primary">Apply Filter</button>
</form>

                <!-- Student and Supervisor details, displayed only if department filter is applied -->
    <% if (departmentFilter != null && !departmentFilter.isEmpty() && numStudents > 4) {
    	int maxStudentAllowed = numStudents/2;%>
        <div class="table-container" style="display: block;">
            <h3>Number of Students: <%= numStudents %></h3>
            <h3>Number of Supervisors: <%= numSupervisors %></h3>
            <div class="form-group">
                <label for="maxStudentsPerGroup">Maximum Students per Group:</label>
                <input type="number" id="maxStudentsPerGroup" name="maxStudentsPerGroup" class="form-control" min="4" max="<%=maxStudentAllowed %>" required>
                <input type="hidden" id="numStudents" name="numStudents" value="<%=maxStudentAllowed%>"> 
            </div>
            <div class="form-group">
                <label for="topicUseLimit">Topic Use Limit:</label>
                <input type="number" id="topicUseLimit" name="topicUseLimit" class="form-control" min="1" max="5" required>
            </div>
            <button type="button" class="btn btn-custom" id="runAlgorithm">Run Algorithm</button>
        </div>
    <% } else if(departmentFilter == null || departmentFilter.isEmpty()){ %>
    <p id="message" style="color: red; display: block">Please select a department</p>
    <%} else if(departmentFilter != null && !departmentFilter.isEmpty() && (students.isEmpty() || supervisors.isEmpty())){ %>
    <p id="message" style="color: red; display: block">No records to display</p>
    <%} else if(numStudents <= 4){%>
    <p id="message" style="color: red; display: block">Students less than minimum size</p>
    <%} %>
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
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js"></script>
<script src="js/sidebar.js"></script>
<script src="js/modal.js"></script>
<script>
$(document).ready(function() {
    $('#runAlgorithm').click(function(event) {
        event.preventDefault();  // Prevent the default form submission

        var department = $('#department').val();
        var maxStudentsPerGroup = $('#maxStudentsPerGroup').val();
        var numStudents = $('#numStudents').val();
        var topicUseLimit = $('#topicUseLimit').val();

        if (!department) {
            alert('Please select a department.');
            return; // Exit the function if no department is selected
        } 
        if (!maxStudentsPerGroup) {
            alert('Please enter the maximum number of students per group.');
            return;
        }

        // Proceed with AJAX submission
        $.ajax({
            url: 'RunAlgorithm',
            type: 'POST',
            data: {
                department: department,
                maxStudentsPerGroup: maxStudentsPerGroup,
                numStudents: numStudents,
                topicUseLimit: topicUseLimit
            },
            dataType: 'json', 
            success: function(response) {
                alert(response.message); 
                if (response.redirectUrl) {
                    window.location.href = response.redirectUrl;
                }
            },
            error: function(xhr, status, error) {
                alert('Error: ' + xhr.responseText); 
            }
        });
    });
});

</script>

</body>
</html>