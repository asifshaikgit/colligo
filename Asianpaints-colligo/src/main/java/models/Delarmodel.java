package models;

public class Delarmodel {
	
	private String dealerCode;
	
	private String dealerBankName;
    private String dealerCity;
    private String dealerEmail;
    private String dealerName;
	private Object dealerMobile;
	private String dealerbankBranch;
	
	public String getDealerCode() {
		return dealerCode;
	}
	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public Object getDealerMobile() {
		return dealerMobile;
	}
	public void setDealerMobile(Object dealerMobile) {
		this.dealerMobile = dealerMobile;
	}
	
	public String getDealerbankBranch() {
        return dealerbankBranch;
    }
    public void setDealerbankBranch(String dealerbankBranch) {
        this.dealerbankBranch = dealerbankBranch;
    }
    public String getDealerBankName() {
        return dealerBankName;
    }
    public void setDealerBankName(String dealerBankName) {
        this.dealerBankName = dealerBankName;
    }
    
	public String getDealerCity() {
		return dealerCity;
	}
	public void setDealerCity(String dealerCity) {
		this.dealerCity = dealerCity;
	}
	public String getDealerEmail() {
		return dealerEmail;
	}
	public void setDealerEmail(String dealerEmail) {
		this.dealerEmail = dealerEmail;
	}
	
}
