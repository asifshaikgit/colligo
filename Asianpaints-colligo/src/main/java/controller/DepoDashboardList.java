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

/**
 * Servlet implementation class DepoDashboardList
 */
@WebServlet("/DepoDashboardList")
public class DepoDashboardList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Salesdashboard salesDash = new SalesDashboardlistrepo();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DepoDashboardList() {
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
	    Object LoginString = salesDash.ListSalesdashboard(data,"depo");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(LoginString.toString());
	}

}
