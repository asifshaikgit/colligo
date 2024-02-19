package Databaseconnection;

import static Constants.AsianConstants.DBCON_HOST;
import static Constants.AsianConstants.DBCON_PWD;
import static Constants.AsianConstants.DBCON_USER;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
	   
	public static Connection Connection() {
		Connection connection = null;
		try {
			Class.forName("com.sap.db.jdbc.Driver");
			connection=DriverManager.getConnection(DBCON_HOST,DBCON_USER,DBCON_PWD);
			return connection;
		  } catch (Exception ex) {
			// TODO: handle exception
            return null;
		}
	}
}
