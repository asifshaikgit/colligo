package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Repository.Dealerrepository;
import Utilres.Jsonresponse;

/**
 * Servlet implementation class DealerServlet
 */
@WebServlet("/Api/dealer-list")
public class DealerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Dealerrepository DealerSer = new Dealerrepository();
	Jsonresponse Josnutilres = new Jsonresponse();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DealerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String dealercountry = request.getParameter("countryId").toString().trim();
		String delarname = request.getParameter("filter").toString().trim();
		String userid = request.getParameter("userid").toString().trim();
		String isCountry = request.getParameter("isCountry").toString().trim();
		Object responseMap = DealerSer.getDealerInformation(userid, dealercountry, delarname, isCountry);
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(responseMap.toString());
	}

}
