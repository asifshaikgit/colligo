package Repository;

import static Constants.AsianConstants.BANGLADESH_OTP_SERVICE_URL;
import static Constants.AsianConstants.BEHARIN_OTP_SERVICE_URL;
import static Constants.AsianConstants.COUNTRIES_MASTER;
import static Constants.AsianConstants.DEALER_MOBILE;
import static Constants.AsianConstants.DL_APP_USER_INFO;
import static Constants.AsianConstants.DL_DEALER_TABLE;
import static Constants.AsianConstants.EGYPRT_AR_OTP_SERVICE_URL;
import static Constants.AsianConstants.EGYPRT_EN_OTP_SERVICE_URL;
import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.INDONESIA_OTP_SERVICE_URL;
import static Constants.AsianConstants.LANKA_OTP_SERVICE_URL;
import static Constants.AsianConstants.LOGS_TABLE;
import static Constants.AsianConstants.NEPAL_OTP_SERVICE_URL;
import static Constants.AsianConstants.OMAN_OTP_SERVICE_URL;
import static Constants.AsianConstants.OTP_TABLE;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

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
import Services.otpservices;
import Utilres.Jsonresponse;
import Utilres.ValidationCtrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressWarnings("deprecation")
public class Otprepository implements otpservices {
	private Connection connection;
	private static final String POST_PARAMS = "username=skyhawksms&password=skyhawk@1";
	private HttpClient httpclient = new DefaultHttpClient();
	private ValidationCtrl validationlog;
	private Mailrepository mailling;
	@SuppressWarnings("static-access")
	
	public Otprepository() {
		// TODO Auto-generated constructor stub
		connection = new Database().Connection();
		httpclient = new DefaultHttpClient();
		validationlog = new ValidationCtrl();
		mailling = new Mailrepository();
	}
	
