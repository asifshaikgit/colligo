package com.dl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
//		Connection connection = Database.Connection();
//		String q="SELECT dcm.TIMEZONE FROM AP_GLOBAL.DL_COUNTRIES_MASTER dcm WHERE dcm.COUNTRY_CODE = 'OM'";
//		ResultSet set = connection.createStatement().executeQuery(q);
//		System.out.println("dd");
//		while(set.next()) {
//			String string = set.getString("TIMEZONE");
//			System.out.println(string);

//	}//

		String string = "+ 5:48 Hrs";
		Pattern pattern = Pattern.compile("(\\d{1,2})(?::(\\d{2}))?");
		Matcher matcher = pattern.matcher(string);

		if (matcher.find()) {
			String hour = matcher.group(1);
			String minute = matcher.group(2) != null ? matcher.group(2) : "00";
			System.out.println("Hour: " + hour);
			System.out.println("Minute: " + minute);
			
			Calendar calendar = Calendar.getInstance();
			System.out.println(calendar.getTime());
			calendar.setTime(new Date());
			calendar.add(calendar.HOUR, Integer.parseInt(hour));
			calendar.add(calendar.MINUTE, Integer.parseInt(minute));
			Timestamp create = new Timestamp(calendar.getTimeInMillis());
			System.out.println(create);

		}

	}

}
