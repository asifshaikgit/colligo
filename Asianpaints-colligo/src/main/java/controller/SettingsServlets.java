package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.SettingsRepo;
import Utilres.ValidationCtrl;
/**
 * Servlet implementation class SettingsServlets
 */
@WebServlet("/SettingsServlets")
public class SettingsServlets extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private SettingsRepo settdata = new SettingsRepo();
    private ValidationCtrl validationCtrl = new ValidationCtrl();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SettingsServlets() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String typeParam = request.getParameter("type").toString().trim();
		
	    switch(typeParam) {
	    case "filter":
	    	Object settingsInfo = settdata.getloadSettingsdata(typeParam);
			response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(settingsInfo.toString());
	    	break;
	    case "dropdown":
	    	JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
	    	Object dropdownValidation = validationCtrl.settingDropdownValidation(data);
	    	if(dropdownValidation.equals(true)) {
	    		Object getpayeenameDropdown = settdata.getpayeenameDropdown(data);
	    		response.setContentType("application/json");
	    		response.setCharacterEncoding("UTF-8");
	    		response.getWriter().write(getpayeenameDropdown.toString());
	    	}else {
	    		response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    response.getWriter().write(dropdownValidation.toString());
	    	}
	    	break;
	    }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