	private void dealerEmailSendOtp(String mobileNumber, String otpString, String amount, String username, String dealerid) {
		
		try {
			ResultSet dealerInformation = this.getdealerInformationdata(mobileNumber,dealerid);
			String dealerEmail = "";
			if(dealerInformation.next()) {
				dealerEmail = dealerInformation.getString("DEALER_EMAIL") != null ? dealerInformation.getString("DEALER_EMAIL").toString() : "";
			}
			String OtpMessageMobile = this.getAppendOtpMessage(username,amount,otpString, mobileNumber,dealerid);
	        mailling.sendMail(OtpMessageMobile, dealerEmail,"","OTP to validate the transaction and obtain receipt","");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public Object SendOtpMessage(String CountryName, String mobileNumber, String userId,String amount, String dealerid) {
		if(CountryName.equals("IOM1")) { return this.sendOtpMessageforOman(CountryName, mobileNumber, userId, amount,dealerid); }
		else if(CountryName.equals("INP1")) { return this.sendOtpMessageforNepal(CountryName, mobileNumber, userId,amount,dealerid); }
		else if(CountryName.equals("ILK2")) { return this.sendOtpMessageforLanka(CountryName, mobileNumber, userId,amount,dealerid); }
		else if(CountryName.equals("IBD1")) { return this.sendOtpMessageforBangladesh(CountryName, mobileNumber, userId, amount,dealerid); }
		else if(CountryName.equals("IBH1")) { return this.sendOtpMessageforBaharain(CountryName, mobileNumber, userId, amount,dealerid); }
		else if(CountryName.equals("EgyptEn")) { return this.sendOtpMessageforEgyptEn(CountryName, mobileNumber, userId, amount,dealerid); }
		else if(CountryName.equals("EgyptAr")) { return this.sendOtpMessageforEgyptAr(CountryName, mobileNumber, userId, amount,dealerid); }
		else if(CountryName.equals("IET1")) { return this.sendOtpMessageforIndonesia(CountryName, mobileNumber, userId, amount,dealerid); }
		else if(CountryName.equals("IID1")) { return this.sendOtpMessageforIndonesia(CountryName, mobileNumber, userId, amount,dealerid); }
		else if(CountryName.equals("IAE1")) { return this.sendOtpMessageforDubaie(CountryName, mobileNumber, userId, amount,dealerid); }
		else { return null; }

	}
		
	@SuppressWarnings("unused")
	private Object sendOtpMessageforDubaie(String countryName, String mobileNumber, String userId, String amount, String dealerid) {
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
				BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in .readLine()) != null) {
	                response.append(inputLine);
	            } in .close();
	            JsonObject data = new Gson().fromJson(response.toString(), JsonObject.class);
	           if(data.get("token").getAsString() != null) {
	        	   return this.getSendOtpDubaieotp(countryName, userId, amount, mobileNumber, dealerid, data.get("token").getAsString());
	           } else {
	               return null;
	           }
			} else {
				this.storeLogsDataInformation("", "DUBAIE GETTING ERROR FOR TAKING TOKEN","","","ERROR");
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
	private Object getSendOtpDubaieotp(String countryName, String userId, String amount, String mobileNumber, String dealerid, String token) {
		
		try {
			URL url = new URL("https://smartmessaging.etisalat.ae:5676/campaigns/submissions/sms/nb/");
			ResultSet userInforamtion = this.getExistingUserDataById(userId);
			userInforamtion.next();
	 	   JSONObject otpMessage = (JSONObject) this.generatetheOtp(countryName);
	 	   String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.get("x").toString(), mobileNumber,dealerid);
	 	   final String DUBAIE_MESSAGE = "desc="+URLEncoder.encode("This is the description for campaign", "UTF8")+"&campaignName="+URLEncoder.encode("test campaign","UTF8")+"&msgCategory=4.5&contentType=3.2&senderAddr=APBERGER&dndCategory=campaign&priority=1&clientTxnId=112346587965&recipient=971"+mobileNumber+"&msg="+URLEncoder.encode(OtpMessageMobile, "UTF8");
	 	   HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
	 	   httpURLConnection.setRequestMethod("POST");
	 	   httpURLConnection.setRequestProperty("Authorization", "Bearer "+token);
	 	   httpURLConnection.setDoOutput(true);
			OutputStream os1 = httpURLConnection.getOutputStream();
			os1.write(DUBAIE_MESSAGE.getBytes());
			os1.flush();
			os1.close();
			JSONObject httpResponsuccess = new JSONObject();
			Integer statusInsert = this.insertOtpMessage(countryName,otpMessage.get("x").toString(),userId,"",mobileNumber);
			this.dealerEmailSendOtp(mobileNumber, otpMessage.get("x").toString(), amount,userInforamtion.getString("USERNAME").toString(),dealerid);
			httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
			httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
			httpResponsuccess.put("msg", "OTP Sent Successfully");
			httpResponsuccess.put("otp", otpMessage.get("x").toString());
			httpResponsuccess.put("otptoken", statusInsert);
			httpResponsuccess.put("otplength", otpMessage.get("l").toString());
			return httpResponsuccess;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	private Object sendOtpMessageforIndonesia(String countryName, String mobileNumber, String userId, String amount, String dealerid) {
		// TODO Auto-generated method stub
		try {
			ResultSet userInforamtion = this.getExistingUserDataById(userId);
			userInforamtion.next();
			JSONObject otpMessage = (JSONObject) this.generatetheOtp(countryName);
			String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.get("x").toString(), mobileNumber,dealerid);
			HttpGet httpomanget = new HttpGet(INDONESIA_OTP_SERVICE_URL+"?userid=APIasianpaints&password=APIasianpaints5432&msisdn=62"+mobileNumber+"&message="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8")+"&sender=AsianPaints&division=Marketing&batchname=APNotification&uploadby=AsianPaints&channel=0");
			HttpResponse httpresponse = httpclient.execute(httpomanget);
			String entity = EntityUtils.toString(httpresponse.getEntity());
			Integer statusCode = httpresponse.getStatusLine().getStatusCode();
			JSONObject httpResponsuccess = new JSONObject();
			Integer statusInsert = this.insertOtpMessage(countryName,otpMessage.get("x").toString(),userId,entity.toString(),mobileNumber);
			this.dealerEmailSendOtp(mobileNumber, otpMessage.get("x").toString(), amount,userInforamtion.getString("USERNAME").toString(),dealerid);
			if (statusCode == 200) {
				httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
				httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
				httpResponsuccess.put("msg", "OTP Sent Successfully");
				httpResponsuccess.put("otp", otpMessage.get("x").toString());
				httpResponsuccess.put("otptoken", statusInsert);
				httpResponsuccess.put("otplength", otpMessage.get("l").toString());
			} else {
				httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
				httpResponsuccess.put("msg", "Something went wrong!, Please try again");
			}
			return httpResponsuccess;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
		}
	}

	private Object sendOtpMessageforEgyptAr(String countryName, String mobileNumber, String userId, String amount, String dealerid) {
		// TODO Auto-generated method stub
		try {
			ResultSet userInforamtion = this.getExistingUserDataById(userId);
			userInforamtion.next();
			JSONObject otpMessage = (JSONObject) this.generatetheOtp(countryName);
			String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.get("x").toString(), mobileNumber,dealerid);
			HttpGet httpomanget = new HttpGet(EGYPRT_AR_OTP_SERVICE_URL+"?username=scib&password=scP@WD&request_id="+otpMessage+"&Mobile_No="+mobileNumber+"&type=2&encoding=2&sender=Advansys&message="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8")+"&operator=Vodafone");
			HttpResponse httpresponse = httpclient.execute(httpomanget);
			String entity = EntityUtils.toString(httpresponse.getEntity());
			Integer statusCode = httpresponse.getStatusLine().getStatusCode();
			JSONObject httpResponsuccess = new JSONObject();
			Integer statusInsert = this.insertOtpMessage(countryName,otpMessage.get("x").toString(),userId,entity.toString(),mobileNumber);
			this.dealerEmailSendOtp(mobileNumber, otpMessage.get("x").toString(), amount,userInforamtion.getString("USERNAME").toString(),dealerid);
			if(statusCode.equals(200)) {
				if(entity.contains("OK")) {
					httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
					httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
					httpResponsuccess.put("msg", "OTP Sent Successfully");
					httpResponsuccess.put("otp", otpMessage.get("x").toString());
					httpResponsuccess.put("otptoken", statusInsert);
					httpResponsuccess.put("otplength", otpMessage.get("l").toString());
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

	private Object sendOtpMessageforEgyptEn(String countryName, String mobileNumber, String userId, String amount, String dealerid) {
		// TODO Auto-generated method stub
		try {
			ResultSet userInforamtion = this.getExistingUserDataById(userId);
			userInforamtion.next();
			JSONObject otpMessage = (JSONObject) this.generatetheOtp(countryName);
			String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.get("x").toString(), mobileNumber,dealerid);
			HttpGet httpomanget = new HttpGet(EGYPRT_EN_OTP_SERVICE_URL+"?username=scib&password=scP@WD&type=2&message="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8")+"&encoding=1&sender=Advansys&operator=Vodafone&Mobile_No="+mobileNumber);
			HttpResponse httpresponse = httpclient.execute(httpomanget);
			String entity = EntityUtils.toString(httpresponse.getEntity());
			Integer statusCode = httpresponse.getStatusLine().getStatusCode();
			JSONObject httpResponsuccess = new JSONObject();
			Integer statusInsert = this.insertOtpMessage(countryName,otpMessage.get("x").toString(),userId,entity.toString(),mobileNumber);
			this.dealerEmailSendOtp(mobileNumber, otpMessage.get("x").toString(), amount,userInforamtion.getString("USERNAME").toString(),dealerid);
			if(statusCode.equals(200)) {
				if(entity.contains("OK")) {
					httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
					httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
					httpResponsuccess.put("msg", "OTP Sent Successfully");
					httpResponsuccess.put("otp", otpMessage.get("x").toString());
					httpResponsuccess.put("otptoken", statusInsert);
					httpResponsuccess.put("otplength", otpMessage.get("l").toString());
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
	private Object sendOtpMessageforBangladesh(String countryName, String mobileNumber, String userId,String amount, String dealerid) {
		// TODO Auto-generated method stub
		try {
			JSONObject httpResponsuccess = new JSONObject();
			ResultSet userInforamtion = this.getExistingUserDataById(userId);
			userInforamtion.next();
			JSONObject mainJson = new JSONObject();
			JSONObject otpMessage = (JSONObject) this.generatetheOtp(countryName);
		 	String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.get("x").toString(), mobileNumber,dealerid);
		 	
		 	JSONArray smsdataJsonArray = new JSONArray();
			JSONObject authJson = new JSONObject();
			authJson.put("api_key", "cd268e0ff8ee5bec8cebf80a2e1e46de81");
			authJson.put("api_secret", "0de0dcd758e561f52da5cf71a68d3cfd81");
			authJson.put("username", "asianpaints");
			JSONObject smsdataJson = new JSONObject();
			smsdataJson.put("recipient", mobileNumber);
			smsdataJson.put("message", URLEncoder.encode(OtpMessageMobile.toString(), "UTF8"));
			smsdataJson.put("mask", "ASIANPAINTS");
			smsdataJsonArray.add(smsdataJson);
			mainJson.put("auth", authJson);
			mainJson.put("sms_data", smsdataJsonArray);
			
		 	OkHttpClient client = new OkHttpClient().newBuilder().build();
		 			RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainJson.toString());
		 			Request request = new Request.Builder()
		 			  .url(BANGLADESH_OTP_SERVICE_URL)
		 			  .method("POST", body)
		 			  .addHeader("Content-Type", "application/json")
		 			  .build();
		 			Response responseer = client.newCall(request).execute();
					if (responseer.code() == 200) {
					String jsonData = responseer.body().string();
			 		JsonObject dataResponse = new Gson().fromJson(jsonData.toString(), JsonObject.class);
					Integer statusInsert = this.insertOtpMessage(countryName,otpMessage.get("x").toString(),userId,dataResponse.toString(),mobileNumber);
					this.dealerEmailSendOtp(mobileNumber, otpMessage.get("x").toString(), amount,userInforamtion.getString("USERNAME").toString(),dealerid);
					httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
					httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
					httpResponsuccess.put("msg", "OTP Sent Successfully");
					httpResponsuccess.put("otp", otpMessage.get("x").toString());
					httpResponsuccess.put("otptoken", statusInsert);
					httpResponsuccess.put("otplength", otpMessage.get("l").toString());
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

	private Object sendOtpMessageforLanka(String countryName, String mobileNumber, String userId, String amount, String dealerid) {
		// TODO Auto-generated method stub
		try {
			ResultSet userInforamtion = this.getExistingUserDataById(userId);
			userInforamtion.next();
			JSONObject otpMessage = (JSONObject) this.generatetheOtp(countryName);
			String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.get("x").toString(), mobileNumber,dealerid);
			System.err.println(LANKA_OTP_SERVICE_URL+"?USER=AsianPntL&PWD=c7kObA12@z&MASK=APC&NUM="+mobileNumber+"&MSG="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8"));
			HttpGet httpomanget = new HttpGet(LANKA_OTP_SERVICE_URL+"?USER=AsianPntL&PWD=c7kObA12@z&MASK=APC&NUM="+mobileNumber+"&MSG="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8"));
			HttpResponse httpresponse = httpclient.execute(httpomanget);
			String entity = EntityUtils.toString(httpresponse.getEntity());
			Integer statusCode = httpresponse.getStatusLine().getStatusCode();
			JSONObject httpResponsuccess = new JSONObject();
			Integer statusInsert = this.insertOtpMessage(countryName,otpMessage.get("x").toString(),userId,entity.toString(),mobileNumber);
			this.dealerEmailSendOtp(mobileNumber, otpMessage.get("x").toString(), amount,userInforamtion.getString("USERNAME").toString(),dealerid);
			if(statusCode.equals(200)) {
				if(entity.contains("SUCCESS")) {
					httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
					httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
					httpResponsuccess.put("msg", "OTP Sent Successfully");
					httpResponsuccess.put("otp", otpMessage.get("x").toString());
					httpResponsuccess.put("otptoken", statusInsert);
					httpResponsuccess.put("otplength", otpMessage.get("l").toString());
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
	
	private Object sendOtpMessageforBaharain(String countryName, String mobileNumber, String userId, String amount, String dealerid) {
		// TODO Auto-generated method stub
		try {
			ResultSet userInforamtion = this.getExistingUserDataById(userId);
			userInforamtion.next();
			JSONObject otpMessage = (JSONObject) this.generatetheOtp(countryName);
			String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.get("x").toString(), mobileNumber,dealerid);
			HttpGet httpomanget = new HttpGet(BEHARIN_OTP_SERVICE_URL+"?User=Berger&passwd=klm@pxw4&mobilenumber=+973"+mobileNumber+"&message="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8")+"&sid=xxx&mtype=N&DR=Y");
			HttpResponse httpresponse = httpclient.execute(httpomanget);
			String entity = EntityUtils.toString(httpresponse.getEntity());
			Integer statusCode = httpresponse.getStatusLine().getStatusCode();
			JSONObject httpResponsuccess = new JSONObject();
			Integer statusInsert = this.insertOtpMessage(countryName,otpMessage.get("x").toString(),userId,entity.toString(),mobileNumber);
			this.dealerEmailSendOtp(mobileNumber, otpMessage.get("x").toString(), amount,userInforamtion.getString("USERNAME").toString(),dealerid);
			if(statusCode.equals(200)) {
				if(entity.contains("OK")) {
					httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
					httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
					httpResponsuccess.put("msg", "OTP Sent Successfully");
					httpResponsuccess.put("otp", otpMessage.get("x").toString());
					httpResponsuccess.put("otptoken", statusInsert);
					httpResponsuccess.put("otplength", otpMessage.get("l").toString());
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

	private Object sendOtpMessageforNepal(String countryName, String mobileNumber, String userId, String amount, String dealerid) {
		// TODO Auto-generated method stub
		try {
			ResultSet userInforamtion = this.getExistingUserDataById(userId);
			userInforamtion.next();
			JSONObject otpMessage = (JSONObject) this.generatetheOtp(countryName);
			String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.get("x").toString(), mobileNumber,dealerid);
			//?token=v2_NiSDSe0LCsO3AOiRlKsAhEAUDgC.Hr0Q&from=Demo&to=+977+9845149348&text=message to be sent
			HttpGet httpomanget = new HttpGet(NEPAL_OTP_SERVICE_URL+"?token=v2_NiSDSe0LCsO3AOiRlKsAhEAUDgC.Hr0Q&from=Demo&to=+977+"+mobileNumber+"&text="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8"));
			System.err.println(NEPAL_OTP_SERVICE_URL+"?token=v2_NiSDSe0LCsO3AOiRlKsAhEAUDgC.Hr0Q&from=Demo&to=+977+"+mobileNumber+"&text="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8").toString());
			HttpResponse httpresponse = httpclient.execute(httpomanget);
			String entity = EntityUtils.toString(httpresponse.getEntity());
//			this.storeLogsDataInformation("", entity.toString(),"NEPAL DATA OTP","","error");
			Integer statusCode = httpresponse.getStatusLine().getStatusCode();
			Integer statusInsert = this.insertOtpMessage(countryName,otpMessage.get("x").toString(),userId,entity.toString(),mobileNumber);
			this.dealerEmailSendOtp(mobileNumber, otpMessage.get("x").toString(), amount,userInforamtion.getString("USERNAME").toString(),dealerid);
			JSONObject httpResponsuccess = new JSONObject();
			JSONObject userloggedinfo = new JSONObject(entity);
			this.storeLogsDataInformation("", userloggedinfo.toString(),"NEPAL DATA OTP query","","info");
			if(statusCode.equals(200)) {
				String response_code = userloggedinfo.get("response_code").toString();
				if(response_code.equals("200")) {
					httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
					httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
					httpResponsuccess.put("msg", "OTP Sent Successfully");
					httpResponsuccess.put("otp", otpMessage.get("x").toString());
					httpResponsuccess.put("otptoken", statusInsert);
					httpResponsuccess.put("otplength", otpMessage.get("l").toString());
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
			this.storeLogsDataInformation("", e.getMessage().toString(),"NEPAL DATA OTP query","","error");
			e.printStackTrace();
			return null;
		}
	}

	private Object sendOtpMessageforOman(String countryName, String mobileNumber, String userId, String amount, String dealerid) {
		// TODO Auto-generated method stub
		try {
			ResultSet userInforamtion = this.getExistingUserDataById(userId);
			userInforamtion.next();
			JSONObject otpMessage = (JSONObject) this.generatetheOtp(countryName);
			String OtpMessageMobile = this.getAppendOtpMessage(userInforamtion.getString("USERNAME"),amount,otpMessage.get("x").toString(), mobileNumber,dealerid);
			String dateTime = Jsonresponse.getOtpStartDate(new Date());
			System.err.println(OMAN_OTP_SERVICE_URL+"?UserId=bergerwebs&Password=T!btws14&MobileNo="+mobileNumber+"&Message="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8")+"&PushDateTime="+URLEncoder.encode(dateTime,"UTF8")+"&Lang=0&Header=AsianPaints&referenceIds=10101");
			HttpGet httpomanget = new HttpGet(OMAN_OTP_SERVICE_URL+"?UserId=bergerwebs&Password=A@xaty90&MobileNo="+mobileNumber+"&Message="+URLEncoder.encode(OtpMessageMobile.toString(), "UTF8")+"&PushDateTime="+URLEncoder.encode(dateTime,"UTF8")+"&Lang=0&Header=AsianPaints&referenceIds=10101");
			HttpResponse httpresponse = httpclient.execute(httpomanget);
			String entity = EntityUtils.toString(httpresponse.getEntity());
			System.err.println(entity.toString());
			Integer statusCode = httpresponse.getStatusLine().getStatusCode();
			Integer statusInsert = this.insertOtpMessage(countryName,otpMessage.get("x").toString(),userId,entity.toString(),mobileNumber);
			this.dealerEmailSendOtp(mobileNumber, otpMessage.get("x").toString(), amount,userInforamtion.getString("USERNAME").toString(),dealerid);
			JSONObject httpResponsuccess = new JSONObject();
			if(statusCode.equals(200)) {
				if(entity.equals("1")) {
					httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
					httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
					httpResponsuccess.put("msg", "OTP Sent Successfully");
					httpResponsuccess.put("otp", otpMessage.get("x").toString());
					httpResponsuccess.put("otplength", otpMessage.get("l").toString());
					httpResponsuccess.put("otptoken", statusInsert);
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
			System.err.println(e.getMessage().toString());
			this.storeLogsDataInformation("", e.getMessage().toString(),"OMAN DATA OTP query","","error");
			e.printStackTrace();
			return null;
		}
	}
	
	private Object generatetheOtp(String countryName) {
		// TODO Auto-generated method stub
		String x = "";
		JSONObject otpObject = new JSONObject();
		try {
			ResultSet countryInfo = this.getByCountryCodeData(countryName);
			countryInfo.next();
			Integer getotpLength = countryInfo.getInt("COUNTRY_OTP_LENGTH");
			String numbers = "0123456789";
			Random randomOtp = new Random();		
			char[] otp = new char[getotpLength]; 
		    for (int i = 0; i < getotpLength; i++) { 
		        otp[i]=numbers.charAt(randomOtp.nextInt(numbers.length())); 
		        x=x+otp[i];
		    }
		    otpObject.put("x", x);
		    otpObject.put("l", getotpLength);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return otpObject;
	}
	
	public Integer insertOtpMessage(String countryName, String otpMessage, String userId, String otpResponse, String mobileNumber) {
		// TODO Auto-generated method stub
				Timestamp createdAt = new Timestamp(System.currentTimeMillis());
				// TODO Auto-generated method stub
				try {
					Boolean resendOtpfalseStatus = false;Boolean resendOtptrueStatus = true;
					ResultSet checkOtpMessageinfo = this.getOtpMessageid(countryName,otpMessage,userId,false,mobileNumber);
					if(checkOtpMessageinfo.next()) {
						String updateQry = "UPDATE " + SCHEMA_TABLE + "." + OTP_TABLE + " O "
								+ "SET"
								+ " O.IS_RESEND_OTP = "+resendOtptrueStatus+" WHERE "
								+ "O.USERID = '"+userId+"' "
										+ "AND"
										+ " O.COUNTRY_CODE = '"+countryName+"' "
												+ "AND"
												+ " O.IS_RESEND_OTP = "+resendOtpfalseStatus+""
												+ " AND"
												+ " O.DEALER_MOBILE = '" + mobileNumber + "' ";
						System.out.println(updateQry);
						PreparedStatement updateQryPrepare = connection.prepareStatement(updateQry);
						updateQryPrepare.execute();
						updateQryPrepare.close();
					}
					String insertQry = "INSERT INTO " + SCHEMA_TABLE + "." + OTP_TABLE + " ("
							+ "USERID,"
							+ "OTP,"
							+ "COUNTRY_CODE,"
							+ "IS_VERIFY_OTP,"
							+ "IS_RESEND_OTP,"
							+ "CREATED_AT,"
							+ "OTP_RESPONSE,"
							+ "DEALER_MOBILE) values (?,?,?,?,?,?,?,?) ";
					PreparedStatement pstOtpMsg = connection.prepareStatement(insertQry);
					pstOtpMsg.setString(1, userId.toString());
					pstOtpMsg.setString(2, otpMessage.toString());
					pstOtpMsg.setString(3, countryName.toString());
					pstOtpMsg.setBoolean(4, false);
					pstOtpMsg.setBoolean(5, false);
					pstOtpMsg.setString(6, createdAt.toString());
					pstOtpMsg.setString(7, otpResponse.toString());
					pstOtpMsg.setString(8, mobileNumber.toString());
					pstOtpMsg.executeUpdate();
					pstOtpMsg.close();
					ResultSet getOtpMessageinfo = this.getOtpMessageid(countryName, otpMessage, userId, true, mobileNumber);
					getOtpMessageinfo.next();
					return getOtpMessageinfo.getInt("ID");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
		}
	
	public ResultSet getOtpMessageid(String countryName, String otpMessage, String userId, Boolean resendTag, String mobileNumber) {
		Statement stmt;
		try {
			
			Boolean isfalsecondition = false;
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "O.*"
					+ " FROM " + SCHEMA_TABLE + "." + OTP_TABLE + " O "
							+ "WHERE O.USERID = '"+userId+"' "
									+ "AND O.COUNTRY_CODE = '"+countryName+"' "
											+ "AND O.IS_RESEND_OTP = "+isfalsecondition+" "
											+ "AND O.DEALER_MOBILE = '" + mobileNumber + "' ";
			if(resendTag.equals(true)) {
				querySelect += " AND O.OTP = '"+otpMessage+"' ";
			}
			querySelect += " ORDER BY ID DESC";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
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
					+ "L.* FROM " + SCHEMA_TABLE + " ." + DL_APP_USER_INFO + " L WHERE L.USERID = '"+userId+"' ORDER BY L.ID DESC";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private ResultSet getByCountryCodeData(String countryId) {
		// TODO Auto-generated method stub
		Statement stmt;
		try {
			String[] countryLength = countryId.split(",");
			stmt = connection.createStatement();
			String querySelect = "SELECT L.* FROM " + SCHEMA_TABLE + " ." + COUNTRIES_MASTER + " L WHERE ";
			for (int i=0;i<countryLength.length;i++) {
				querySelect += " L.COMPANY_CODE = '" + countryLength[i] + "' ";
				if(i < (countryLength.length)-1 ) {
					querySelect += " OR ";
				}
			}
			ResultSet resultSet = stmt.executeQuery(querySelect);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private void storeLogsDataInformation(String excutionTime, String messageType, String category, String userId, String errorType) {
        try {
        	Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            String insertCommand = "INSERT INTO " + SCHEMA_TABLE + "." + LOGS_TABLE + " "
                    + "("
                    + "MESSAGE,"
                    + "EXEXUTION_TIME,"
                    + "CREATED_AT,"
                    + "CATEGORY,"
                    + "USERID,"
                    + "LOG_TYPE"
                    + ") VALUES (?,?,?,?,?,?) ";
            PreparedStatement pstUserlogin = connection.prepareStatement(insertCommand);
            pstUserlogin.setString(1, messageType.toString());
            pstUserlogin.setString(2, excutionTime.toString());
            pstUserlogin.setString(3, createdAt.toString());
            pstUserlogin.setString(4, category.toString());
            pstUserlogin.setString(5, userId.toString());
            pstUserlogin.setString(6, errorType.toString());
            pstUserlogin.executeUpdate();
            pstUserlogin.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
	
	private ResultSet getdealerInformationdata(String dealerMobile, String dealerid) {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT D.DEALER_NAME,C.CURRENCY,D.DEALER_EMAIL FROM " + SCHEMA_TABLE + " ." + DEALER_MOBILE + " M "
					+ "INNER JOIN "+SCHEMA_TABLE+"."+DL_DEALER_TABLE+" D ON D.DEALER_ID = M.DEALER_ID "
							+ "INNER JOIN "+SCHEMA_TABLE+"."+COUNTRIES_MASTER+" C ON C.COMPANY_CODE = D.DEALER_COMPANY_CODE "
							+ "WHERE "
					+ "M.DEALER_MOBILE = '"+dealerMobile+"' AND M.DEALER_ID = '"+dealerid+"' ";
			this.storeLogsDataInformation("", querySelect.toString(),"dealer query","","info");
			ResultSet resultSet = stmt.executeQuery(querySelect);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private String getAppendOtpMessage(String username, String amount,String otpMessage, String mobileNumber, String dealerid) {
		ResultSet dealerInformation = this.getdealerInformationdata(mobileNumber,dealerid);
		String dealerName = "";
		String currency = "";
		try {
			if(dealerInformation.next()) {
				dealerName = dealerInformation.getString("DEALER_NAME").toString();
				currency = dealerInformation.getString("CURRENCY").toString();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String otpMessageRespose = "Dear "+dealerName+", Please share OTP "+otpMessage+" with "+username+" to obtain receipt towards payment of "+currency+" "+amount+".";
		return otpMessageRespose;
		
	}

}
