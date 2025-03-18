package com.lib.dto;

public class OTPRequest {
	private String email;
	private String OTP;
	public void setEmail(String email) {
		this.email=email;
	}
	public String getEmail() {
		return email;
	}
	public void setOTP(String OTP) {
		this.OTP=OTP;
	}
	public String getOTP() {
		return OTP;
	}
}
