package actions;

import java.util.*;

class Preference {
    int studentId;
    int topicId;
    int rank;

    public Preference(int studentId, int topicId, int rank) {
        this.studentId = studentId;
        this.topicId = topicId;
        this.rank = rank;
    }
}

class Student {
    int id;
    List<Preference> preferences;
    int assignedGroup;

    public Student(int id) {
        this.id = id;
        this.preferences = new ArrayList<>();
        this.assignedGroup = -1;
    }
}

class Group {
    int topic;
    List<Student> students;

    public Group(int topic) {
        this.topic = topic;
        this.students = new ArrayList<>();
    }
}

public class GroupAllocation {
    public static void allocateStudents(List<Preference> preferences, int numTopics) {
        // Create a map of students and their preferences
        Map<Integer, Student> students = new HashMap<>();
        for (Preference pref : preferences) {
            Student student = students.getOrDefault(pref.studentId, new Student(pref.studentId));
            student.preferences.add(pref);
            students.put(pref.studentId, student);
        }

        // Sort preferences for each student
        for (Student student : students.values()) {
            student.preferences.sort(Comparator.comparingInt(p -> p.rank));
        }

        // Create a list of groups for each topic
        List<Group>[] groups = new List[numTopics];
        for (int i = 0; i < numTopics; i++) {
            groups[i] = new ArrayList<>();
        }

        // Assign students to groups using dynamic programming
        for (Student student : students.values()) {
            assignStudentToGroup(student, groups, 0, -1);
        }

        // Remove groups with fewer than 3 students
        for (int i = 0; i < numTopics; i++) {
            groups[i].removeIf(group -> group.students.size() < 3);
        }

        // Print the groups
        for (int i = 0; i < numTopics; i++) {
            if(groups[i].isEmpty()) {
            	continue;
            }
            for (Group group : groups[i]) {
            	System.out.println("Topic " + (i + 1) + ":");
                System.out.print("Group: ");
                for (Student student : group.students) {
                    System.out.print(student.id + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    private static void assignStudentToGroup(Student student, List<Group>[] groups, int prefIndex, int prevGroup) {
        if (prefIndex == student.preferences.size()) {
            return;
        }

        Preference pref = student.preferences.get(prefIndex);
        List<Group> topicGroups = groups[pref.topicId - 1];

        int maxSatisfaction = Integer.MIN_VALUE;
        int bestGroup = -1;

        // Try assigning student to an existing group
        for (int i = 0; i < topicGroups.size(); i++) {
            Group group = topicGroups.get(i);
            if (group.students.size() < 5 && i != prevGroup) {
                group.students.add(student);
                int satisfaction = calculateSatisfaction(groups);
                if (satisfaction > maxSatisfaction) {
                    maxSatisfaction = satisfaction;
                    bestGroup = i;
                }
                group.students.remove(student);
            }
        }

        // Try creating a new group for the student
        Group newGroup = new Group(pref.topicId);
        newGroup.students.add(student);
        int satisfaction = calculateSatisfaction(groups);
        if (satisfaction > maxSatisfaction) {
            maxSatisfaction = satisfaction;
            bestGroup = topicGroups.size();
            topicGroups.add(newGroup);
        } else {
            newGroup.students.clear();
        }

        // Assign student to the best group and recurse for the next preference
        if (bestGroup != -1) {
            if (bestGroup == topicGroups.size()) {
                topicGroups.add(newGroup);
            } else {
                topicGroups.get(bestGroup).students.add(student);
            }
            student.assignedGroup = bestGroup;
            assignStudentToGroup(student, groups, prefIndex + 1, bestGroup);
        } else {
            assignStudentToGroup(student, groups, prefIndex + 1, prevGroup);
        }
    }

    private static int calculateSatisfaction(List<Group>[] groups) {
        int satisfaction = 0;
        for (List<Group> topicGroups : groups) {
            for (Group group : topicGroups) {
                int minRank = Integer.MAX_VALUE;
                for (Student student : group.students) {
                    for (Preference pref : student.preferences) {
                        if (pref.topicId == group.topic) {
                            minRank = Math.min(minRank, pref.rank);
                            break;
                        }
                    }
                }
                satisfaction += minRank;
            }
        }
        return satisfaction;
    }

    public static void main(String[] args) {
        List<Preference> preferences = new ArrayList<>();
        preferences.add(new Preference(2, 3, 2));
        preferences.add(new Preference(2, 8, 1));
        preferences.add(new Preference(2, 11, 3));
        preferences.add(new Preference(2, 14, 4));
        preferences.add(new Preference(1, 12, 2));
        preferences.add(new Preference(1, 15, 3));
        preferences.add(new Preference(1, 8, 1));
        preferences.add(new Preference(1, 19, 4));
        preferences.add(new Preference(3, 5, 1));
        preferences.add(new Preference(3, 10, 2));
        preferences.add(new Preference(3, 15, 3));
        preferences.add(new Preference(3, 8, 4));

        allocateStudents(preferences, 20);
    }
}