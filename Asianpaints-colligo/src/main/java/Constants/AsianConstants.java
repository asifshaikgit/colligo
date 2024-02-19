package Constants;

public class AsianConstants {
	
	public static final String ENVRONMENT                          	   = "DEV";
	
	public static final String ERROR_CODE_500                          = "500";
	public static final String ERROR_CODE_401                          = "401";
	public static final String SUCCESS_CODE                            = "200";
	public static final String SUCCESS                                 = "success";	
	public static final String STATUS_CODE                             = "status";
	public static final String STATUS_FLAG                             = "statusFlag";
	public static final String STATUS_MESSAGE                          = "statusMessage";
	
	/* * DATABASE TABLE NAMES * */
	public static final String SCHEMA_TABLE                            = "AP_GLOBAL";
	public static final String COLLECTION_USER_LOGIN                   = "DL_COLLECTION_USER_LOGIN";
	public static final String OTP_TABLE                               = "DL_OTP_TABLE";
	public static final String COLLECTION_RECEIPT_TABLE                = "DL_COLLECTION_RECEIPT_DETAILS";
	public static final String DL_APP_USER_INFO                        = "DL_APP_USER_INFO";
	public static final String COUNTRIES_MASTER                        = "DL_COUNTRIES_MASTER";
	public static final String DEALER_MOBILE                           = "DL_DEALER_MOBILE";
	public static final String SETTINGS_TABLE                          = "DL_SETTINGS_TABLE";
	public static final String DEALER_OCR_DATA                         = "DL_DEALER_OCR_DATA";
	public static final String DL_DEALER_TABLE                         = "DL_DEALER_TABLE";
	public static final String LOGS_TABLE                              = "DL_LOGS_TABLE";
	public static final String INSTRUMENT_TABLE                        = "DL_RECEIPT_INSTRUMENT_DETAILS";
	public static final String SALES_MAP                               = "DL_DEALER_SALES_MAP";
	public static final String VERSION_MAPPYING                        = "DL_VERSION_TABLE";
	public static final String POST                                    = "POST";
	public static final String GET                                     = "GET";
	public static final String DL_FONT_COLLECTION_URL                  = "DL_FONT_COLLECTION_URL";
	public static final String DL_RECEIPT_INSTRUMENT_HISTORY_DETAILS   = "DL_RECEIPT_INSTRUMENT_HISTORY_DETAILS";
	public static final String DL_COLLECTION_RECEIPT_HISTORY_DETAILS   = "DL_COLLECTION_RECEIPT_HISTORY_DETAILS";
	public static final String DL_GL_POSTING_INSTRUMENTS   			   = "DL_GL_POSTING_INSTRUMENTS";
	public static final String SCANNER_TABLE                		   = "DL_SCANNER_TABLE";
	public static final String DL_RECEIPT_REMARKS_TABLE                = "DL_RECEIPT_REMARKS_TABLE";
	
	/*************** Database Connections ***************************/
	
	public static final String DBCON_HOST                              = "jdbc:sap://172.25.210.176:37615/?autocommit=true&useUnicode=yes&characterEncoding=utf-8";
	public static final String DBCON_USER                              = "SS_USER";
	public static final String DBCON_PWD                               = "Pass1234";
	
//	public static final String DBCON_HOST                              = "jdbc:sap://172.25.110.205:31115/?autocommit=true&useUnicode=yes&characterEncoding=utf-8";
//	public static final String DBCON_USER                              = "P001111310";
//	public static final String DBCON_PWD                               = "Asian1234";
	
	public static final String LOGIN_API_ASIAN                         = "https://api.asianpaints.com/loginad";
	
	/*************** OTP Details ***************************/
	
	/*******************************OCR***********************************/
	public static final String OCR_MODEL                               = "309f77da-1409-4b7e-8933-ea8c2fced252";
	public static final String OCR_API_KEY                             = "hCq3ubgt4lesD0FNl60uzrVrJ0Iq7_2u";
	/*********************************OCR*********************************/
	
	/*************** OMAN ***************************/
	public static final String OMAN_OTP_SERVICE_URL                    = "https://www.ismartsms.net/iBulkSMS/HttpWS/SMSDynamicRefIntlAPI.aspx";
	public static final String NEPAL_OTP_SERVICE_URL                   = "https://api.sparrowsms.com/v2/sms";
	
	public static final String LANKA_OTP_SERVICE_URL                   = "https://bulksms.hutch.lk/sendsmsmultimask2.php";
	public static final String BANGLADESH_OTP_SERVICE_URL              = "http://202.164.208.212/smsnet/bulk/api";
	public static final String EGYPRT_EN_OTP_SERVICE_URL               = "http://bulksms.advansystelecom.com/Message_Request.aspx";
	public static final String EGYPRT_AR_OTP_SERVICE_URL               = "http://bulksms.advansystelecom.com/Message_Request.aspx";
	public static final String INDONESIA_OTP_SERVICE_URL               = "https://sms-api.jatismobile.com/index.ashx";
	public static final String DUBAIE_OTP_LOGIN_SERVICE_URL            = "https://smartmessaging.etisalat.ae:5676/login/user";
	public static final String DUBAIE_OTP_SERVICE_URL                  = "https://sms-api.jatismobile.com/index.ashx";
	public static final String BEHARIN_OTP_SERVICE_URL                  = "https://ems.kalaam-telecom.com/SendSms.aspx";
	/*************** OTP Details***************************/
	
	public static final String Apigeeimagepath                         = "/tmp/hsperfdata_apigee/";
	public static final String devimagepath                            = "/";
	public static final String ApigeeLogoPath                          = "../images/asian-paints-logo.png";
	public static final String devLogoPath                             = "images/asian-paints-logo.png";
	
	/*************** EMAIL SMTP Details***************************/
	
//	public static final String MAIL_SMTP_HOST                         = "codetruai.com";
//	public static final String MAIL_SMTP_PORT                         = "587";
//	public static final String MAIL_SMTP_USER                         = "quotation@apps.asianpaints.com";
//	public static final String MAIL_SMTP_PWD                          = "password@1";
	
	/*************** EMAIL SMTP Details***************************/
//	public static final String SI_SO_PO_CREATE 						  = "https://api.asianpaints.com/colligo_sap_posting";
	public static final String SI_SO_PO_CREATE 						  = "https://apidev.asianpaints.com/colligo_sap_posting";
	public static final String FCMTOKENAPI 							  = "https://fcm.googleapis.com/fcm/send";
	public static final String FCMTOKENID							  = "AAAAQqSfWCM:APA91bGilhQ_COmq1xGfT0x7IREl3G-e0SkfXZHvq2iIAfISmdiDl08fQ46pe2FaMMBsUtYrUtBTUFq1sAtVyPXtqJ9EDrNGVWii8AnzAouAuRGyMcOY9ia0QIOVXKm28qaTF2HU7Wo-";
	public static final String TRIGGERMAIL		                      = "https://api.asianpaints.com/v2/triggerEmail";
	public static final String FORGOTPWDURL		                      = "https://apidev.asianpaints.com/v1/apg_lms_app/LdapServlet";
	public static final String CONTENTSTORAGE		                  = "https://api.asianpaints.com/v1/contentstorage?apikey=jJs5QR9LY5YJcMej3TjnMdXDZ8Air1Zz";
	
}
