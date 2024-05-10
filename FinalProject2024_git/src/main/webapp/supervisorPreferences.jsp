<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.ArrayList, model.Supervisor, database.SupervisorsDAO, database.DepartmentDAO"%>
<%
DepartmentDAO dd = new DepartmentDAO();
String name = session.getAttribute("Name").toString();
List<String> departments = dd.getAllDepartmentsWithPreferences();
    String departmentFilter = request.getParameter("department");
    List<Supervisor> supervisors = new ArrayList<>();
    if (departmentFilter != null && !departmentFilter.isEmpty()) {
        SupervisorsDAO sd = new SupervisorsDAO();
        supervisors = sd.getAllSupervisors(departmentFilter);
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Supervisor Preferences</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="css/landing.css">
</head>
<body>
<div id="wrapper">
    <!-- Sidebar -->
    <div id="sidebar-wrapper">
        <ul class="sidebar-nav">
        <li><a href="adminHome.jsp">Home Page</a></li>
            <li><a href="studentPreferences.jsp">Student Preferences</a></li>
            <li><a href="supervisorPreferences.jsp" class="active">Supervisor Preferences</a></li>
            <li><a href="runAlgorithm.jsp">Run Algorithm</a></li>
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
                <h1>Supervisor Preferences</h1>
                <form action="supervisorPreferences.jsp" method="get" class="mb-4">
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
                    <button type="submit" class="btn btn-custom">Apply Filter</button>
                </form>
                <!-- Student table, displayed only if department filter is applied -->
    <% if (departmentFilter != null && !departmentFilter.isEmpty() && !supervisors.isEmpty()) { %>
        <div class="table-container" style="display: block;">
            <table class="table table-bordered">
                <thead class="thead-light">
                    <tr>
                        <th>Supervisor ID</th>
                        <th>Name</th>
                        <th>Department</th>
                        <th>Supervising Capacity</th>
                        <th>Preferred Topics</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Supervisor supervisor : supervisors) { %>
                    <tr>
                        <td><%= supervisor.getId() %></td>
                        <td><%= supervisor.getName() %></td>
                        <td><%= supervisor.getDepartment() %></td>
                        <td><%= supervisor.getNumGroups() %></td>
                        <td>
                        <%for(String topic : supervisor.getPreferences()){ %>
                        <li><%=topic %></li>
                        <%} %></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    <% } else if(departmentFilter == null || departmentFilter.isEmpty()){ %>
    <p id="message" style="color: red; display: block">Please select a department</p>
    <%} else if(departmentFilter != null && !departmentFilter.isEmpty() && supervisors.isEmpty()){ %>
    <p id="message" style="color: red; display: block">No records to display</p>
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
</body>
</html>
