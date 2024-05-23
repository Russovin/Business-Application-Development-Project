package model;

public class User {
	private String userId;
	private String username;
	private String email;
	private String userPassword;
	private String userGender;
	private String userRole;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserGender() {
		return userGender;
	}
	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	public User(String userId, String username, String email, String userPassword, String userGender, String userRole) {
		super();
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.userPassword = userPassword;
		this.userGender = userGender;
		this.userRole = userRole;
	}
}
