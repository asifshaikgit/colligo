package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.VerifyingRepository;
/**
 * Servlet implementation class Verifyotp
 */
@WebServlet("/Verifyotp")
public class Verifyotp extends HttpServlet {
	private static final long serialVersionUID = 1L;
      private VerifyingRepository verifyServ = new VerifyingRepository();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Verifyotp() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
		JSONObject verifyOtpObject = new JSONObject();
		try {
			verifyOtpObject.put("otp",  data.get("otp").getAsString());
			verifyOtpObject.put("otptoken",  data.get("otptoken").getAsString());
			verifyOtpObject.put("mobileNumber",  data.get("mobileNumber").getAsString());
			verifyOtpObject.put("userId",  data.get("userId").getAsString());
			Object OtpResponse = verifyServ.getCheckOtpMessage(verifyOtpObject);
			response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(OtpResponse.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
