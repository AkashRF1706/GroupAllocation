package actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.alg.util.Pair;

public class Redistribution {

	public Map<String, Object[]> redistributeStudents(Map<String, Object[]> topicGroupCount,
			Map<Integer, List<String>> students, int minGroupSize, int maxGroupSize, int topicUseLimit) {
		// Track which students have been reallocated to print or log them later if
		// needed
		Map<Integer, String> reallocatedStudents = new HashMap<>();
		List<Integer> studentsNeedingReallocation = new ArrayList<>();

		topicGroupCount.forEach((topic, details) -> {
			@SuppressWarnings("unchecked")
			List<List<Integer>> groups = (List<List<Integer>>) details[0];
			List<List<Integer>> groupsToRemove = new ArrayList<>();
			for (List<Integer> group : groups) {
				if (group.size() < minGroupSize) {
					// Attempt to redistribute each student in underfilled groups
					for (Integer studentId : new ArrayList<>(group)) { // Copy to avoid concurrent modification
						for (String preference : students.get(studentId)) {
							if (tryReallocateStudentToGroup(preference, studentId, topicGroupCount, minGroupSize,
									maxGroupSize, topicUseLimit)) {
								group.remove(studentId); // Remove from current group if reallocated
								reallocatedStudents.put(studentId, preference); // Track successful reallocation
								break; // Break after successful reallocation
							}
						}
					}
					// If group is empty after redistribution, mark it for removal
					if (group.isEmpty()) {
						groupsToRemove.add(group);
					}
				}
			}
			// Remove empty groups from the topic
			groups.removeAll(groupsToRemove);
		});

		// Optionally, log or print reallocated students and their new groups
		reallocatedStudents.forEach((studentId, newGroup) -> System.out
				.println("Student " + studentId + " was reallocated to " + newGroup));

		Map<Integer, Pair<String, Integer>> originalGroupMapping = new HashMap<>();

		// Identify students in underfilled groups and their current group
		topicGroupCount.forEach((topic, details) -> {
			@SuppressWarnings("unchecked")
			List<List<Integer>> groups = (List<List<Integer>>) details[0];
			for (int groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
				final int finalGroupIndex = groupIndex;
				List<Integer> group = groups.get(groupIndex);
				if (group.size() < minGroupSize) {
					group.forEach(studentId -> {
						
						Pair<String, Integer> pair = Pair.of(topic, finalGroupIndex); 
						// Record the student's original group
						originalGroupMapping.put(studentId, pair);
					});
				}
			}
		});

		List<Integer> underfilledStudents = new ArrayList<Integer>();
		// Attempt to reallocate each student
		originalGroupMapping.forEach((studentId, originalGroupInfo) -> {
			String originalTopic = originalGroupInfo.getFirst();
			int originalGroupIndex = originalGroupInfo.getSecond();
			List<String> preferences = students.get(studentId);

			if (tryToReallocateStudent(studentId, preferences, topicGroupCount, minGroupSize, maxGroupSize)) {
				// If reallocated successfully, remove the student from their original group
				@SuppressWarnings("unchecked")
				List<List<Integer>> originalGroups = (List<List<Integer>>) topicGroupCount.get(originalTopic)[0];
				if (originalGroupIndex < originalGroups.size()) {
					originalGroups.get(originalGroupIndex).remove(studentId);
				}
				StudentAllocation sa = new StudentAllocation();
				System.out.println("2nd Redistribution");
				sa.printAllocation(topicGroupCount);
			} else {
				System.out.println(studentId + " could not be allocated into any group");
				if (!underfilledStudents.contains(studentId)) {
					underfilledStudents.add(studentId);
				}
				// attemptComplexRedistribution(studentId, topicGroupCount, students,
				// minGroupSize, maxGroupSize);
			}
		});
		StudentAllocation sa = new StudentAllocation();
		System.out.println("2nd Redistribution");
		sa.printAllocation(topicGroupCount);
		if (!underfilledStudents.isEmpty()) {
			for (int student : underfilledStudents) {
				if(attemptComplexRedistribution(student, topicGroupCount, students, minGroupSize, maxGroupSize, topicUseLimit)) {
					break;
				}
			}
		}

		Iterator<Map.Entry<String, Object[]>> it = topicGroupCount.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object[]> entry = it.next();
			@SuppressWarnings("unchecked")
			List<List<Integer>> groups = (List<List<Integer>>) entry.getValue()[0];
			groups.removeIf(List::isEmpty);
			if (groups.isEmpty()) {
				it.remove();
			}
		}

