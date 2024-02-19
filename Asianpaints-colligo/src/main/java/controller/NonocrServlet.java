package controller;

import static Constants.AsianConstants.devLogoPath;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.NonOcrRepository;
import Utilres.ValidationCtrl;
/**
 * Servlet implementation class NonocrServlet
 */
@WebServlet("/NonocrServlet")
public class NonocrServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ValidationCtrl validate = new ValidationCtrl();
	private NonOcrRepository nonOcrCall = new NonOcrRepository();
	/**
     * @see HttpServlet#HttpServlet()
     */
    public NonocrServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	    JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
	    String imagepath = this.getClass().getClassLoader().getResource("../../").getPath()+"images";
        String fullPathImage = URLDecoder.decode(imagepath, "UTF-8");
	    Object validatecheck = validate.validateNonOCRCollectionApidata(data);
	    if(validatecheck.equals(true)) {
	        Object dataNonOcr = nonOcrCall.checkSavingtheNonOcrData(data, fullPathImage, devLogoPath);
	        response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(dataNonOcr.toString());
	    } else {
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(validatecheck.toString());
	    }
	    
	}

}
