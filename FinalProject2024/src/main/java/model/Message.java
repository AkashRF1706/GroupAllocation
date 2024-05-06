package model;

import java.sql.Timestamp;

public class Message {

	private String username;
	private String message;
	private String groupId;
	private java.sql.Timestamp createdAt;

	public Message(String username, String message, String groupId, Timestamp createdAt) {
		super();
		this.username = username;
		this.message = message;
		this.groupId = groupId;
		this.createdAt = createdAt;
	}

	public String getUsername() {
		return username;
	}

	public void setSender(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public java.sql.Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(java.sql.Timestamp createdAt) {
		this.createdAt = createdAt;
	}

}
