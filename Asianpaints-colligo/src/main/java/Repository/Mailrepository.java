package Repository;

import static Constants.AsianConstants.TRIGGERMAIL;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Mailrepository {
	public Mailrepository() {
		// TODO Auto-generated constructor stub
	}
    
    public Boolean sendMail(String mailMessage, String emailId, String pdffullpath, String subject, String fileName) {
    	if(emailId.isEmpty()) { return false; }
		try {
					OkHttpClient client = new OkHttpClient().newBuilder().build();
					RequestBody body;
					if(pdffullpath.toString().isEmpty()) {
						body = new MultipartBody.Builder().setType(MultipartBody.FORM)
								  .addFormDataPart("reqTo",emailId)
								  .addFormDataPart("reqSubject",subject)
								  .addFormDataPart("reqBody",mailMessage)
								  .addFormDataPart("reqFrom","apwasprd@apps.asianpaints.com")
								  .build();
					} else {

						body = new MultipartBody.Builder().setType(MultipartBody.FORM)
								  .addFormDataPart("reqTo",emailId)
								  .addFormDataPart("reqSubject",subject)
								  .addFormDataPart("reqBody",mailMessage)
								  .addFormDataPart("reqFrom","apwasprd@apps.asianpaints.com")
								  .addFormDataPart("reqAttachment1",fileName,
										  RequestBody.create(MediaType.parse("application/octet-stream"),new File(pdffullpath)))
								  .build();
					}
					Request request = new Request.Builder()
					  .url(TRIGGERMAIL)
					  .method("POST", body)
					  .addHeader("Content-Type", "multipart/form-data")
					  .build();
					Response response = client.newCall(request).execute();
					if(response.code() == 200) {
						return true;
					} else {
						return false;
					}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
    }

}
