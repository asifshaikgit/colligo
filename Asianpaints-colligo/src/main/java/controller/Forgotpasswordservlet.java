package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.Forgotrepo;
import Utilres.ValidationCtrl;

/**
 * Servlet implementation class Forgotpasswordservlet
 */
@WebServlet("/Forgotpasswordservlet")
public class Forgotpasswordservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Forgotrepo forgotcall = new Forgotrepo();
    private ValidationCtrl validate = new ValidationCtrl();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Forgotpasswordservlet() {
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
		Object validatecheck = validate.validateforgotApidata(data);
		if(validatecheck.equals(true)) {
			Object LoginString = forgotcall.checkUserId(data);
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
