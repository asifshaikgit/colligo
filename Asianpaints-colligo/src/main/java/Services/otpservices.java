package Services;

public interface otpservices {
	
	Object SendOtpMessage(String CountryName, String mobileNumber, String userId,String amount, String dealerid);
	Integer insertOtpMessage(String countryName, String otpMessage, String userId, String otpResponse, String mobileNumber);

}
