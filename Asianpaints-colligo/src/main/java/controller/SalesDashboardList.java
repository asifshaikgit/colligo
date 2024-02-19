package controller;

import static Constants.AsianConstants.ERROR_CODE_500;

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
 * Servlet implementation class SalesDashboardList
 */
@WebServlet("/SalesDashboardList")
public class SalesDashboardList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ValidationCtrl validate = new ValidationCtrl();
	private Salesdashboard salesDash = new SalesDashboardlistrepo();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
	
    public SalesDashboardList() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        if(!request.getParameterMap().containsKey("receiptId")) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(validate.errorValidationResponse(ERROR_CODE_500, "receiptId key not found").toString());
        } else if(!request.getParameterMap().containsKey("userid")) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(validate.errorValidationResponse(ERROR_CODE_500, "userid key not found").toString());
        } else if(request.getParameterMap().containsKey("receiptId") && request.getParameter("receiptId").toString().isEmpty()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(validate.errorValidationResponse(ERROR_CODE_500, "Receipt Id is empty").toString());
        } else if(request.getParameterMap().containsKey("userid") && request.getParameter("userid").toString().isEmpty()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(validate.errorValidationResponse(ERROR_CODE_500, "User Id is empty").toString());
        } else {
            String receiptId = request.getParameter("receiptId").toString().trim();
            String userid = request.getParameter("userid").toString().trim();
            Object LoginString = salesDash.getSalesReceiptData(receiptId, userid);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(LoginString.toString());
            System.out.println(response);
        }
        
    }
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	    JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
	    Object validatecheck = validate.saleslistDashboard(data);
        if(validatecheck.equals(true)) {
            Object LoginString = salesDash.ListSalesdashboard(data,"sales");
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