		return topicGroupCount;
	}

	public boolean tryReallocateStudentToGroup(String topic, Integer studentId, Map<String, Object[]> topicGroupCount,
			int minGroupSize, int maxGroupSize, int topicUseLimit) {

		Object[] details = topicGroupCount.get(topic);

		@SuppressWarnings("unchecked")
		List<List<Integer>> groups = (List<List<Integer>>) details[0];
		// Try to add to existing group
		for (List<Integer> group : groups) {
			if (group.size() < maxGroupSize) {
				group.add(studentId);
				return true;
			}
		}
		// Check if a new group can be created under the topic use limit
		if (groups.size() < topicUseLimit) {
			List<Integer> newGroup = new ArrayList<>();
			newGroup.add(studentId);
			groups.add(newGroup);
			return true;
		}
		// If the student couldn't be reallocated to any group within their preferences
		System.out.println("Cannot allocate StudentID " + studentId);
		return false; // No suitable group found or created
	}

	private boolean tryToReallocateStudent(Integer studentId, List<String> preferences,
			Map<String, Object[]> topicGroupCount, int minGroupSize, int maxGroupSize) {
		for (String preference : preferences) {
			Object[] details = topicGroupCount.get(preference);
			if (details == null)
				continue;

			@SuppressWarnings("unchecked")
			List<List<Integer>> groups = (List<List<Integer>>) details[0];
			for (List<Integer> group : groups) {
				if (group.size() >= minGroupSize && group.size() < maxGroupSize) {
					group.add(studentId);
					return true; // Student successfully reallocated
				}
			}
		}
		return false; // No suitable group found
	}

	private boolean attemptComplexRedistribution(Integer unallocatedStudentId, Map<String, Object[]> topicGroupCount,
			Map<Integer, List<String>> students, int minGroupSize, int maxGroupSize, int topicUseLimit) {
		List<String> unallocatedStudentPrefs = students.get(unallocatedStudentId);
		Map<String, List<Integer>> potentialNewGroups = new HashMap<String, List<Integer>>();

		boolean reallocated = false;
		boolean redistributionDone = false;

		// Iterate over all topics and their groups
		outerloop: for (Map.Entry<String, Object[]> entry : topicGroupCount.entrySet()) {
			String topic = entry.getKey();
			@SuppressWarnings("unchecked")
			List<List<Integer>> groups = (List<List<Integer>>) entry.getValue()[0];

			for (int i = 0; i < groups.size(); i++) {
				List<Integer> group = groups.get(i);
				if(unallocatedStudentPrefs.contains(topic) && group.size() < maxGroupSize && (group.size() + 1) >= minGroupSize) {
					group.add(unallocatedStudentId);
					removeStudentFromAllGroups(unallocatedStudentId, topicGroupCount, topic, i);
					reallocated = true;
				} if(group.size() >= minGroupSize && reallocated) {
					redistributionDone = true;
					break outerloop;
				}
			}
			for (int i = 0; i < groups.size(); i++) {
				List<Integer> group = groups.get(i);
				// Only consider groups larger than minGroupSize for possible reallocation
				if (group.size() > minGroupSize - 1) {
					for (Integer studentId : new ArrayList<>(group)) {
						// Check if any student in the group shares a preference with the unallocated
						// student
						for (String pref : students.get(studentId)) {
							if (unallocatedStudentPrefs.contains(pref) && pref.equals(topic)) {
								// Try to reallocate the unallocated student to this group
								if (group.size() < maxGroupSize) {
									group.add(unallocatedStudentId);
									reallocated = true;
									removeStudentFromAllGroups(unallocatedStudentId, topicGroupCount, topic, i);
									break outerloop;
								} else {
									// If the group is at max capacity, see if any member can be moved to another
									// group
									for (Integer memberId : new ArrayList<>(group)) {
										if (tryFindAndReallocate(memberId, unallocatedStudentId, topicGroupCount,
												students, minGroupSize, maxGroupSize, topic, i)) {
											group.add(unallocatedStudentId);
											reallocated = true;
											removeStudentFromAllGroups(unallocatedStudentId, topicGroupCount, topic, i);
											break;
										}
									}
								}
							}

						}

					}
				}

			}

		}
		
		boolean allGroupsValid = true;
		
		for (Map.Entry<String, Object[]> entry : topicGroupCount.entrySet()) {
			@SuppressWarnings("unchecked")
			List<List<Integer>> groups = (List<List<Integer>>) entry.getValue()[0];

			for (List<Integer> group : groups) {
		        if (group.size() < minGroupSize) {
		            allGroupsValid = false; 
		            break;
		        }
		    }
			if (!allGroupsValid) {
		        break; 
		    }
		}
		
		if(reallocated && allGroupsValid) {
			redistributionDone = true;
		}

		if (!reallocated) {
			for (Map.Entry<String, Object[]> entry : topicGroupCount.entrySet()) {
				String topic = entry.getKey();
				@SuppressWarnings("unchecked")
				List<List<Integer>> groups = (List<List<Integer>>) entry.getValue()[0];
				for (int i = 0; i < groups.size(); i++) {
					List<Integer> group = groups.get(i);
					if (group.size() > minGroupSize) {
						for (Integer studentId : new ArrayList<>(group)) {
							for (String pref : students.get(studentId)) {
								if (unallocatedStudentPrefs.contains(pref)) {
									if (!potentialNewGroups.containsKey(pref)) {
										potentialNewGroups.put(pref, new ArrayList<>());
									}
									if (!potentialNewGroups.get(pref).contains(unallocatedStudentId)) {
										potentialNewGroups.get(pref).add(unallocatedStudentId);
									}
									if (!potentialNewGroups.get(pref).contains(studentId)) {
										potentialNewGroups.get(pref).add(studentId);
									}
								}
							}
						}
					}
				}
			}
			System.out.println(potentialNewGroups.toString());
			System.out.println("Cannot allocate StudentID " + unallocatedStudentId + " after complex redistribution.");

			Map<Integer, String> studentToOriginalTopic = new HashMap<>();
			Map<Integer, Integer> studentToOriginalGroupIndex = new HashMap<>();

			for (Map.Entry<String, Object[]> entry : topicGroupCount.entrySet()) {
				String topic = entry.getKey();
				@SuppressWarnings("unchecked")
				List<List<Integer>> groups = (List<List<Integer>>) entry.getValue()[0]; 
																						
				// Iterate over each group
				for (int i = 0; i < groups.size(); i++) {
					List<Integer> group = groups.get(i);
					// Iterate over each student in the group
					for (int j = 0; j < group.size(); j++) {
						Integer studentId = group.get(j);
						studentToOriginalTopic.put(studentId, topic);
						studentToOriginalGroupIndex.put(studentId, i);
					}
				}
			}
			Map<String, List<Integer>> availableSpotsInGroups = new HashMap<>();

			// Initialize available spots count
			for (Map.Entry<String, Object[]> entry : topicGroupCount.entrySet()) {
				String topic = entry.getKey();
				@SuppressWarnings("unchecked")
				List<List<Integer>> groups = (List<List<Integer>>) entry.getValue()[0];
				List<Integer> spotsPerGroup = new ArrayList<>();
				for (List<Integer> group : groups) {
					spotsPerGroup.add(group.size());
				}
				availableSpotsInGroups.put(topic, spotsPerGroup);
			}

			// Now attempt to create valid new groups
			Map<String, List<Integer>> updates = new HashMap<>();
			List<Integer> updatesList = new ArrayList<Integer>();
			List<Integer> newCandidateIds = new ArrayList<Integer>();
			newCandidateIds.add(unallocatedStudentId);
			outerloop: for (Map.Entry<String, List<Integer>> entry : potentialNewGroups.entrySet()) {
				String topic = entry.getKey();
				List<Integer> candidateIds = entry.getValue();
				if (candidateIds.size() >= minGroupSize) {
					for (Integer candidateId : candidateIds) {
						if (candidateId == unallocatedStudentId)
							continue;
						String originalTopic = studentToOriginalTopic.get(candidateId);
						List<Integer> spotsList = availableSpotsInGroups.get(originalTopic);
						int studentIndex = studentToOriginalGroupIndex.get(candidateId);

						if (spotsList.get(studentIndex) - 1 >= minGroupSize) {
							spotsList.set(studentIndex, spotsList.get(studentIndex) - 1);
							newCandidateIds.add(candidateId);
						} else {
							break;
						}
						if (newCandidateIds.size() >= minGroupSize) {
							Object details[] = topicGroupCount.get(topic);
							@SuppressWarnings("unchecked")
							List<List<Integer>> topicGroups = (List<List<Integer>>) details[0];
							if (topicGroups.isEmpty()) {
								topicGroups.add(new ArrayList<Integer>());
							}
							for (List<Integer> topicGroup : topicGroups) {
								if ((topicGroup.size() + newCandidateIds.size()) > maxGroupSize
										&& topicGroups.size() < topicUseLimit) {
									updates.put(topic, newCandidateIds);
									break outerloop;
								} else if ((topicGroup.size() + newCandidateIds.size()) <= maxGroupSize) {
									List<Integer> newGroup = new ArrayList<Integer>();
									newGroup.addAll(topicGroup);
									newGroup.addAll(newCandidateIds);
									for(int student : newGroup) {
										removeFromCurrentGroup(topicGroupCount, student, studentToOriginalTopic);
									}
									topicGroup.addAll(newCandidateIds);
									//updatesList.addAll(topicGroup);
									//updates.put(topic, updatesList);
									break outerloop;
								}
							}
						}
					}
				}
			}

			if(!updates.isEmpty()) {
				potentialNewGroups.clear();
				potentialNewGroups.putAll(updates);
				updateTopicGroupCount(topicGroupCount, potentialNewGroups, studentToOriginalTopic, maxGroupSize,
						minGroupSize, topicUseLimit);
			}
			System.out.println(potentialNewGroups.toString());

			StudentAllocation sa = new StudentAllocation();
			System.out.println("LOGGING TGC: ");
			sa.printAllocation(topicGroupCount);
			

		}
		
		return redistributionDone;
	}

	private boolean tryFindAndReallocate(Integer memberId, int unallocatedStudent,
			Map<String, Object[]> topicGroupCount, Map<Integer, List<String>> students, int minGroupSize,
			int maxGroupSize, String excludeTopic, int excludeGroupIndex) {
		List<String> memberPrefs = students.get(memberId);
		boolean reallocated = false;

		// Attempt to find a new group for the member based on their preferences
		for (String preference : memberPrefs) {
			// Skip the current topic to avoid reallocating to the same group
			if (preference.equals(excludeTopic))
				continue;

			Object[] details = topicGroupCount.get(preference);
			if (details == null)
				continue;

			@SuppressWarnings("unchecked")
			List<List<Integer>> groups = (List<List<Integer>>) details[0];

			// Iterate through groups in the preferred topic
			for (int i = 0; i < groups.size(); i++) {
				if (i == excludeGroupIndex && preference.equals(excludeTopic)) {
					// Skip the original group
					continue;
				}

				List<Integer> group = groups.get(i);
				if (group.size() >= minGroupSize && group.size() < maxGroupSize) {
					// Found a suitable group for reallocation
					group.add(memberId);
					reallocated = true;
					break;
				}
			}

			if (reallocated) {
				// Successfully reallocated, now remove the member from the original group
				@SuppressWarnings("unchecked")
				List<List<Integer>> originalGroups = (List<List<Integer>>) topicGroupCount.get(excludeTopic)[0];
				if (!originalGroups.isEmpty() && excludeGroupIndex < originalGroups.size()) {
					List<Integer> originalGroup = originalGroups.get(excludeGroupIndex);
					originalGroup.remove(memberId);
				}
				break;
			}
		}

		return reallocated;
	}

	private void removeStudentFromAllGroups(Integer studentId, Map<String, Object[]> topicGroupCount,
			String excludeTopic, int excludeGroupIndex) {
		// Iterate over all topics and groups to remove the student from any group that
		// is not the new allocation
		topicGroupCount.forEach((topic, details) -> {
			@SuppressWarnings("unchecked")
			List<List<Integer>> groups = (List<List<Integer>>) details[0];
			for (int groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
				if (!(topic.equals(excludeTopic) && groupIndex == excludeGroupIndex)) {
					groups.get(groupIndex).remove(studentId);
				}
			}
		});
	}

	public void updateTopicGroupCount(Map<String, Object[]> topicGroupCount,
			Map<String, List<Integer>> potentialNewGroups, Map<Integer, String> studentToOriginalTopic,
			int maxGroupSize, int minGroupSize, int topicUseLimit) {

		// Add students to new groups as per potentialNewGroups
		for (Map.Entry<String, List<Integer>> entry : potentialNewGroups.entrySet()) {
			String topic = entry.getKey();
			List<Integer> studentIds = entry.getValue();
			if (addToNewGroup(topicGroupCount, topic, studentIds, maxGroupSize, minGroupSize, studentToOriginalTopic, topicUseLimit)) {
				for (Integer studentId : studentIds) {
					removeFromCurrentGroup(topicGroupCount, studentId, studentToOriginalTopic);// Remove students from
																								// their current groups
				}
			}
		}
	}

	private void removeFromCurrentGroup(Map<String, Object[]> topicGroupCount, Integer studentId,
			Map<Integer, String> studentToOriginalTopic) {
		String originalTopic = studentToOriginalTopic.get(studentId);
		if (originalTopic == null)
			return;

		@SuppressWarnings("unchecked")
		List<List<Integer>> groups = (List<List<Integer>>) topicGroupCount.get(originalTopic)[0];
		for (List<Integer> group : groups) {
			if (group.contains(studentId)) {
				group.remove(studentId);
			} else {
				continue;
			}

		}
	}

	private boolean addToNewGroup(Map<String, Object[]> topicGroupCount, String topic, List<Integer> studentIds,
			int maxGroupSize, int minGroupSize, Map<Integer, String> studentToOriginalTopic, int topicUseLimit) {
		@SuppressWarnings("unchecked")
		List<List<Integer>> groups = (List<List<Integer>>) topicGroupCount.get(topic)[0];
		List<List<Integer>> newGroups = new ArrayList<>(); // List to hold new groups

		boolean added = false; // Flag to check if students have been added to a group

		for (List<Integer> group : groups) {
			for (int i = 0; i < studentIds.size(); i++) {
				if (group.contains(studentIds.get(i))) {
					studentIds.remove(i);
					i--;
					continue;
				}
			}
		}
		for (List<Integer> group : groups) {
			if (!studentIds.isEmpty() && studentIds != null && (group.size() + studentIds.size()) <= maxGroupSize) {
				group.addAll(studentIds); // Add to the existing group
				added = true;
				for (int student : studentIds) {
					studentToOriginalTopic.put(student, topic);
				}
				break;
			}
		}

		if (!added && !studentIds.isEmpty() && studentIds != null) { // If students have not been added to any existing
																		// group
			if (studentIds.size() >= minGroupSize && groups.size() < topicUseLimit) { // Check if new groups can be added
				newGroups.add(new ArrayList<>(studentIds)); // Add as a new group to the topic
				added = true;
				for (int student : studentIds) {
					studentToOriginalTopic.put(student, topic);
				}
			} else {
				System.out.println("Groups full");
			}
		}

		if (!newGroups.isEmpty()) {
			groups.addAll(newGroups); // Add new groups to the main list of groups
		}
		return added;
	}

}