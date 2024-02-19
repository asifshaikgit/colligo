package Repository;

import static Constants.AsianConstants.BANGLADESH_OTP_SERVICE_URL;
import static Constants.AsianConstants.BEHARIN_OTP_SERVICE_URL;
import static Constants.AsianConstants.DL_APP_USER_INFO;
import static Constants.AsianConstants.EGYPRT_AR_OTP_SERVICE_URL;
import static Constants.AsianConstants.EGYPRT_EN_OTP_SERVICE_URL;
import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.INDONESIA_OTP_SERVICE_URL;
import static Constants.AsianConstants.LANKA_OTP_SERVICE_URL;
import static Constants.AsianConstants.NEPAL_OTP_SERVICE_URL;
import static Constants.AsianConstants.OMAN_OTP_SERVICE_URL;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.simple.JSONArray;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Databaseconnection.Database;
import Utilres.Jsonresponse;

@SuppressWarnings("deprecation")
public class Smstextrepository {
private static final String POST_PARAMS = "username=skyhawksms&password=skyhawk@1";
    
    private Connection connection;
    private Mailrepository mailling;
    
	@SuppressWarnings("static-access")
	public Smstextrepository() {
		// TODO Auto-generated constructor stub
		connection = new Database().Connection();
		mailling = new Mailrepository();
	}
    
    private HttpClient httpclient = new DefaultHttpClient();
    
    public Object SendOtpMessage(String CountryName, String mobileNumber, String userId,String amount, String receiptNumber, String dealerEmail) {
        
        if(CountryName.equals("IOM1")) { return this.sendOtpMessageforOman(CountryName, mobileNumber, userId, amount, receiptNumber,dealerEmail); }
        else if(CountryName.equals("INP1")) { return this.sendOtpMessageforNepal(CountryName, mobileNumber, userId,amount, receiptNumber,dealerEmail); }
        else if(CountryName.equals("ILK2")) { return this.sendOtpMessageforLanka(CountryName, mobileNumber, userId,amount, receiptNumber,dealerEmail); }
        else if(CountryName.equals("IIN1")) { return this.sendOtpMessageforLanka(CountryName, mobileNumber, userId,amount, receiptNumber,dealerEmail); }
        else if(CountryName.equals("IIN2")) { return this.sendOtpMessageforLanka(CountryName, mobileNumber, userId,amount, receiptNumber,dealerEmail); }
        else if(CountryName.equals("IBD1")) { return this.sendOtpMessageforBangladesh(CountryName, mobileNumber, userId, amount, receiptNumber,dealerEmail); }
        else if(CountryName.equals("IBD1")) { return this.sendOtpMessageforBaharain(CountryName, mobileNumber, userId, amount, receiptNumber,dealerEmail); }
        else if(CountryName.equals("EgyptEn")) { return this.sendOtpMessageforEgyptEn(CountryName, mobileNumber, userId, amount, receiptNumber,dealerEmail); }
        else if(CountryName.equals("EgyptAr")) { return this.sendOtpMessageforEgyptAr(CountryName, mobileNumber, userId, amount, receiptNumber,dealerEmail); }
        else if(CountryName.equals("IET1")) { return this.sendOtpMessageforIndonesia(CountryName, mobileNumber, userId, amount, receiptNumber,dealerEmail); }
        else if(CountryName.equals("IID1")) { return this.sendOtpMessageforIndonesia(CountryName, mobileNumber, userId, amount, receiptNumber,dealerEmail); }
        else if(CountryName.equals("IAE1")) { return this.sendOtpMessageforDubaie(CountryName, mobileNumber, userId, amount, receiptNumber,dealerEmail); }
        else { return null; }

    }
    
