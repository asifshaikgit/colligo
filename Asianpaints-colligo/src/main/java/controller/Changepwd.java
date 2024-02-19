package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.ChangePwdRepo;
import Utilres.ValidationCtrl;

/**
 * Servlet implementation class Changepwd
 */
@WebServlet("/Changepwd")
public class Changepwd extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ChangePwdRepo pwdreq = new ChangePwdRepo();
	private ValidationCtrl validate = new ValidationCtrl();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Changepwd() {
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
		Object validatecheck = validate.validatechnagepwdApidata(data);
		if(validatecheck.equals(true)) {
			Object LoginString = pwdreq.updateThePwd(data);
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
