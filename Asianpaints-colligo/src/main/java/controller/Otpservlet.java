package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.Otprepository;
import Utilres.Jsonresponse;
/**
 * Servlet implementation class Otpservlet
 */
@WebServlet("/Otpservlet")
public class Otpservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Otprepository otpservices = new Otprepository();
	@SuppressWarnings("unused")
	private Jsonresponse Josnutilres = new Jsonresponse();
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
		String countrycode = data.get("countrycode").getAsString();
		String mobileNumber = data.get("mobileNumber").getAsString();
		String userId = data.get("userId").getAsString();
		String amount = data.get("amount").getAsString();
		String dealerid = data.get("dealerid").getAsString();
		Object OtpResponse = otpservices.SendOtpMessage(countrycode,mobileNumber,userId,amount,dealerid);
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(OtpResponse.toString());
	}

}
