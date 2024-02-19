package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Repository.SettingsRepo;

/**
 * Servlet implementation class Getbanknames
 */
@WebServlet("/Getbanknames")
public class Getbanknames extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SettingsRepo settdata = new SettingsRepo();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Getbanknames() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String typeParam = request.getParameter("country").toString().trim();
		Object settingsInfo = settdata.loadBanknameslist(typeParam);
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(settingsInfo.toString());
	}

}
