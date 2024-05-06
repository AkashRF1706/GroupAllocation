<%@page import="model.Supervisor"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.Connection, java.sql.PreparedStatement, java.sql.ResultSet, database.MySQLConnection, java.sql.SQLException, java.util.List, java.util.ArrayList, model.Student, database.StudentsDAO, database.DepartmentDAO"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Home</title>
    <link rel="stylesheet"
        href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <link rel="stylesheet"
        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="css/landing.css">
</head>
<body>
<% if (request.getParameter("ReleasedStudentTopics") != null) {%>
    <script>alert('Student Topics Released Successfully');</script>  
<% } %>
<% if (request.getParameter("ReleaseStudentFailed") != null) {%>
    <script>alert('Student Topic Release failed. Supervisor topics may not be released yet..');</script>  
<% } %>
<% if (request.getParameter("ReleasedSupervisorTopics") != null) {%>
    <script>alert('Supervisor Topics Released Successfully');</script>  
<% } %>
<% if (request.getParameter("ReleaseSupervisorFailed") != null) {%>
    <script>alert('Supervisor Topic Release failed. Please try again..');</script>  
<% } %>
    <%
    String name = session.getAttribute("Name").toString();
    DepartmentDAO dd = new DepartmentDAO();
	List<String> departments = dd.getAllDepartmentsForTopic();
    int numberOfStudents = 0;
    int numberOfSupervisors = 0;
    		List<Student> studentList = new ArrayList<>();
    		List<Supervisor> supervisorList = new ArrayList<>();
    try (Connection conn = MySQLConnection.getConnection()) {
        // Query for counting students
        PreparedStatement stmt1 = conn.prepareStatement("SELECT COUNT(*) AS count FROM students where student_id !=3");
        ResultSet rs1 = stmt1.executeQuery();
        if (rs1.next()) {
            numberOfStudents = rs1.getInt("count");
        }
        
        // Query for counting supervisors
        PreparedStatement stmt2 = conn.prepareStatement("SELECT COUNT(*) AS count FROM staff where staff_id !=1 and role = 'staff' and is_supervisor = 'Y'");
        ResultSet rs2 = stmt2.executeQuery();
        if (rs2.next()) {
            numberOfSupervisors = rs2.getInt("count");
        }
        
        PreparedStatement stmt3 = conn.prepareStatement("Select student_name, username, department from students where student_id != 3");
        ResultSet rs3 = stmt3.executeQuery();
        while(rs3.next()){
        	Student student = new Student(rs3.getString("username"), rs3.getString("student_name"), rs3.getString("department"));
        	studentList.add(student);
        }
        
        PreparedStatement stmt4 = conn.prepareStatement("Select staff_name, username, department from staff where is_supervisor = 'Y' and staff_id != 1");
        ResultSet rs4 = stmt4.executeQuery();
        while(rs4.next()){
        	Supervisor supervisor = new Supervisor(rs4.getString("username"), rs4.getString("staff_name"), rs4.getString("department"));
        	supervisorList.add(supervisor);
        }
        request.setAttribute("students", studentList);
        request.setAttribute("supervisors", supervisorList);
    } catch (SQLException e) {
        e.printStackTrace();
    }
%>
    <div id="wrapper">
        <!-- Sidebar -->
        <div id="sidebar-wrapper">
            <ul class="sidebar-nav">
                <li><a href="adminHome.jsp" class="active">Home Page</a></li>
                <li><a href="studentPreferences.jsp">Student Preferences</a></li>
                <li><a href="runAlgorithm.jsp">Run Algorithm</a></li>
                <li><a href="formedGroups.jsp">Formed Groups</a></li>
                <li><a href="logout.jsp">Logout</a></li>
            </ul>
        </div>

        <!-- Page Content -->
        <div id="page-content-wrapper">
            <div class="container-fluid">
                <a href="#" class="btn btn-secondary" id="menu-toggle"><i class="fas fa-bars"></i></a>
                <div class="row">
                    <div class="col-lg-12">
                    <h1>Welcome <%=name %></h1>
                        <h2>Admin Dashboard</h2>
                        <div class="row mt-4">
                            <!-- Student Count Card -->
                            <div class="col-md-6 col-lg-4 mb-3" data-toggle="modal" data-target="#studentDetailsModal" style="cursor: pointer;">
                                <div class="card text-white bg-info">
                                    <div class="card-body">
                                        <h5 class="card-title">Number of Students</h5>
                                        <p class="card-text"><%= numberOfStudents %></p>
                                    </div>
                                </div>
                            </div>
                            <!-- Supervisor Count Card -->
                            <div class="col-md-6 col-lg-4 mb-3" data-toggle="modal" data-target="#supervisorDetailsModal" style="cursor: pointer;">
                                <div class="card text-white bg-info">
                                    <div class="card-body">
                                        <h5 class="card-title">Number of Supervisors</h5>
                                        <p class="card-text"><%= numberOfSupervisors %></p>
                                    </div>
                                </div>
                            </div>

                            <!-- Actions -->
