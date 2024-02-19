package Repository;

import java.util.Random;

public class Otprepo {
	
	protected String generatethemailOtp(String Otplength) {
		// TODO Auto-generated method stub
		String x = "";
		try {
			int getotpLength = Integer.parseInt(Otplength);
			String numbers = "0123456789";
			Random randomOtp = new Random();		
			char[] otp = new char[getotpLength]; 
		    for (int i = 0; i < getotpLength; i++) { 
		        otp[i]=numbers.charAt(randomOtp.nextInt(numbers.length())); 
		        x=x+otp[i];
		    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return x;
	}

}
