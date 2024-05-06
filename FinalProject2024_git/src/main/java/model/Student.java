package model;

import java.util.List;

public class Student {

	private String id;
	private String name;
	private String department;
	private List<String> preferences;

	public Student(String id, String name, String department) {
		super();
		this.id = id;
		this.name = name;
		this.department = department;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public List<String> getPreferences() {
		return preferences;
	}

	public void setPreferences(List<String> preferences) {
		this.preferences = preferences;
	}

}