<!-- Actions -->
<div class="col-md-12 col-lg-4 mb-3">
    <div class="card">
        <div class="card-body">
            <h5 class="card-title">Actions</h5>
            <form action="releaseTopics" method="post" class="row">
    <!-- Button 1 -->
    <div class="col-6">
        <button type="button" name="action" value="releaseStudentTopics" class="btn btn-success">Release Student Topics</button>
    </div>
    <!-- Button 2 -->
    <div class="col-6">
        <button type="button" name="action" value="releaseSupervisorTopics" class="btn btn-success">Release Supervisor Topics</button>
    </div>
</form>
        </div>
    </div>
</div>

                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modals -->
        <!-- Student Details Modal -->
        <div class="modal fade" id="studentDetailsModal" tabindex="-1" role="dialog" aria-labelledby="studentDetailsModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="studentDetailsModalLabel">Student Details</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <table class="table table-bordered">
                    <tr>
                        <th>User Name</th>
                        <th>Name</th>
                        <th>Department</th>
                    </tr>
                    <% 
                    List<Student> students = (List<Student>) request.getAttribute("students");
                    for (Student s : students) {
                        out.println("<tr><td>" + s.getId() + "</td><td>" + s.getName() + "</td><td>" + s.getDepartment() + "</td></tr>");
                    }
                    %>
                </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Supervisor Details Modal -->
        <div class="modal fade" id="supervisorDetailsModal" tabindex="-1" role="dialog" aria-labelledby="supervisorDetailsModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="supervisorDetailsModalLabel">Supervisor Details</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <table class="table table-bordered">
                    <tr>
                        <th>User Name</th>
                        <th>Name</th>
                        <th>Department</th>
                    </tr>
                    <% 
                    List<Supervisor> supervisors = (List<Supervisor>) request.getAttribute("supervisors");
                    for (Supervisor s : supervisors) {
                        out.println("<tr><td>" + s.getId() + "</td><td>" + s.getName() + "</td><td>" + s.getDepartment() + "</td></tr>");
                    }
                    %>
                </table>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Confirmation Modal -->
<div class="modal fade" id="confirmationModal" tabindex="-1" role="dialog" aria-labelledby="confirmationModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="confirmationModalLabel">Schedule Release</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form action="scheduleRelease" method="post">
                <div class="modal-body">
                    <div class="form-group">
                        <label for="departmentSelect">Select Department:</label>
                        <select class="form-control" id="department" name="department">
                            <option value="">All Departments</option>
    <% for(String dept : departments) {  %>
    <option value="<%= dept %>"><%= dept %></option>
    <% } %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="dateTimePicker">Choose Date and Time:</label>
                        <input type="datetime-local" class="form-control" id="dateTimePicker" name="dateTime">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-danger" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success">Confirm</button>
                </div>
            </form>
        </div>
    </div>
</div>

        
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js"></script>
    <script src="js/sidebar.js"></script>
    <script src="js/modal.js"></script>
    <script>
function setMinDateTime() {
    var dateTimePicker = document.getElementById('dateTimePicker');
    var now = new Date();
    var localOffset = now.getTimezoneOffset() * 60000; // Offset in milliseconds
    var localNow = new Date(now.getTime() - localOffset); // Local time adjusted by timezone offset

    var year = localNow.getFullYear();
    var month = ("0" + (localNow.getMonth() + 1)).slice(-2);
    var day = ("0" + localNow.getDate()).slice(-2);
    var hour = ("0" + localNow.getHours()).slice(-2);
    var minute = ("0" + localNow.getMinutes()).slice(-2);

    var minDateTime = year + '-' + month + '-' + day + 'T' + hour + ':' + minute;
    dateTimePicker.min = minDateTime;
}

document.addEventListener('DOMContentLoaded', function() {
    setMinDateTime();
    setInterval(setMinDateTime, 60000); // Update min datetime every minute
});
</script>


    
</body>
</html>
