package models;

public class Countriesclas {
	
	private String Id;
	private String CountryName;
	private String CountryCode;
	private String numberFormate;
	private Integer daysBack;
	private String ocrModelId;
	private String apiUserPassword;
	private String countryPriceLimit;
	private String decimal;
	private Boolean ocrStatus;
	private boolean cash;
	private boolean noncash;
 
    public Countriesclas(String countid, 
    		String countryname,
    		String currencycode, 
    		String countryCode, 
    		String numberFormate, 
    		Integer daysBack, 
    		String ocrModelId, 
    		String apiUserName, 
    		String apiUserPassword, 
    		String countryPriceLimit, String countrydecimalpoints, Boolean ocrStatus,boolean cash, boolean noncash) {
        // TODO Auto-generated constructor stub
        this.Id = countryCode;
        this.CountryName = countryname;
        this.currencyCode = currencycode;
        this.CountryCode = countid;
        this.numberFormate = numberFormate;
        this.daysBack = daysBack;
        this.ocrModelId = ocrModelId;
        this.apiUserName = apiUserName;
        this.apiUserPassword = apiUserPassword;
        this.countryPriceLimit = countryPriceLimit;
        this.decimal = countrydecimalpoints;
        this.ocrStatus = ocrStatus;
        this.cash = cash;
        this.noncash = noncash;
    }
	
	
	public boolean isCash() {
		return cash;
	}

	public void setCash(boolean cash) {
		this.cash = cash;
	}

	public boolean isNoncash() {
		return noncash;
	}

	public void setNoncash(boolean noncash) {
		this.noncash = noncash;
	}

	public String getOcrModelId() {
        return ocrModelId;
    }

    public void setOcrModelId(String ocrModelId) {
        this.ocrModelId = ocrModelId;
    }

    public String getApiUserName() {
        return apiUserName;
    }

    public void setApiUserName(String apiUserName) {
        this.apiUserName = apiUserName;
    }
    private String apiUserName;
	
	public Integer getDaysBack() {
        return daysBack;
    }

    public void setDaysBack(Integer daysBack) {
        this.daysBack = daysBack;
    }

    public String getNumberFormate() {
        return numberFormate;
    }

    public void setNumberFormate(String numberFormate) {
        this.numberFormate = numberFormate;
    }

    public String getCountryCode() {
		return CountryCode;
	}

	public void setCountryCode(String countryCode) {
		CountryCode = countryCode;
	}
	private String currencyCode;
	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}

	public String getCountryName() {
		return CountryName;
	}
	public void setCountryName(String countryName) {
		CountryName = countryName;
	}
	public String getApiUserPassword() {
        return apiUserPassword;
    }

    public void setApiUserPassword(String apiUserPassword) {
        this.apiUserPassword = apiUserPassword;
    }

	public String getCountryPriceLimit() {
		return countryPriceLimit;
	}

	public void setCountryPriceLimit(String countryPriceLimit) {
		this.countryPriceLimit = countryPriceLimit;
	}

	public String getDecimal() {
		return decimal;
	}

	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	public Boolean getOcrStatus() {
		return ocrStatus;
	}

	public void setOcrStatus(Boolean ocrStatus) {
		this.ocrStatus = ocrStatus;
	}
	@Override
	public String toString() {
		return "Countriesclas [Id=" + Id + ", CountryName=" + CountryName + ", CountryCode=" + CountryCode
				+ ", numberFormate=" + numberFormate + ", daysBack=" + daysBack + ", ocrModelId=" + ocrModelId
				+ ", apiUserPassword=" + apiUserPassword + ", countryPriceLimit=" + countryPriceLimit + ", decimal="
				+ decimal + ", ocrStatus=" + ocrStatus + ", cash=" + cash + ", noncash=" + noncash + ", apiUserName="
				+ apiUserName + ", currencyCode=" + currencyCode + "]";
	}

    
}
