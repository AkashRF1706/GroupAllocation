<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.Group, database.GroupsDAO, database.DepartmentDAO"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formed Groups</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="css/landing.css">
</head>
<body>
<% if (request.getParameter("success") != null) {%>
    <script>alert('Results released successfully');</script>  
<% } %>
<% if (request.getParameter("failure") != null) {%>
    <script>alert('Email notification failed. Please try again...');</script>  
<% } %>
<div id="wrapper">
    <!-- Sidebar -->
    <div id="sidebar-wrapper">
        <ul class="sidebar-nav">
        <li><a href="adminHome.jsp">Home Page</a></li>
            <li><a href="studentPreferences.jsp">Student Preferences</a></li>
            <li><a href="runAlgorithm.jsp">Run Algorithm</a></li>
            <li><a href="formedGroups.jsp" class="active">Formed Groups</a></li>
            <li><a href="logout.jsp">Logout</a></li>
        </ul>
    </div>
<%
DepartmentDAO departmentsDAO = new DepartmentDAO();
List<String> departments = departmentsDAO.getAllDepartmentsForTopic();
String selectedDepartment = request.getParameter("department");
String name = session.getAttribute("Name").toString();
    List<Group> groups = new ArrayList<Group>();
    List<String> emails = new ArrayList<String>();
    if(selectedDepartment != null && !selectedDepartment.isEmpty()){
    	GroupsDAO gd = new GroupsDAO();
    	groups = gd.getAllGroups(selectedDepartment);
    	emails = gd.getStudentAndSupervisorEmails(selectedDepartment);
    }
    
%>
    <!-- Page Content -->
    <div id="page-content-wrapper">
        <div class="container-fluid">
            <a href="#" class="btn btn-secondary" id="menu-toggle"><i class="fas fa-bars"></i></a>
            <div class="row">
                <div class="col-lg-12">
                <h1>Formed Groups</h1>
                <form action="formedGroups.jsp" method="get">
            <div class="form-group">
            <label for="departmentSelect">Select Department:</label>
            <select class="form-control" id="departmentSelect" name="department">
    <option value="All">All</option>
    <% for(String dept : departments) { 
    String selected = dept.equals(selectedDepartment) ? "selected" : "";%>
        <option value="<%= dept %>" <%= selected %>><%= dept %></option>
    <% } %>
</select>
</div>
<button type="submit" class="btn btn-primary">Filter</button>
</form>
            
                <%if(selectedDepartment != null && !selectedDepartment.isEmpty() && groups != null && !groups.isEmpty()){ %>
                <div class="table-container" style="display: block;">
                    <table class="table table-bordered">
                        <thead class="thead-light">
                            <tr>
                                <th>Group Name</th>
                                <th>Topic Name</th>
                                <th>Students</th>
                                <th>Supervisor</th>
                                <th>Second Marker</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Group group : groups) { %>
                            <tr>
                                <td><%= group.getGroupName() %></td>
                                <td><%= group.getTopicName() %></td>
                                <td>
                                    <ul>
                                        <% for (String student : group.getStudents()) { %>
                                        <li><%= student %></li>
                                        <% } %>
                                    </ul>
                                </td>
                                <td><%= group.getSupervisorName() != null ? group.getSupervisorName() :  "Supervisor could not be allocated"%></td>
                                <td><%= group.getSecondMarkerName() != null ? group.getSecondMarkerName() : "Second Marker could not be allocated" %></td>

                            </tr>
                            <% }%>
                        </tbody>
                    </table>
                </div>
                <form action="releaseResults" method="post">
    <div class="text-center">
        <% for (String email : emails) { %>
            <input type="hidden" name="emails" value="<%= email %>">
        <% } %>
        <button type="submit" class="btn btn-success">Release Results and Notify</button>
    </div>
</form>
<br>
                
                <%} else if(selectedDepartment == null || selectedDepartment.isEmpty()){%>
                <h3 style="color: grey; text-align: center">Please select a department</h3>
        <% } else if(selectedDepartment != null && !selectedDepartment.isEmpty() && (groups == null || groups.isEmpty())){ %>
        <h3 style="color: grey; text-align: center">No Groups to display</h3>
        <%} %>
        
                </div>
            </div>
        </div>
    </div>
    </div>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js"></script>
<script src="js/sidebar.js"></script>
</body>
</html>