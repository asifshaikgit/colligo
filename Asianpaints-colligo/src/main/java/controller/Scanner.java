package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.Scannerepo;
import Utilres.ValidationCtrl;

/**
 * Servlet implementation class Scanner
 */
@WebServlet("/Scanner")
public class Scanner extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ValidationCtrl validate = new ValidationCtrl();
	private Scannerepo scannerep = new Scannerepo();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Scanner() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String userid = request.getParameter("userid").toString().trim();
		String filter = request.getParameter("filter").toString().trim();
		String search = request.getParameter("search").toString().trim();
		String fromdate = request.getParameter("fromdate").toString().trim();
		String todate = request.getParameter("todate").toString().trim();
		Object responseMap = scannerep.getPdfData(userid, filter, search, fromdate,todate);
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(responseMap.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
    	Object validatecheck = validate.validateStorePdfApidata(data);
    	if(validatecheck.equals(true)) {
            Object StorePdfResponse = scannerep.savePdfData(data);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(StorePdfResponse.toString());
    	} else {
    	    response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(validatecheck.toString());
    	}
    	
	}

}
