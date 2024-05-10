package model;

import java.util.List;

public class Supervisor {

	private String id;
	private String name;
	private String department;
	private int numGroups;
	private List<String> preferences;

	public Supervisor(String id, String name, String department) {
		super();
		this.id = id;
		this.name = name;
		this.department = department;
	}

	public int getNumGroups() {
		return numGroups;
	}

	public void setNumGroups(int numGroups) {
		this.numGroups = numGroups;
	}

	public String getId() {
		return id;
	}

	public List<String> getPreferences() {
		return preferences;
	}

	public void setPreferences(List<String> preferences) {
		this.preferences = preferences;
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

}
