package Utilres;

import static Constants.AsianConstants.ERROR_CODE_500;

import java.util.regex.Pattern;

import com.google.gson.JsonObject;

public class ValidationCtrl extends Jsonresponse {
    
    public Object valiadateOcrApidata(JsonObject data) {
        return true;
    }
    
    public Object validateloginApidata(JsonObject data) {
    	System.out.println("VALIDATE API FIRST CAll");
            try {
                if(!data.has("username")) {
                	System.out.println("VALIDATE USERNAME VALIDATE CAll");
                    return this.errorValidationResponse(ERROR_CODE_500, "username key is not found.");
                } else if(!data.has("password")) {
                	System.out.println("VALIDATE PASSWORD VALIDATE CAll");
                    return this.errorValidationResponse(ERROR_CODE_500, "password key is not found.");
                } else if(data.has("username") && data.get("username").getAsString().isEmpty()) {
                	System.out.println("VALIDATE USERNAME VALIDATE IF IS EMPTY");
                    return this.errorValidationResponse(ERROR_CODE_500, "username is empty.");
                } else if(data.has("password") && data.get("password").getAsString().isEmpty()) {
                	System.out.println("VALIDATE PASSWORD VALIDATE IF IS EMPTY");
                    return this.errorValidationResponse(ERROR_CODE_500, "password is empty.");
                }
                return true;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println(e.getMessage().toString());
                return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
            }
    }
    
