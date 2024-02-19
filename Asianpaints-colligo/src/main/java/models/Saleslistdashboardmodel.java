package models;

public class Saleslistdashboardmodel {
    
    private String receiptNumber;
    private String dealerName;
    private String receiptFinalAmount;
    private String createdAt;
    private String modelOfPayment;
    private String currency;
    private String receiptStatus;
    private String collectionDraftstatus;
    private String dealerId;
    
    public Saleslistdashboardmodel(String receiptNumber, String createdAt, String finalReceiptAmount,
            String paymentMode, String dealerStatustring, String dealerName, String currency, String collectionDraftstatus, String dealerId) {
        // TODO Auto-generated constructor stub
        this.receiptNumber = receiptNumber;
        this.dealerName = dealerName;
        this.receiptFinalAmount = finalReceiptAmount;
        this.createdAt = createdAt;
        this.modelOfPayment = paymentMode;
        this.currency = currency;
        this.receiptStatus = dealerStatustring;
        this.collectionDraftstatus = collectionDraftstatus;
        this.dealerId = dealerId;
    }
    public String getReceiptNumber() {
        return receiptNumber;
    }
    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
    public String getDealerName() {
        return dealerName;
    }
    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }
    public String getReceiptFinalAmount() {
        return receiptFinalAmount;
    }
    public void setReceiptFinalAmount(String receiptFinalAmount) {
        this.receiptFinalAmount = receiptFinalAmount;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getModelOfPayment() {
        return modelOfPayment;
    }
    public void setModelOfPayment(String modelOfPayment) {
        this.modelOfPayment = modelOfPayment;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getReceiptStatus() {
        return receiptStatus;
    }
    public void setReceiptStatus(String receiptStatus) {
        this.receiptStatus = receiptStatus;
    }
    public String getCollectionDraftstatus() {
        return collectionDraftstatus;
    }
    public void setCollectionDraftstatus(String collectionDraftstatus) {
        this.collectionDraftstatus = collectionDraftstatus;
    }
	public String getDealerId() {
		return dealerId;
	}
	public void setDealerId(String dealerId) {
		this.dealerId = dealerId;
	}
    
}
