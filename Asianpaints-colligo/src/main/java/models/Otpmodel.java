package models;

public class Otpmodel {
	
	private Integer Id;
	private String UserId;
	private String Country;
	private String Otp;
	private Integer IsResendOtp;
	private Integer IsVerifyOtp;
	private String CreatedAt;
	
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getCountry() {
		return Country;
	}
	public void setCountry(String country) {
		Country = country;
	}
	public String getOtp() {
		return Otp;
	}
	public void setOtp(String otp) {
		Otp = otp;
	}
	public Integer getIsResendOtp() {
		return IsResendOtp;
	}
	public void setIsResendOtp(Integer isResendOtp) {
		IsResendOtp = isResendOtp;
	}
	public Integer getIsVerifyOtp() {
		return IsVerifyOtp;
	}
	public void setIsVerifyOtp(Integer isVerifyOtp) {
		IsVerifyOtp = isVerifyOtp;
	}
	public String getCreatedAt() {
		return CreatedAt;
	}
	public void setCreatedAt(String createdAt) {
		CreatedAt = createdAt;
	}

}
