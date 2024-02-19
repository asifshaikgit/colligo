package models;

public class Collectionreports {
	
	private Integer Id;
	private String Ocrtype;
	private Integer dealerName;
	private String dealerMonile;
	private String dealerEmail;
	private Integer modeOfPayment;
	private String finalReceiptAmount;
	private String receiedAmount;
	private String dateOfReceiving;
	private String receiptNumber;
	private String receiptDate;
	private String otp;
	private Integer receiptIsTrash;
	private String receiptFileName;
	private Integer createdBy;
	private String updatedBy;
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public String getOcrtype() {
		return Ocrtype;
	}
	public void setOcrtype(String ocrtype) {
		Ocrtype = ocrtype;
	}
	public Integer getDealerName() {
		return dealerName;
	}
	public void setDealerName(Integer dealerName) {
		this.dealerName = dealerName;
	}
	public String getDealerMonile() {
		return dealerMonile;
	}
	public void setDealerMonile(String dealerMonile) {
		this.dealerMonile = dealerMonile;
	}
	public String getDealerEmail() {
		return dealerEmail;
	}
	public void setDealerEmail(String dealerEmail) {
		this.dealerEmail = dealerEmail;
	}
	public Integer getModeOfPayment() {
		return modeOfPayment;
	}
	public void setModeOfPayment(Integer modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}
	public String getFinalReceiptAmount() {
		return finalReceiptAmount;
	}
	public void setFinalReceiptAmount(String finalReceiptAmount) {
		this.finalReceiptAmount = finalReceiptAmount;
	}
	public String getReceiedAmount() {
		return receiedAmount;
	}
	public void setReceiedAmount(String receiedAmount) {
		this.receiedAmount = receiedAmount;
	}
	public String getDateOfReceiving() {
		return dateOfReceiving;
	}
	public void setDateOfReceiving(String dateOfReceiving) {
		this.dateOfReceiving = dateOfReceiving;
	}
	public String getReceiptNumber() {
		return receiptNumber;
	}
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}
	public String getReceiptDate() {
		return receiptDate;
	}
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public Integer getReceiptIsTrash() {
		return receiptIsTrash;
	}
	public void setReceiptIsTrash(Integer receiptIsTrash) {
		this.receiptIsTrash = receiptIsTrash;
	}
	public String getReceiptFileName() {
		return receiptFileName;
	}
	public void setReceiptFileName(String receiptFileName) {
		this.receiptFileName = receiptFileName;
	}
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	private String createdAt;
}
