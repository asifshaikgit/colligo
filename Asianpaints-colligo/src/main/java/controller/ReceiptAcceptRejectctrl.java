package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.SalesDashboardlistrepo;
import Services.Salesdashboard;
import Utilres.ValidationCtrl;

/**
 * Servlet implementation class ReceiptAcceptRejectctrl
 */
@WebServlet("/ReceiptAcceptRejectctrl")
public class ReceiptAcceptRejectctrl extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ValidationCtrl validate = new ValidationCtrl();
	private Salesdashboard salesDash = new SalesDashboardlistrepo();
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReceiptAcceptRejectctrl() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	    JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
	    Object validatecheck = validate.validateActionreceiptApidata(data);
        if(validatecheck.equals(true)) {
            Object LoginString = salesDash.getUpdateReceiptDepoinfo(data);
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
