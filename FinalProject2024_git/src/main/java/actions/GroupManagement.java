package actions;

import java.util.*;

public class GroupManagement {

    public Map<String, List<Object>> allocateSupervisorsAndCreateGroups(Map<String, Object[]> topicGroupCount, Map<Integer, List<String>> supervisors, Map<Integer, Integer> supervisorCapacities) {
        Map<String, List<Object>> groupDetails = new HashMap<>();
        Set<Integer> usedSecondMarkers = new HashSet<>();

        // Pre-decrement the capacities for all original supervisors
        topicGroupCount.values().forEach(details -> {
            int supervisorId = (Integer) details[1];
            if (supervisorCapacities.containsKey(supervisorId)) {
                supervisorCapacities.put(supervisorId, supervisorCapacities.get(supervisorId) - 1);
            }
        });

        topicGroupCount.forEach((topic, details) -> {
            @SuppressWarnings("unchecked")
            List<List<Integer>> studentGroups = (List<List<Integer>>) details[0];
            int originalSupervisorId = (Integer) details[1];

            for (int i = 0; i < studentGroups.size(); i++) {
                String uniqueGroupId = topic + "-" + (i + 1);
                List<Integer> groupStudents = studentGroups.get(i);
                List<Object> groupInfo = new ArrayList<>();

                int assignedSupervisorId = originalSupervisorId;
                if (i == 0) {
                    if (supervisorCapacities.get(originalSupervisorId) != null && supervisorCapacities.get(originalSupervisorId) > 0) {
                    	//Add algorithm allocated supervisor to group if capacity > 0
                        groupInfo.add(groupStudents);
                        groupInfo.add(originalSupervisorId);
                    } else {
                    	//Else find available supervisor
                        Optional<Integer> newSupervisorId = findSuitableSupervisor(supervisors, supervisorCapacities, topic, originalSupervisorId);
                        if (newSupervisorId.isPresent()) {
                            assignedSupervisorId = newSupervisorId.get();
                            supervisorCapacities.put(assignedSupervisorId, supervisorCapacities.get(assignedSupervisorId) - 1); // Update supervisor capacity
                        }
                        groupInfo.add(groupStudents);
                        groupInfo.add(assignedSupervisorId);
                    }
                } else {
                	//Find new supervisor if single topic has multiple groups 
                    Optional<Integer> newSupervisorId = findSuitableSupervisor(supervisors, supervisorCapacities, topic, originalSupervisorId);
                    if (newSupervisorId.isPresent()) {
                        assignedSupervisorId = newSupervisorId.get();
                        supervisorCapacities.put(assignedSupervisorId, supervisorCapacities.get(assignedSupervisorId) - 1);
                        groupInfo.add(groupStudents);
                        groupInfo.add(assignedSupervisorId);
                    } else if(supervisorCapacities.get(originalSupervisorId) > 0) {
                    	//Add the original supervisor if capacity and no new supervisor could be found and
                    	groupInfo.add(groupStudents);
                    	groupInfo.add(assignedSupervisorId);
                    } else {
                    	//If no supervisor found allot -1 as flag
						groupInfo.add(studentGroups);
						groupInfo.add("-1");
					}
                }

                // Assign a second marker
                Optional<Integer> secondMarkerId = findSecondMarker(supervisors, usedSecondMarkers, topic, assignedSupervisorId);
                secondMarkerId.ifPresent(usedSecondMarkers::add);
                groupInfo.add(secondMarkerId.orElse(-1));

                groupDetails.put(uniqueGroupId, groupInfo);
            }
        });

        // Output the full allocation for verification
        groupDetails.forEach((groupId, details) -> {
            System.out.println("Group ID: " + groupId + ", Students: " + details.get(0) + ", Supervisor: " + details.get(1) + ", Second Marker: " + details.get(2));
        });
        
        return groupDetails;
    }

    private static Optional<Integer> findSuitableSupervisor(Map<Integer, List<String>> supervisors, Map<Integer, Integer> supervisorCapacities, String topic, int excludeId) {
    	 //Check for supervisor capacity and exclude the original supervisor
    	return supervisors.entrySet().stream()
                .filter(entry -> entry.getValue().contains(topic) && supervisorCapacities.get(entry.getKey()) > 0 && entry.getKey() != excludeId)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private static Optional<Integer> findSecondMarker(Map<Integer, List<String>> supervisors, Set<Integer> usedSecondMarkers, String topic, int excludeId) {
        return supervisors.entrySet().stream()
                .filter(entry -> entry.getValue().contains(topic) && !usedSecondMarkers.contains(entry.getKey()) && entry.getKey() != excludeId)
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
