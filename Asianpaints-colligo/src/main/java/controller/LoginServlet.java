package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.LoginRepository;
import Utilres.Jsonresponse;
import Utilres.ValidationCtrl;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    
	private static final long serialVersionUID = 1L;
	LoginRepository loginServ = new LoginRepository();
    Jsonresponse Josnutilres = new Jsonresponse();
    private ValidationCtrl validate = new ValidationCtrl();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
		Object validatecheck = validate.validateloginApidata(data);
		if(validatecheck.equals(true)) {
		    String username = data.get("username").getAsString();
	        String password = data.get("password").getAsString();
	        String devicetoken = data.get("devicetoken").getAsString();
	        String userlevel = "1";
	        Object LoginString = loginServ.loginCheckrepository(username, password, userlevel, devicetoken);
	        response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(LoginString.toString());
		} else {
		    response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(validatecheck.toString());
		}
		
	}

}
