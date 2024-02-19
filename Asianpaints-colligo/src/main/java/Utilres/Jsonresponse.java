package Utilres;

import static Constants.AsianConstants.ERROR_CODE_401;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_FLAG;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Jsonresponse {
	
	public static Map<String, Object> successResponse(String responcekey, Object msg) {
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(STATUS_CODE, SUCCESS_CODE);
		responseMap.put(STATUS_FLAG, 0);
		responseMap.put(STATUS_MESSAGE, SUCCESS);
		responseMap.put(responcekey, msg);
		return responseMap;
	}
	
	public Object successResponseCall(String responcekey, Object msg) {
	    JSONObject responseMap = new JSONObject();
        try {
            responseMap.put(STATUS_CODE, SUCCESS_CODE);
            responseMap.put(STATUS_FLAG, 0);
            responseMap.put(STATUS_MESSAGE, SUCCESS);
            responseMap.put(responcekey, msg);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responseMap;
	}
	
	public Object loginerrorResponseCall() {
	    JSONObject responseMap = new JSONObject();
        try {
            responseMap.put(STATUS_CODE, ERROR_CODE_401);
            responseMap.put(STATUS_FLAG, 0);
            responseMap.put(STATUS_MESSAGE, "User Account deleted, Please check and login.");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responseMap;
	}
	
	public Object successOcrResponseCall(String responcekey, Object msg, String responcekey1, Object msg1) {
        JSONObject responseMap = new JSONObject();
        try {
            responseMap.put(STATUS_CODE, SUCCESS_CODE);
            responseMap.put(STATUS_FLAG, 0);
            responseMap.put(STATUS_MESSAGE, SUCCESS);
            responseMap.put(responcekey, msg);
            responseMap.put(responcekey1, msg1);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responseMap;
    }
	
	public static String getStart(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String startDate = formatter.format(date) + " " + "00:00:00";
		return startDate;
	}

	public String getStartDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String startDate = formatter.format(date);
		return startDate;
	}
	
	public static String getOtpStartDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String startDate = formatter.format(date) + " " + "00:00:00";
		return startDate;
	}
	
	public String getStartDateFormat(String date) {
	    String startDateString = date;
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
	    try {
            return sdf2.format(sdf.parse(startDateString));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
	
	public String glpostingStartDateFormat(String inputdate) {
			Date date;
			String time = null;
			try {
				date = new SimpleDateFormat("dd/MM/yyyy").parse(inputdate);
				time = new SimpleDateFormat("dd.MM.yyyy").format(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return time;
	    }
	
	public String glpostingHippenDateFormat(String inputdate) {
		Date date;
		String time = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(inputdate);
			time = new SimpleDateFormat("dd/MM/yyyy").format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return time;
    }
	
	public String glpostingDotDateFormat(String inputdate) {
		Date date;
		String time = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(inputdate);
			time = new SimpleDateFormat("dd.MM.yyyy").format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return time;
    }
	
	public String getStartDatewithtime(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String startDate = formatter.format(date);
        return startDate;
    }
	
	public String getDatewithtime(String inputdate) {
		Date date;
		String time = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(inputdate);
			time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return time;
    }
	
	public String getDatewithonly(String inputdate) {
		Date date;
		String time = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(inputdate);
			time = new SimpleDateFormat("dd/MM/yyyy").format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return time;
    }
	
	public String getDateTimestamp(String inputdate) {
		Date date;
		String time = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(inputdate);
			time = new SimpleDateFormat("dd/MM/yyyy").format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return time;
    }

	public static String getEndDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String endDate = formatter.format(date) + " " + "23:59:59";
		return endDate;
	}
	
	public String addoneDaytoDate(String dateto) {
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        Calendar cal = Calendar.getInstance();
        try {
			cal.setTime( dateFormat.parse( dateto ) );
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        cal.add( Calendar.DATE, 1 );
        return (String)(dateFormat.format(cal.getTime()));
	}
	
	public Object errorValidationResponse(String errCode, Object errResMsg) {
        JSONObject responseMap = new JSONObject();
        try {
            responseMap.put(STATUS_CODE, errCode);
            responseMap.put(STATUS_FLAG, 1);
            responseMap.put(STATUS_MESSAGE, errResMsg);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responseMap;
    }

}