    @SuppressWarnings("unused")
    private Object sendOtpMessageforDubaie(String countryName, String mobileNumber, String userId, String amount, String receiptNumber, String dealerEmail) {
        // TODO Auto-generated method stub
        
        StringBuilder postData = new StringBuilder();
        URL url;
        
        try {
            JSONObject httpResponsuccess = new JSONObject();
            url = new URL("https://smartmessaging.etisalat.ae:5676/login/user");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("POST Response Code :: " + responseCode);
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in .readLine()) != null) {
                    response.append(inputLine);
                } in .close();
                JsonObject data = new Gson().fromJson(response.toString(), JsonObject.class);
               if(data.get("token").getAsString() != null) {
                   return this.getSendOtpDubaieotp(countryName, userId, amount, mobileNumber, receiptNumber, dealerEmail);
               } else {
                   return null;
               }
            } else {
                httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                httpResponsuccess.put("msg", "Something went wrong!, Please try again");
                return httpResponsuccess;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    @SuppressWarnings("static-access")
    private Object getSendOtpDubaieotp(String countryName, String userId, String amount, String mobileNumber, String receiptNumber, String dealerEmail) {
        
        try {
            URL url = new URL("https://smartmessaging.etisalat.ae:5676/campaigns/submissions/sms/nb/");
            ResultSet userInforamtion = this.getExistingUserDataById(userId);
            userInforamtion.next();
            String otpMessage = "Hi we have received the amount: "+amount+" and please find the receipt Numner : "+receiptNumber+". we'll share the receipt throw the mail" ;
           String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.toString());
           mailling.sendMail(OtpMessageMobile, dealerEmail,"","Success Message","");
           final String DUBAIE_MESSAGE = "desc="+URLEncoder.encode("This is the description for campaign", "UTF8")+"&campaignName="+URLEncoder.encode("test campaign","UTF8")+"&msgCategory=4.5&contentType=3.2&senderAddr=APBERGER&dndCategory=campaign&priority=1&clientTxnId=112346587965&recipient=971504282531&msg="+URLEncoder.encode(OtpMessageMobile, "UTF8");
           HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
           httpURLConnection.setRequestMethod("POST");
           httpURLConnection.setDoOutput(true);
            OutputStream os1 = httpURLConnection.getOutputStream();
            os1.write(DUBAIE_MESSAGE.getBytes());
            os1.flush();
            os1.close();
            JSONObject httpResponsuccess = new JSONObject();
            int responseCode1 = httpURLConnection.getResponseCode();
            if (responseCode1 == httpURLConnection.HTTP_OK) {
                httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
                httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
                httpResponsuccess.put("msg", "Sent Successfully");
            } else {
                httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                httpResponsuccess.put("msg", "Something went wrong!, Please try again");
            }
            return httpResponsuccess;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unused")
	private Object sendOtpMessageforIndonesia(String countryName, String mobileNumber, String userId, String amount, String receiptNumber, String dealerEmail) {
        // TODO Auto-generated method stub
        try {
            ResultSet userInforamtion = this.getExistingUserDataById(userId);
            userInforamtion.next();
            String otpMessage = "Hi we have received the amount: "+amount+" and please find the receipt Numner : "+receiptNumber+". we'll share the receipt throw the mail" ;
            String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.toString());
            mailling.sendMail(OtpMessageMobile, dealerEmail,"","Success Message","");
            HttpGet httpomanget = new HttpGet(INDONESIA_OTP_SERVICE_URL+"?userid=APIasianpaints&password=APIasianpaints5432&msisdn="+mobileNumber+"&message="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8")+"&sender=AsianPaints&division=Marketing&batchname=APNotification&uploadby=AsianPaints&channel=0");
            HttpResponse httpresponse = httpclient.execute(httpomanget);
            String entity = EntityUtils.toString(httpresponse.getEntity());
            Integer statusCode = httpresponse.getStatusLine().getStatusCode();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private Object sendOtpMessageforEgyptAr(String countryName, String mobileNumber, String userId, String amount, String receiptNumber, String dealerEmail) {
        // TODO Auto-generated method stub
        try {
            ResultSet userInforamtion = this.getExistingUserDataById(userId);
            userInforamtion.next();
            String otpMessage = "Hi we have received the amount: "+amount+" and please find the receipt Numner : "+receiptNumber+". we'll share the receipt throw the mail" ;
            String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.toString());
            mailling.sendMail(OtpMessageMobile, dealerEmail,"","Success Message","");
            HttpGet httpomanget = new HttpGet(EGYPRT_AR_OTP_SERVICE_URL+"?username=scib&password=scP@WD&request_id="+otpMessage+"&Mobile_No="+mobileNumber+"&type=2&encoding=2&sender=Advansys&message="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8")+"&operator=Vodafone");
            HttpResponse httpresponse = httpclient.execute(httpomanget);
            String entity = EntityUtils.toString(httpresponse.getEntity());
            Integer statusCode = httpresponse.getStatusLine().getStatusCode();
            JSONObject httpResponsuccess = new JSONObject();
            if(statusCode.equals(200)) {
                if(entity.contains("OK")) {
                    httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
                    httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
                    httpResponsuccess.put("msg", "Sent Successfully");
                } else {
                    httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                    httpResponsuccess.put("msg", "Something went wrong!, Please try again");
                }
            } else {
                httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                httpResponsuccess.put("msg", "Something went wrong!, Please try again");
            }
            return httpResponsuccess;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private Object sendOtpMessageforEgyptEn(String countryName, String mobileNumber, String userId, String amount, String receiptNumber, String dealerEmail) {
        // TODO Auto-generated method stub
        try {
            ResultSet userInforamtion = this.getExistingUserDataById(userId);
            userInforamtion.next();
            String otpMessage = "Hi we have received the amount: "+amount+" and please find the receipt Numner : "+receiptNumber+". we'll share the receipt throw the mail" ;
            String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.toString());
            mailling.sendMail(OtpMessageMobile, dealerEmail,"","Success Message","");
            HttpGet httpomanget = new HttpGet(EGYPRT_EN_OTP_SERVICE_URL+"?username=scib&password=scP@WD&type=2&message="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8")+"&encoding=1&sender=Advansys&operator=Vodafone&Mobile_No="+mobileNumber);
            HttpResponse httpresponse = httpclient.execute(httpomanget);
            String entity = EntityUtils.toString(httpresponse.getEntity());
            Integer statusCode = httpresponse.getStatusLine().getStatusCode();
            JSONObject httpResponsuccess = new JSONObject();
            if(statusCode.equals(200)) {
                if(entity.contains("OK")) {
                    httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
                    httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
                    httpResponsuccess.put("msg", "Sent Successfully");
                } else {
                    httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                    httpResponsuccess.put("msg", "Something went wrong!, Please try again");
                }
            } else {
                httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                httpResponsuccess.put("msg", "Something went wrong!, Please try again");
            }
            return httpResponsuccess;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    @SuppressWarnings({ "static-access", "unchecked" })
    private Object sendOtpMessageforBangladesh(String countryName, String mobileNumber, String userId,String amount, String receiptNumber, String dealerEmail) {
        // TODO Auto-generated method stub
        try {
            ResultSet userInforamtion = this.getExistingUserDataById(userId);
            userInforamtion.next();
            JSONObject mainJson = new JSONObject();
            String otpMessage = "Hi we have received the amount: "+amount+" and please find the receipt Numner : "+receiptNumber+". we'll share the receipt throw the mail" ;
            String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.toString());
            mailling.sendMail(OtpMessageMobile, dealerEmail,"","Success Message","");
            JSONObject authJson = new JSONObject();
            authJson.put("api_key", "cd268e0ff8ee5bec8cebf80a2e1e46de81");
            authJson.put("api_secret", "0de0dcd758e561f52da5cf71a68d3cfd81");
            authJson.put("username", "asianpaints");
            
            JSONArray smsdataJsonArray = new JSONArray();
            JSONObject smsdataJson = new JSONObject();
            smsdataJson.put("recipient", mobileNumber);
            smsdataJson.put("message", URLEncoder.encode(OtpMessageMobile.toString(), "UTF8"));
            smsdataJson.put("mask", "ASIANPAINTS");
            smsdataJsonArray.add(smsdataJson);
            mainJson.put("auth", authJson);
            mainJson.put("sms_data", smsdataJsonArray);
            System.err.println(mainJson.toString());
            URL url = new URL(BANGLADESH_OTP_SERVICE_URL);
           HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
           httpURLConnection.setRequestMethod("POST");
           httpURLConnection.setDoOutput(true);
           httpURLConnection.setRequestProperty("Accept", "application/json");
            OutputStream outputStream  = httpURLConnection.getOutputStream();
            BufferedWriter os1 = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            os1.write(mainJson.toString());
            os1.flush();
            os1.close();
            outputStream.close();
            int responseCode1 = httpURLConnection.getResponseCode();
            JSONObject httpResponsuccess = new JSONObject();
            if (responseCode1 == httpURLConnection.HTTP_OK) {
                httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
                httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
                httpResponsuccess.put("msg", "Sent Successfully");
            } else {
                httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                httpResponsuccess.put("msg", "Something went wrong!, Please try again");
            }
            return httpResponsuccess;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }
    
    private Object sendOtpMessageforBaharain(String countryName, String mobileNumber, String userId, String amount, String receiptNumber, String dealerEmail) {
        // TODO Auto-generated method stub
        try {
            ResultSet userInforamtion = this.getExistingUserDataById(userId);
            userInforamtion.next();
            String otpMessage = "Hi we have received the amount: "+amount+" and please find the receipt Numner : "+receiptNumber+". we'll share the receipt throw the mail" ;
            String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.toString());
            mailling.sendMail(OtpMessageMobile, dealerEmail,"","Success Message","");
            HttpGet httpomanget = new HttpGet(BEHARIN_OTP_SERVICE_URL+"?User=Berger&passwd=klm@pxw4&mobilenumber=+973"+mobileNumber+"&message="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8")+"&sid=xxx&mtype=N&DR=Y");
            //HttpGet httpomanget = new HttpGet(LANKA_OTP_SERVICE_URL+"?USER=AsianPntL&PWD=c7kObA12@z&MASK=APC&NUM="+mobileNumber+"&MSG="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8"));
            HttpResponse httpresponse = httpclient.execute(httpomanget);
            String entity = EntityUtils.toString(httpresponse.getEntity());
            Integer statusCode = httpresponse.getStatusLine().getStatusCode();
            JSONObject httpResponsuccess = new JSONObject();
            if(statusCode.equals(200)) {
                if(entity.contains("SUCCESS")) {
                    httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
                    httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
                    httpResponsuccess.put("msg", "Sent Successfully");
                } else {
                    httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                    httpResponsuccess.put("msg", "Something went wrong!, Please try again");
                }
            } else {
                httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                httpResponsuccess.put("msg", "Something went wrong!, Please try again");
            }
            return httpResponsuccess;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private Object sendOtpMessageforLanka(String countryName, String mobileNumber, String userId, String amount, String receiptNumber, String dealerEmail) {
        // TODO Auto-generated method stub
        try {
            ResultSet userInforamtion = this.getExistingUserDataById(userId);
            userInforamtion.next();
            String otpMessage = "Hi we have received the amount: "+amount+" and please find the receipt Numner : "+receiptNumber+". we'll share the receipt throw the mail" ;
            String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.toString());
            mailling.sendMail(OtpMessageMobile, dealerEmail,"","Success Message","");
            HttpGet httpomanget = new HttpGet(LANKA_OTP_SERVICE_URL+"?USER=AsianPntL&PWD=c7kObA12@z&MASK=APC&NUM="+mobileNumber+"&MSG="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8"));
            HttpResponse httpresponse = httpclient.execute(httpomanget);
            String entity = EntityUtils.toString(httpresponse.getEntity());
            Integer statusCode = httpresponse.getStatusLine().getStatusCode();
            JSONObject httpResponsuccess = new JSONObject();
            if(statusCode.equals(200)) {
                if(entity.contains("SUCCESS")) {
                    httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
                    httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
                    httpResponsuccess.put("msg", "Sent Successfully");
                } else {
                    httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                    httpResponsuccess.put("msg", "Something went wrong!, Please try again");
                }
            } else {
                httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                httpResponsuccess.put("msg", "Something went wrong!, Please try again");
            }
            return httpResponsuccess;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private Object sendOtpMessageforNepal(String countryName, String mobileNumber, String userId, String amount, String receiptNumber, String dealerEmail) {
        // TODO Auto-generated method stub
        try {
            ResultSet userInforamtion = this.getExistingUserDataById(userId);
            userInforamtion.next();
            String otpMessage = "Hi we have received the amount: "+amount+" and please find the receipt Numner : "+receiptNumber+". we'll share the receipt throw the mail" ;
            String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.toString());
            mailling.sendMail(OtpMessageMobile, dealerEmail,"","Success Message","");
            HttpGet httpomanget = new HttpGet(NEPAL_OTP_SERVICE_URL+"?token=v2_NiSDSe0LCsO3AOiRlKsAhEAUDgC.Hr0Q&from=Demo&to=+977+"+mobileNumber+"&text="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8"));
            HttpResponse httpresponse = httpclient.execute(httpomanget);
            String entity = EntityUtils.toString(httpresponse.getEntity());
            Integer statusCode = httpresponse.getStatusLine().getStatusCode();
            JSONObject httpResponsuccess = new JSONObject();
            if(statusCode.equals(200)) {
                JSONObject userloggedinfo = new JSONObject(entity);
                String response_code = userloggedinfo.get("response_code").toString();
                System.err.println(response_code);
                if(response_code.equals("200")) {
                    httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
                    httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
                    httpResponsuccess.put("msg", "Sent Successfully");
                } else {
                    httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                    httpResponsuccess.put("msg", "Something went wrong!, Please try again");
                }
            } else {
                httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                httpResponsuccess.put("msg", "Something went wrong!, Please try again");
            }
            System.err.println(httpResponsuccess.toString());
            return httpResponsuccess;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    private Object sendOtpMessageforOman(String countryName, String mobileNumber, String userId, String amount, String receiptNumber, String dealerEmail) {
        // TODO Auto-generated method stub
        try {
            ResultSet userInforamtion = this.getExistingUserDataById(userId);
            userInforamtion.next();
            String otpMessage = "Hi we have received the amount: "+amount+" and please find the receipt Numner : "+receiptNumber+". we'll share the receipt throw the mail" ;
            String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.toString());
            mailling.sendMail(OtpMessageMobile, dealerEmail,"","Success Message","");
            String dateTime = Jsonresponse.getOtpStartDate(new Date());
            HttpGet httpomanget = new HttpGet(OMAN_OTP_SERVICE_URL+"?UserId=bergerwebs&Password=A@xaty90&MobileNo="+mobileNumber+"&Message="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8")+"&PushDateTime="+URLEncoder.encode(dateTime,"UTF8")+"&Lang=0&Header=AsianPaints&referenceIds=10101");
            HttpResponse httpresponse = httpclient.execute(httpomanget);
            String entity = EntityUtils.toString(httpresponse.getEntity());
            Integer statusCode = httpresponse.getStatusLine().getStatusCode();
            JSONObject httpResponsuccess = new JSONObject();
            if(statusCode.equals(200)) {
                if(entity.equals("1")) {
                    httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
                    httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
                    httpResponsuccess.put("msg", "Sent Successfully");
                } else {
                    httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                    httpResponsuccess.put("msg", "Something went wrong!, Please try again");
                }
            } else {
                httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
                httpResponsuccess.put("msg", "Something went wrong!, Please try again");
            }
            return httpResponsuccess;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    private ResultSet getExistingUserDataById(String userId) {
		// TODO Auto-generated method stub
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "L.* FROM " + SCHEMA_TABLE + " ." + DL_APP_USER_INFO + " L WHERE L.USERID = '"+userId+"' ORDER BY ID DESC";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
    
    private String getAppendOtpMessage(String username, String amount,String otpMessage) {
	    
		return username+" is collecting Amt. "+amount+". Please share the OTP to validate the trasaction and get the receipt:"+otpMessage;
		
	}
}
