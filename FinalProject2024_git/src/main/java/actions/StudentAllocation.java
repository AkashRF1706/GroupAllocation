package actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jgrapht.Graph;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.json.JSONObject;

import database.MySQLConnection;

@WebServlet("/RunAlgorithm")
public class StudentAllocation extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected synchronized void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        JSONObject jsonResponse = new JSONObject();
		try {
		
		int minGroupSize = 3;
		int maxGroupSize = Integer.parseInt(req.getParameter("maxStudentsPerGroup"));
		int numStudents = Integer.parseInt(req.getParameter("numStudents"));
		System.out.println(numStudents);
		int topicUseLimit = Integer.parseInt(req.getParameter("topicUseLimit"));
		String department = req.getParameter("department");
		String deptUrl = department.replace(" ", "+");
		System.out.println(department);
		
		if(maxGroupSize < 4 || maxGroupSize > numStudents) {
			//Send response to admin if maxgroupsize is invalid
			jsonResponse.put("status", "success");
			jsonResponse.put("message", "Max Students per group must be between 4 and " +numStudents);
            jsonResponse.put("redirectUrl", "runAlgorithm.jsp?department="+deptUrl);
            out.print(jsonResponse.toString());
    		out.flush();
    		out.close();
		} if(topicUseLimit < 1 || topicUseLimit > 5) {
			//Send response to admin if topicUseLimit is invalid
			jsonResponse.put("status", "success");
			jsonResponse.put("message", "Topic Use Limit must be between 1 and 5");
            jsonResponse.put("redirectUrl", "runAlgorithm.jsp?department="+deptUrl);
            out.print(jsonResponse.toString());
    		out.flush();
    		out.close();
		}

		Map<Integer, List<String>> students = new HashMap<>();
		Map<Integer, List<String>> supervisors = new HashMap<>();
		Map<Integer, Integer> supervisorCapacities = new HashMap<>();
		List<String> topics = new ArrayList<>();
		Connection conn = null;
		try {
			conn = MySQLConnection.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			loadStudents(conn, students, department);
			System.out.println(students.toString());
			loadTopics(conn, topics, department);
			loadSupervisors(conn, supervisors, supervisorCapacities, department);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}

		// Initialize graph
        Graph<String, DefaultWeightedEdge> G = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        setupGraph(G, students, supervisors, topics, supervisorCapacities, maxGroupSize, topicUseLimit);
        System.out.println("Vertex and edges: " + G.toString());

     // Compute max flow
        EdmondsKarpMFImpl<String, DefaultWeightedEdge> maxFlow = new EdmondsKarpMFImpl<>(G);
        double flow = maxFlow.calculateMaximumFlow("source", "sink");
        Map<DefaultWeightedEdge, Double> maximumFlow = maxFlow.getFlowMap();
        System.out.println("Maxflow:");
        for (Map.Entry<DefaultWeightedEdge, Double> entry : maximumFlow.entrySet()) {
            System.out.println("Edge: " + entry.getKey() + ", Flow: " + entry.getValue());
        }

        
     // Get allocation results
        Map<String, Object[]> topicGroupCount = analyzeFlow(G, maximumFlow, topics, students, supervisors, maxGroupSize);

     // Output the allocation
        System.out.println("Before Redistribution:");
        printAllocation(topicGroupCount);
        
     // Redistribute students   
        Redistribution redistribution = new Redistribution();
        topicGroupCount = redistribution.redistributeStudents(topicGroupCount, students, minGroupSize, maxGroupSize, topicUseLimit);
        
        System.out.println("------------------------------");
        System.out.println("After Redistribution:");
        printAllocation(topicGroupCount);
        
        List<Integer> unallocatedStudents = new ArrayList<Integer>();
        unallocatedStudents = getUnallocatedStudents(topicGroupCount, minGroupSize);
        
        if(unallocatedStudents == null || unallocatedStudents.isEmpty()) {
        	jsonResponse.put("status", "success");
        	jsonResponse.put("message", "Student could not be allocated optimally. Please modify the student preferences.");
        	jsonResponse.put("redirectUrl", "sendEmails.jsp?department=" + department + "&studentIds=" + unallocatedStudents.toString().replaceAll("[\\[\\] ]", ""));
        	out.print(jsonResponse.toString());
    		out.flush();
    		out.close();
        }
        
        // Supervisors allocating algorithm
        GroupManagement gm = new GroupManagement();
        Map<String, List<Object>> formedGroup = new HashMap<String, List<Object>>();
        formedGroup = gm.allocateSupervisorsAndCreateGroups(topicGroupCount, supervisors, supervisorCapacities);

        List<Integer> supervisorsList = new ArrayList<Integer>();
        List<Integer> secondMarkersList = new ArrayList<Integer>();
        
        formedGroup.forEach((groupId, details) -> {
        	supervisorsList.add(Integer.parseInt(details.get(1).toString()));
        	secondMarkersList.add(Integer.parseInt(details.get(2).toString()));
        });
        
        if(formedGroup != null && !formedGroup.isEmpty()) {
        	// DB Upload
        	uploadGroupDetails(formedGroup, conn, department);
        }
        
        if(!supervisorsList.contains(-1) && !secondMarkersList.contains(-1)) {
        	jsonResponse.put("status", "success");
            jsonResponse.put("message", "Algorithm run successfully!");
            jsonResponse.put("redirectUrl", "formedGroups.jsp?department="+department);
        } else {
        	// Message to admin
        	jsonResponse.put("status", "success");
            jsonResponse.put("message", "Cannot be allocated optimally. Check the preferences and run again.");
            jsonResponse.put("redirectUrl", "formedGroups.jsp?department="+department);
        }
		} catch (Exception e) {
			// TODO: handle exception
			jsonResponse.put("status", "error");
            jsonResponse.put("message", "Error running algorithm: " + e.getMessage());
            jsonResponse.put("redirectUrl", "runAlgorithm.jsp");
		}
		out.print(jsonResponse.toString());
		out.flush();
		out.close();
	}

	private static void loadStudents(Connection conn, Map<Integer, List<String>> students, String department) throws Exception {
		String query = "SELECT p.student_id, p.preferences "
				+ " FROM preferences p"
				+ " JOIN students s ON p.student_id = s.student_id "
				+ " WHERE s.department = ?";
		try { 
			PreparedStatement ptst = conn.prepareStatement(query); 
			ptst.setString(1, department);
			ResultSet rs = ptst.executeQuery();
			while (rs.next()) {
				int studentId = rs.getInt("student_id");
				String preferences = rs.getString("preferences");
				List<String> prefs = Arrays.stream(preferences.split(",")).map(topicId -> "t" + topicId)
						.collect(Collectors.toList());
				students.put(studentId, prefs);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private static void loadTopics(Connection conn, List<String> topics, String department) throws Exception {		 
		String query = "SELECT concat('t', topic_id) AS topicID FROM topics where is_available = 'Y' and department = ?";
	try {
		PreparedStatement ptst1 = conn.prepareStatement(query); 
		ptst1.setString(1, department);
		ResultSet rs1 = ptst1.executeQuery();
			while (rs1.next()) {
				topics.add(rs1.getString("topicID"));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private static void loadSupervisors(Connection conn, Map<Integer, List<String>> supervisors,
			Map<Integer, Integer> supervisorCapacities, String department) throws Exception {
		String query = "SELECT sp.staff_id, sp.numgroups, sp.topics "
				+ " FROM supervisor_prefs sp "
				+ " JOIN staff s ON sp.staff_id = s.staff_id "
				+ " WHERE s.department = ?";
		try { 
			PreparedStatement ptst = conn.prepareStatement(query); 
			ptst.setString(1, department);
			ResultSet rs = ptst.executeQuery();
			while (rs.next()) {
				int supervisorId = rs.getInt("staff_id");
				int numGroups = rs.getInt("numgroups");
				String dbTopics = rs.getString("topics");
				List<String> supervisorTopics = Arrays.stream(dbTopics.split(",")).map(s -> "t" + s)
						.collect(Collectors.toList());
				supervisors.put(supervisorId, supervisorTopics);
				supervisorCapacities.put(supervisorId, numGroups);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private static void setupGraph(Graph<String, DefaultWeightedEdge> G, Map<Integer, List<String>> students, Map<Integer, List<String>> supervisors, List<String> topics, Map<Integer, Integer> supervisorCapacities, int maxGroupSize, int topicUseLimit) {System.out.println("Adding vertices and edges...");

// Add source and sink vertices
G.addVertex("source");
G.addVertex("sink");

students.keySet().forEach(s -> {
    String studentVertex = "s" + s;
    G.addVertex(studentVertex); //Add Student vertex
    G.addEdge("source", studentVertex);
    G.setEdgeWeight(G.getEdge("source", studentVertex), 1); //Add edge between source and student with weight as 1
    System.out.println("Added edge from source to " + studentVertex);
});

topics.forEach(t -> {
    G.addVertex(t); // Add topic vertex
    G.addEdge(t, "sink");
    G.setEdgeWeight(G.getEdge(t, "sink"), 1); // Add edge between topics and sink with weight 1
    System.out.println("Added edge from " + t + " to sink");
});

supervisors.keySet().forEach(supervisorId -> {
    String supervisorVertex = "supervisor" + supervisorId;
    G.addVertex(supervisorVertex); // Add supervisor vertex
    G.addEdge(supervisorVertex, "sink");
 // Add edge between supervisor and sink with supervisor capacity as weight
    G.setEdgeWeight(G.getEdge(supervisorVertex, "sink"), supervisorCapacities.get(supervisorId)); 
    System.out.println("Added edge from " + supervisorVertex + " to sink");
});

students.forEach((studentId, prefs) -> {
    for (int i = 0; i < prefs.size(); i++) {
        String pref = prefs.get(i);
        if (G.containsVertex("s" + studentId) && G.containsVertex(pref)) {
            DefaultWeightedEdge edge = G.addEdge("s" + studentId, pref); //Add edge between student and chosen topics
            if (edge != null) {
                double weight = 5 - (i + 1);
                G.setEdgeWeight(edge, weight); //Set higher weight for high preference 
                System.out.println("Added edge from s" + studentId + " to " + pref + " with weight " + weight);
            }
        }
    }
});

supervisors.forEach((supervisorId, supervisorTopics) -> {
    supervisorTopics.forEach(topic -> {
        if (G.containsVertex("supervisor" + supervisorId) && G.containsVertex(topic)) {
            DefaultWeightedEdge edge = G.addEdge(topic, "supervisor" + supervisorId); // Add edge between topic and supervisor
            if (edge != null) {
            	// Set supervisor capacity as weight
                G.setEdgeWeight(edge, supervisorCapacities.get(supervisorId));
                System.out.println("Added edge from " + topic + " to supervisor" + supervisorId);
            }
        }
    });
});

System.out.println("Setup complete, running flow algorithm...");
}

	private static Map<String, Object[]> analyzeFlow(Graph<String, DefaultWeightedEdge> G, Map<DefaultWeightedEdge, Double> flowMap, List<String> topics, Map<Integer, List<String>> students, Map<Integer, List<String>> supervisors, int maxGroupSize) {
        Map<String, Object[]> topicGroupCount = new HashMap<>();
        topics.forEach(topic -> {
            List<List<Integer>> assignedStudentsLists = new ArrayList<>();
            List<Integer> assignedStudents = new ArrayList<>();
            int assignedSupervisor = findAssignedSupervisor(G, flowMap, topic, supervisors);
            students.keySet().forEach(studentId -> {
                if (flowMap.getOrDefault(G.getEdge("s" + studentId, topic), 0.0) > 0.0) {
                    assignedStudents.add(studentId);
                    if (assignedStudents.size() == maxGroupSize) {
                        assignedStudentsLists.add(new ArrayList<>(assignedStudents));
                        assignedStudents.clear();
                    }
                }
            });
            if (!assignedStudents.isEmpty()) {
                assignedStudentsLists.add(new ArrayList<>(assignedStudents));
            }
            topicGroupCount.put(topic, new Object[]{assignedStudentsLists, assignedSupervisor});
        });
        return topicGroupCount;
    }

    private static int findAssignedSupervisor(Graph<String, DefaultWeightedEdge> G, Map<DefaultWeightedEdge, Double> flowMap, String topic, Map<Integer, List<String>> supervisors) {
        return supervisors.entrySet().stream().filter(entry -> flowMap.getOrDefault(G.getEdge(topic, "supervisor" + entry.getKey()), 0.0) > 0.0).findFirst().map(Map.Entry::getKey).orElse(0);
    }

    public static void printAllocation(Map<String, Object[]> topicGroupCount) {
        topicGroupCount.forEach((topic, details) -> {
            @SuppressWarnings("unchecked")
            List<List<Integer>> studentGroups = (List<List<Integer>>) details[0];
            int supervisorId = (Integer) details[1];
            System.out.println("Topic: " + topic + ", Supervisor ID: " + supervisorId + ", Student Groups: " + studentGroups);
        });
    }
    
    private static void uploadGroupDetails(Map<String, List<Object>> groupDetails, Connection connection, String department) {
        // SQL to clear group_id before inserting new ones
        String clearStudentsGroupSQL = "UPDATE students SET group_id = NULL WHERE department = ? and group_id is not null";
        String clearStaffGroupSQL = "UPDATE staff SET group_id = NULL WHERE department = ? and group_id is not null";

        String insertSQL = "INSERT INTO discussiongroups (group_name, students, supervisor_id, topic_id, second_marker) VALUES (?, ?, ?, ?, ?)";
        String updateStudentsSQL = "UPDATE students SET group_id = ? WHERE student_id = ?";
        String updateStaffSQL = "UPDATE staff SET group_id = ? WHERE staff_id = ?";

        try (
            PreparedStatement clearStudentsStmt = connection.prepareStatement(clearStudentsGroupSQL);
            PreparedStatement clearStaffStmt = connection.prepareStatement(clearStaffGroupSQL);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
            PreparedStatement updateStudentsStmt = connection.prepareStatement(updateStudentsSQL);
            PreparedStatement updateStaffStmt = connection.prepareStatement(updateStaffSQL);
        ) {
            connection.setAutoCommit(false);  // Start transaction
            
         // Clear group_ids for students and staff
            clearStudentsStmt.setString(1, department);
            clearStudentsStmt.executeUpdate();
            
            clearStaffStmt.setString(1, department);
            clearStaffStmt.executeUpdate();

            for (Map.Entry<String, List<Object>> entry : groupDetails.entrySet()) {
                String groupId = entry.getKey();
                List<Integer> studentsList = (List<Integer>) entry.getValue().get(0);
                String students = studentsList.stream().map(Object::toString).collect(Collectors.joining(","));
                String supervisorId = entry.getValue().get(1).toString();
                String secondMarkerId = entry.getValue().get(2).toString();
                
                String[] parts = groupId.split("-");
                String topicId = parts[0].substring(1);  // Remove 't' and taking the number

                // Insert into discussiongroups
                preparedStatement.setString(1, groupId);
                preparedStatement.setString(2, students);
                preparedStatement.setInt(3, Integer.parseInt(supervisorId));
                preparedStatement.setInt(4, Integer.parseInt(topicId));
                preparedStatement.setInt(5, Integer.parseInt(secondMarkerId));
                preparedStatement.executeUpdate();

                // Update students with groupId
                for (Integer studentId : studentsList) {
                    updateStudentsStmt.setString(1, groupId);
                    updateStudentsStmt.setInt(2, studentId);
                    updateStudentsStmt.executeUpdate();
                }

                // Update staff with groupId
                updateStaffStmt.setString(1, groupId);
                updateStaffStmt.setInt(2, Integer.parseInt(supervisorId));
                updateStaffStmt.executeUpdate();
            }

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            try {
                connection.rollback();  // Rollback the transaction in case of error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true); // Reset auto-commit to true
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
   private static List<Integer> getUnallocatedStudents(Map<String, Object[]> topicGroupCount, int minGroupSize){
	   List<Integer> unallocatedStudents = new ArrayList<Integer>();
	   
	   try {
		   if (topicGroupCount != null) {
	            topicGroupCount.forEach((topic, details) -> {
	                @SuppressWarnings("unchecked")
	                List<List<Integer>> studentGroups = (List<List<Integer>>) details[0];
	                
	                // Iterate over each group and check if underfilled
	                for (List<Integer> group : studentGroups) {
	                    if (group.size() < minGroupSize) {
	                    	unallocatedStudents.addAll(group);
	                    }
	                }
	            });
		   }
	   }catch (Exception e) {
		// TODO: handle exception
		   e.printStackTrace();
	}
	   return unallocatedStudents;
   }
}