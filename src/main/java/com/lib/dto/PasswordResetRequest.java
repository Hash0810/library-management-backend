package com.lib.dto;

public class PasswordResetRequest {
	private String email;       // Needed to identify the user
    private String newPassword; // The new password to be set
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
