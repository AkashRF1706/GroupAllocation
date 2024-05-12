<%@page import="database.SupervisorsDAO"%>
<%@page import="database.StudentsDAO"%>
<%@page import="model.Student"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.*, database.GroupsDAO, database.DepartmentDAO"%>
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
            <li><a href="supervisorPreferences.jsp">Supervisor Preferences</a></li>
            <li><a href="runAlgorithm.jsp">Run Algorithm</a></li>
            <li><a href="formedGroups.jsp" class="active">Formed Groups</a></li>
            <li><a href="sendEmails.jsp">Send Email</a></li>
            <li><a href="#" data-toggle="modal" onclick="showLogoutModal()">Logout</a></li>
        </ul>
    </div>
<%
DepartmentDAO departmentsDAO = new DepartmentDAO();
List<String> departments = departmentsDAO.getAllDepartmentsForTopic();
String selectedDepartment = request.getParameter("department");
String name = session.getAttribute("Name").toString();
    List<Group> groups = new ArrayList<Group>();
    List<String> emails = new ArrayList<String>();
    List<Student> students = new ArrayList<Student>();
    List<Supervisor> supervisors = new ArrayList<Supervisor>();
    if(selectedDepartment != null && !selectedDepartment.isEmpty()){
    	GroupsDAO gd = new GroupsDAO();
    	groups = gd.getAllGroups(selectedDepartment);
    	emails = gd.getStudentAndSupervisorEmails(selectedDepartment);
    	StudentsDAO sd = new StudentsDAO();
    	SupervisorsDAO spd = new SupervisorsDAO();
    	students = sd.getAllStudents(selectedDepartment);
    	supervisors = spd.getAllSupervisors(selectedDepartment);
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
        <button type="submit" id="releaseResultsBtn" class="btn btn-success">Release Results and Notify</button>
        </div>
        </form>
        <br>
        <div class="text-center">
         <button id="manualStudentAllocationBtn" class="btn btn-info">Manually Allocate Student</button>
<button id="manualSupervisorAllocationBtn" class="btn btn-info">Manually Allocate Supervisor/Second Marker</button>
   </div>     
   <br>
        <!-- Student Allocation Dropdowns -->
<div id="studentDropdown" style="display:none;">
    <select class="form-control" id="studentSelect">
        <%
        for(Student student : students) { %>
            <option value="<%= student.getId() %>"><%= student.getName() %></option>
        <% } %>
    </select><br>
    <select class="form-control" id="studentGroupSelect">
        <% // Loop through groups
        for(Group group : groups) { %>
            <option value="<%= group.getGroupName() %>"><%= group.getGroupName() %></option>
        <% } %>
    </select><br>
    <button class="btn btn-primary allocationSubmit" data-action="studentAllocateServlet" data-select="student">Update</button> &nbsp
    <button id="cancelButton" type="button" class="btn btn-primary" onclick="cancelButton()">Cancel</button>
</div>

<!-- Supervisor Allocation Dropdowns -->
<div id="supervisorDropdown" style="display:none;">
    <select class="form-control" id="supervisorSelect">
        <%
        for(Supervisor supervisor : supervisors) { %>
            <option value="<%= supervisor.getId() %>"><%= supervisor.getName() %></option>
        <% } %>
    </select><br>
    <select class="form-control" id="supervisorGroupSelect">
        <% // Loop through groups
        for(Group group : groups) { %>
            <option value="<%= group.getGroupName() %>"><%= group.getGroupName() %></option>
        <% } %>
    </select><br>
    <select class="form-control" id="supervisorSecondMarkerSelect">
    <option value="Supervisor">Supervisor</option>
    <option value="Second Marker">Second Marker</option>
    </select><br>
    <button class="btn btn-primary allocationSubmit" data-action="supervisorAllocateServlet" data-select="supervisor">Update</button> &nbsp
    <button id="cancelButton" type="button" class="btn btn-primary" onclick="cancelButton()">Cancel</button>
</div>
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
    // Toggle buttons and dropdown visibility
    function toggleAllocationOptions(disable) {
        $('#manualStudentAllocationBtn, #manualSupervisorAllocationBtn, #releaseResultsBtn').prop('disabled', disable);
        if (!disable) {
            $('#studentDropdown, #supervisorDropdown').hide();
        }
    }

    $('#manualStudentAllocationBtn').click(function() {
        toggleAllocationOptions(true);
        $('#studentDropdown').show();
        $('#supervisorDropdown').hide();
    });

    $('#manualSupervisorAllocationBtn').click(function() {
        toggleAllocationOptions(true);
        $('#supervisorDropdown').show();
        $('#studentDropdown').hide();
    });

    // Function to handle form submissions for allocations
    $('.allocationSubmit').click(function(e) {
        e.preventDefault();
        var actionUrl = $(this).data('action');
        var userType = $(this).data('select');
        var userId = $('#' + userType + 'Select').val();
        var groupId = $('#' + userType + 'GroupSelect').val();
        var supervisorSecondMarker = $('#' + userType + 'SecondMarkerSelect').val();

        $.ajax({
            url: actionUrl,
            type: 'POST',
            data: { userId: userId, groupId: groupId, supervisorSecondMarker: supervisorSecondMarker },
            success: function(response) {
                alert('Allocation successful.');
                toggleAllocationOptions(false);
                location.reload();
            },
            error: function() {
                alert('Error in allocation. Please try again.');
                toggleAllocationOptions(false);
            }
        });
    });

    // Hide dropdowns initially
    $('#studentDropdown, #supervisorDropdown').hide();
});

function cancelButton() {
    location.reload();
}
</script>

</body>
</html>