package model;

import java.util.Arrays;

public class Group {
    private String groupName;
    private String supervisorName;
    private String secondMarkerName;
    private String topicName;
    private String[] students;
    
    public Group(String groupName, String supervisorName, String secondMarkerName, String topicName, String[] students) {
        this.groupName = groupName;
        this.supervisorName = supervisorName;
        this.secondMarkerName = secondMarkerName;
        this.topicName = topicName;
        this.students = students;
    }
    
    // Getters and setters
    
    public String getGroupName() {
        return groupName;
    }
    
    public String getSupervisorName() {
        return supervisorName;
    }
    
    public String getSecondMarkerName() {
        return secondMarkerName;
    }
    
    public String[] getStudents() {
        return students;
    }
    
    
    
    public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	@Override
    public String toString() {
        return "Group [groupName=" + groupName + ", supervisorName=" + supervisorName + ", secondMarkerName="
                + secondMarkerName + ", topicName= " + topicName +",students=" + Arrays.toString(students) + "]";
    }
}