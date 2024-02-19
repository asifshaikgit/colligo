package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.Ocreopsitory;
import Utilres.ValidationCtrl;
/**
 * Servlet implementation class OcrServlet
 */
@WebServlet("/OcrServlet")
public class OcrServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Ocreopsitory ocrCall = new Ocreopsitory();
    private ValidationCtrl validate = new ValidationCtrl();
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
		Object validatecheck = validate.validateOcrRequest(data);
		if(validatecheck.equals(true)) {
		    Object Ocresponse = ocrCall.getChecktheOcrData(data);
		    response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(Ocresponse.toString());
		} else {
		    response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(validatecheck.toString());
		}
		
	}

}
