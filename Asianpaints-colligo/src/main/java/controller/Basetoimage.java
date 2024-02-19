package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.Base64decode;
import Services.Base64;
import Utilres.ValidationCtrl;

import static Constants.AsianConstants.devimagepath;

/**
 * Servlet implementation class Basetoimage
 */
@WebServlet("/Basetoimage")
public class Basetoimage extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Base64 base64image = new Base64decode();
    private ValidationCtrl validate = new ValidationCtrl();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Basetoimage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	    JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
	    Object validatecheck = validate.validateImageCapture(data);
        if(validatecheck.equals(true)) {
            Object imageCapturedata = base64image.getLoadDataBase64image(data,devimagepath);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(imageCapturedata.toString());
        } else {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(validatecheck.toString());
        }
	}

}