    public Object validateCollectionApidata(JsonObject data) {
        try {
            if(!data.has("ocr")) {
                return this.errorValidationResponse(ERROR_CODE_500, "ocr key is not found.");
            } else if(!data.has("dealerId")) {
                return this.errorValidationResponse(ERROR_CODE_500, "dealerId key is not found.");
            } else if(!data.has("finalRecepitAmount")) {
                return this.errorValidationResponse(ERROR_CODE_500, "finalRecepitAmount key is not found.");
            } else if(!data.has("receivedAmount")) {
                return this.errorValidationResponse(ERROR_CODE_500, "receivedAmount key is not found.");
            } else if(!data.has("userid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid key is not found.");
            } else if(!data.has("paymentype")) {
                return this.errorValidationResponse(ERROR_CODE_500, "paymentype key is not found.");
            } else if(!data.has("otptoken")) {
                return this.errorValidationResponse(ERROR_CODE_500, "otptoken key is not found.");
            } else if(!data.has("dealerRepresentative")) {
                return this.errorValidationResponse(ERROR_CODE_500, "dealerRepresentative key is not found.");
            } else if(!data.has("dealerRemark")) {
                return this.errorValidationResponse(ERROR_CODE_500, "dealerRemark key is not found.");
            } else if(!data.has("saveflag")) {
                return this.errorValidationResponse(ERROR_CODE_500, "saveflag key is not found.");
            } else if(data.has("saveflag") && data.get("saveflag").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "saveflag is empty.");
            } else if(data.has("ocr") && data.get("ocr").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "ocr is empty.");
            } else if(data.has("dealerId") && data.get("dealerId").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "Dealer ID is empty.");
            } else if(data.has("receivedAmount") && data.get("receivedAmount").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "Received Amount is empty.");
            } else if(data.has("userid") && data.get("userid").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "User ID is empty.");
            } else if(data.has("paymentype") && data.get("paymentype").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "Payment Type is empty.");
            } else if(data.has("dealerRepresentative") && data.get("dealerRepresentative").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "Dealer Representative is empty.");
            } else if(data.has("dealerRemark") && data.get("dealerRemark").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "Dealer Remark is empty.");
            }
            /* else if(data.has("otptoken") && data.get("otptoken").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "OTP Token is empty.");
            } */
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
        }        
    }

    public Object validateOcrRequest(JsonObject data) {
        // TODO Auto-generated method stub
        try {
//            if(!data.has("azurelink")) {
//                return this.errorValidationResponse(ERROR_CODE_500, "azurelink key is not found.");
//            } else if(!data.has("userid")) {
//                return this.errorValidationResponse(ERROR_CODE_500, "userid key is not found.");
//            } else if(!data.has("instrumenttype")) {
//                return this.errorValidationResponse(ERROR_CODE_500, "instrumenttype key is not found.");
//            } else if(data.has("azurelink") && data.get("azurelink").getAsString().isEmpty()) {
//                return this.errorValidationResponse(ERROR_CODE_500, "azurelink is empty.");
//            } else if(data.has("userid") && data.get("userid").getAsString().isEmpty()) {
//                return this.errorValidationResponse(ERROR_CODE_500, "user Id is empty.");
//            } else if(data.has("instrumenttype") && data.get("instrumenttype").getAsString().isEmpty()) {
//                return this.errorValidationResponse(ERROR_CODE_500, "instrument type is empty.");
//            }
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return this.errorValidationResponse(ERROR_CODE_500, "Something went 22 wrong!, please try again.");
        }
    }
    
    public Object validateNonOCRCollectionApidata(JsonObject data) {
        try {
            if(!data.has("ocr")) {
                return this.errorValidationResponse(ERROR_CODE_500, "ocr key is not found.");
            } else if(!data.has("dealerId")) {
                return this.errorValidationResponse(ERROR_CODE_500, "dealerId key is not found.");
            } else if(!data.has("dealerMobile")) {
                return this.errorValidationResponse(ERROR_CODE_500, "dealer Mobile key is not found.");
            } else if(!data.has("finalRecepitAmount")) {
                return this.errorValidationResponse(ERROR_CODE_500, "finalRecepitAmount key is not found.");
            } else if(!data.has("userid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid key is not found.");
            } else if(!data.has("paymentype")) {
                return this.errorValidationResponse(ERROR_CODE_500, "paymentype key is not found.");
            } else if(!data.has("dealerRepresentative")) {
                return this.errorValidationResponse(ERROR_CODE_500, "dealerRepresentative key is not found.");
            } else if(!data.has("dealerRemark")) {
                return this.errorValidationResponse(ERROR_CODE_500, "dealerRemark key is not found.");
            } else if(!data.has("instruments")) {
                return this.errorValidationResponse(ERROR_CODE_500, "instruments key is not found.");
            } else if(!data.has("saveflag")) {
                return this.errorValidationResponse(ERROR_CODE_500, "saveflag key is not found.");
            } else if(!data.has("receiptid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "receiptid key is not found.");
            } else if(data.has("saveflag") && data.get("saveflag").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "saveflag is empty.");
            } else if(data.has("ocr") && data.get("ocr").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "ocr is empty.");
            } else if(data.has("dealerId") && data.get("dealerId").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "Dealer ID is empty.");
            } else if(data.has("userid") && data.get("userid").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "User ID is empty.");
            } else if(data.has("paymentype") && data.get("paymentype").getAsString().isEmpty() &&  data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "Payment Type is empty.");
            } else if(data.has("dealerRepresentative") && data.get("dealerRepresentative").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "Dealer Representative is empty.");
            } else if(data.has("dealerRemark") && data.get("dealerRemark").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "Dealer Remark is empty.");
            }
            /*else if(data.has("dealerMobile") && data.get("dealerMobile").getAsString().isEmpty() && data.has("saveflag") && data.get("saveflag").getAsInt() == 1) {
                return this.errorValidationResponse(ERROR_CODE_500, "Dealer Mobile is empty.");
            } */
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
        }        
    }

    public Object validateImageCapture(JsonObject data) {
        // TODO Auto-generated method stub
        try {
            if(!data.has("userid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid key is not found.");
            } else if(!data.has("imageData")) {
                return this.errorValidationResponse(ERROR_CODE_500, "imageData key is not found.");
            } else if(data.has("userid") && data.get("userid").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid is empty.");
            } else if(data.has("imageData") && data.get("imageData").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "imageData is empty.");
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
        }
    }
    
    public Object saleslistDashboard(JsonObject data) {
        try {
            if(!data.has("userid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid key is not found.");
            } else if(!data.has("page")) {
                return this.errorValidationResponse(ERROR_CODE_500, "page key is not found.");
            } else if(!data.has("datefrom")) {
                return this.errorValidationResponse(ERROR_CODE_500, "datefrom key is not found.");
            } else if(!data.has("dateto")) {
                return this.errorValidationResponse(ERROR_CODE_500, "dateto key is not found.");
            } else if(data.has("userid") && data.get("userid").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid is empty.");
            } else if(data.has("page") && data.get("page").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "page is empty.");
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
        }
    }

    public Object validateActionreceiptApidata(JsonObject data) {
        // TODO Auto-generated method stub
        try {
            if(!data.has("receiptnumber")) {
                return this.errorValidationResponse(ERROR_CODE_500, "receiptnumber key is not found.");
            } else if(!data.has("receiptstatus")) {
                return this.errorValidationResponse(ERROR_CODE_500, "receiptstatus key is not found.");
            } else if(data.has("receiptnumber") && data.get("receiptnumber").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "receipt number is empty.");
            } else if(data.has("receiptstatus") && data.get("receiptstatus").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "receipt status is empty.");
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
        }
    }
    
    public Object validateaccountcheckAPIdata(JsonObject data) {
        try {
            if(!data.has("dealerid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "dealerid key is not found.");
            } else if(!data.has("accountnumber")) {
                return this.errorValidationResponse(ERROR_CODE_500, "accountnumber key is not found.");
            } else if(data.has("accountnumber") && data.get("accountnumber").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "accountnumber is empty.");
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
        }
        
    }
    
    public Object validateforgotApidata(JsonObject data) {
        try {
        	String EMAIL_PATTERN = "^(.+)@(\\S+)$";
        	Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        	System.err.println(pattern.matcher(data.get("emailid").toString()).matches());
            if(!data.has("emailid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "emailid key is not found.");
            } else if(data.has("emailid") && data.get("emailid").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "emailid is empty.");
            } else if(data.has("emailid") && data.get("emailid").getAsString().length() > 0 && !pattern.matcher(data.get("emailid").toString()).matches()) {
                return this.errorValidationResponse(ERROR_CODE_500, "emailid is not valid.");
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
        }
        
    }
    
    public Object validateforgotverifyotpApidata(JsonObject data) {
        try {
        	String EMAIL_PATTERN = "^(.+)@(\\S+)$";
        	Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        	System.err.println(pattern.matcher(data.get("emailid").toString()).matches());
            if(!data.has("emailid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "emailid key is not found.");
            } else if(!data.has("userid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid key is not found.");
            } else if(!data.has("otptoken")) {
                return this.errorValidationResponse(ERROR_CODE_500, "otptoken key is not found.");
            } else if(!data.has("otp")) {
                return this.errorValidationResponse(ERROR_CODE_500, "otp key is not found.");
            } else if(data.has("emailid") && data.get("emailid").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "emailid is empty.");
            } else if(data.has("emailid") && data.get("emailid").getAsString().length() > 0 && !pattern.matcher(data.get("emailid").toString()).matches()) {
                return this.errorValidationResponse(ERROR_CODE_500, "emailid is not valid.");
            } else if(data.has("userid") && data.get("userid").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid is empty.");
            } else if(data.has("otptoken") && data.get("otptoken").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "otptoken is empty.");
            } else if(data.has("otp") && data.get("otp").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "otp is empty.");
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
        }
        
    }
    
    public Object validatechnagepwdApidata(JsonObject data) {
        try {
        	String EMAIL_PATTERN = "^(.+)@(\\S+)$";
        	Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        	System.err.println(pattern.matcher(data.get("emailid").toString()).matches());
            if(!data.has("emailid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "emailid key is not found.");
            } else if(!data.has("userid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid key is not found.");
            } else if(!data.has("password")) {
                return this.errorValidationResponse(ERROR_CODE_500, "password key is not found.");
            } else if(data.has("emailid") && data.get("emailid").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "emailid is empty.");
            } else if(data.has("emailid") && data.get("emailid").getAsString().length() > 0 && !pattern.matcher(data.get("emailid").toString()).matches()) {
                return this.errorValidationResponse(ERROR_CODE_500, "emailid is not valid.");
            } else if(data.has("userid") && data.get("userid").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid is empty.");
            } else if(data.has("password") && data.get("password").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "password is empty.");
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
        }
        
    }
    
    public Object validatelogoutApidata(JsonObject data) {
        try {
            if(!data.has("userid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid key is not found.");
            } else if(data.has("userid") && data.get("userid").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid is empty.");
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
        }
        
   }
    
    public Object validateStorePdfApidata(JsonObject data) {
        try {
            if(!data.has("billingDocumentNumber")) {
                return this.errorValidationResponse(ERROR_CODE_500, "billingDocumentNumber key is not found.");
            } else if(!data.has("billingDate")) {
                return this.errorValidationResponse(ERROR_CODE_500, "billingDate key is not found.");
            } else if(!data.has("odnNumber")) {
                return this.errorValidationResponse(ERROR_CODE_500, "odnNumber key is not found.");
            } else if(!data.has("countryCode")) {
                return this.errorValidationResponse(ERROR_CODE_500, "countryCode key is not found.");
            } else if(!data.has("pdfAzureLink")) {
                return this.errorValidationResponse(ERROR_CODE_500, "pdfAzureLink key is not found.");
            } else if(!data.has("userid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid key is not found.");
            } else if(data.has("billingDocumentNumber") && data.get("billingDocumentNumber").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "billingDocumentNumber is empty.");
            } else if(data.has("billingDate") && data.get("billingDate").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "billingDate is empty.");
            } else if(data.has("odnNumber") && data.get("odnNumber").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "odnNumber is empty.");
            } else if(data.has("countryCode") && data.get("countryCode").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "countryCode is empty.");
            } else if(data.has("pdfAzureLink") && data.get("pdfAzureLink").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "pdfAzureLink is empty.");
            } else if(data.has("userid") && data.get("userid").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid is empty.");
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
        }
        
   }
    
    public Object validateautoloadApidata(JsonObject data) {
        try {
            if(!data.has("userid")) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid key is not found.");
            } else if(!data.has("appversion")) {
                return this.errorValidationResponse(ERROR_CODE_500, "appversion key is not found.");
            } else if(!data.has("logintoken")) {
                return this.errorValidationResponse(ERROR_CODE_500, "logintoken key is not found.");
            } else if(data.has("userid") && data.get("userid").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "userid is empty.");
            }  else if(data.has("appversion") && data.get("appversion").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "appversion is empty.");
            }  else if(data.has("logintoken") && data.get("logintoken").getAsString().isEmpty()) {
                return this.errorValidationResponse(ERROR_CODE_500, "logintoken is empty.");
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
        }
        
   }

    public Object settingDropdownValidation(JsonObject data) {
    	try {
            if(!data.has("countrycode")) {
            	 return this.errorValidationResponse(ERROR_CODE_500, "countrycode key is not found.");
            }else if(!data.has("category")) {
            	 return this.errorValidationResponse(ERROR_CODE_500, "category key is not found.");
            }else if(data.has("countrycode") && data.get("countrycode").getAsString().isEmpty()) {
            	 return this.errorValidationResponse(ERROR_CODE_500, "countrycode is empty.");
            }else if(data.has("category") && data.get("category").getAsString().isEmpty()) {
            	 return this.errorValidationResponse(ERROR_CODE_500, "category is empty.");
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return this.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again.");
        }
    }
}
