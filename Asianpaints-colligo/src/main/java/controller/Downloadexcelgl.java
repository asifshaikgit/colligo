package controller;

//import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
//import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Downloadexcelgl
 */
@WebServlet("/Downloadexcelgl")
public class Downloadexcelgl extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Downloadexcelgl() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
//			XSSFWorkbook workbook = new XSSFWorkbook();
//			XSSFSheet sheet = workbook.createSheet("Calculate Simple Interest");
//			Row header = sheet.createRow(0);
//			header.createCell(0).setCellValue("Pricipal");
//			
//			Row dataRow = sheet.createRow(1);
//			dataRow.createCell(0).setCellValue(14500d);
//			try {
//		        FileOutputStream out =  new FileOutputStream(new File("formulaDemo.xlsx"));
//		        workbook.write(out);
//		        out.close();
//		        System.out.println("Excel with foumula cells written successfully");
//		          
//		    } catch (FileNotFoundException e) {
//		        e.printStackTrace();
//		    } catch (IOException e) {
//		        e.printStackTrace();
//		    }
//			HSSFWorkbook wb = new HSSFWorkbook();
//			HSSFSheet sheet = wb.createSheet();
//			HSSFRow row = sheet.createRow(0);
//			HSSFCell cell = row.createCell(0);
//			cell.setCellValue("Some text");

			// write it as an excel attachment
//			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
//			wb.write(outByteStream);
//			byte [] outArray = outByteStream.toByteArray();
			
//			response.setContentType("application/ms-excel");
//			response.setContentLength(outArray.length);
//			response.setHeader("Expires:", "0"); // eliminates browser caching
//			response.setHeader("Content-Disposition", "attachment; filename=testxls.xls");
//			OutputStream outStream = response.getOutputStream();
//			outStream.write(outArray);
//			outStream.flush();
//			String filename = "D:\\glposting.xls";
//			System.err.println(filename);
//			HSSFWorkbook workbook = new HSSFWorkbook(); 
//			System.err.println(1);
//			FileOutputStream fileOut = new FileOutputStream(filename);  
//			System.err.println(2);
//			HSSFSheet sheet = workbook.createSheet("January");   
//			System.err.println(3);
//			HSSFRow rowhead = sheet.createRow((short)0);  
//			System.err.println(4);
//			//creating cell by using the createCell() method and setting the values to the cell by using the setCellValue() method  
//			rowhead.createCell((short) 0).setCellValue("S.No."); 
//			System.err.println(5);
//			rowhead.createCell((short) 1).setCellValue("Customer Name");  
//			rowhead.createCell((short) 2).setCellValue("Account Number");  
//			rowhead.createCell((short) 3).setCellValue("e-mail");  
//			rowhead.createCell((short) 4).setCellValue("Balance");  
//			System.err.println(6);
//			FileOutputStream fileOuter = new FileOutputStream(filename);  
//			workbook.write(fileOuter);  
//			System.err.println(7);
//			//closing the Stream  
//			fileOut.close();  
//			//closing the workbook  
//			System.err.println(8);
////			workbook.close();  
//			System.err.println(9);
//			//prints the message on the console  
//			System.out.println("Excel file has been generated successfully.");  
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

}
