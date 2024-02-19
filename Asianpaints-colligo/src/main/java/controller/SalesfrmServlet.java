package controller;

import static Constants.AsianConstants.devLogoPath;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.Salesrepository;
import Utilres.ValidationCtrl;
/**
 * Servlet implementation class SalesfrmServlet
 */
@WebServlet("/SalesfrmServlet")
public class SalesfrmServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
     private Salesrepository saleserv = new Salesrepository();
     private ValidationCtrl validate = new ValidationCtrl();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SalesfrmServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
    	JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
    	System.out.println(data.toString());
    	Object validatecheck = validate.validateCollectionApidata(data);
    	String imagepath = this.getClass().getClassLoader().getResource("../../").getPath();
        String fullPathImage = URLDecoder.decode(imagepath, "UTF-8");
    	if(validatecheck.equals(true)) {
    	    JSONObject salesObject = new JSONObject();
            try {
                salesObject.put("ocr", data.get("ocr").getAsString());
                salesObject.put("dealerId", data.get("dealerId").getAsString());
                salesObject.put("finalRecepitAmount", data.get("finalRecepitAmount").getAsString());
                salesObject.put("receivedAmount", data.get("receivedAmount").getAsString());
                salesObject.put("dealerMobile", data.get("dealermobile").getAsString());
                salesObject.put("userid", data.get("userid").getAsString());
                salesObject.put("paymentype", data.get("paymentype").getAsString());
                salesObject.put("otptoken", data.get("otptoken").getAsString());
                salesObject.put("dealerRepresentative", data.get("dealerRepresentative").getAsString());
                salesObject.put("dateofreceving", data.get("dateofreceving").getAsString());
                salesObject.put("dealerRemark", data.get("dealerRemark").getAsString());
                salesObject.put("saveflag", data.get("saveflag").getAsInt());
                salesObject.put("receiptid", data.get("receiptid").getAsString());
                salesObject.put("countrycode", data.has("countrycode") ? data.get("countrycode").getAsString() : "");
                salesObject.put("statuscollection", data.has("statuscollection") ? data.get("statuscollection").getAsString() : "");
                salesObject.put("receiptremarks", data.has("receiptremarks") ? data.get("receiptremarks").getAsString() : "");
                salesObject.put("lang", data.has("lang") ? data.get("lang").getAsString() : "en");
                Object SalesResponse = saleserv.cashreceiptflowmanage(salesObject,fullPathImage, devLogoPath);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(SalesResponse.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
    	} else {
    	    response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(validatecheck.toString());
    	}
		
	}

}
