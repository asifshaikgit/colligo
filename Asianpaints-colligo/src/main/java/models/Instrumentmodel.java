package models;

public class Instrumentmodel {
    
    private String chequeNumber;
    private String instrument_amount;
    private String instrument_bank_code;
    private String instrument_bank_branch;
    private String instrument_remarks;
    private String instrument_type;
    private Integer sqNumber;
    private String instrument_number;
    private String instrument_date;
    private String instrument_date_of_receving;
    private String instrument_image_url;
	private String instrument_bank_account_number;
	private String instrument_micr_no;
	private String instrument_payee_name;
	private String instrumentid;
	private String genId;
	private String id;
	private String instrumentstatus;
	
	public String getInstrumentstatus() {
		return instrumentstatus;
	}

	public void setInstrumentstatus(String instrumentstatus) {
		this.instrumentstatus = instrumentstatus;
	}

	public Instrumentmodel(String iD, int sqNumber, String receiptId, String instrumentAmount, String instrumentBankCode, String instrumentbankBranch, String instrumentRemarks, String instrumenType, String instrumentNumber, String instrumentDate, String instrumentDateofReceving, String instrumentImage, String instrumentAccountNumber, String instrumentmicrno, String instrumentpayeename, String instrumentid, String genId, String instrumentStatus) {
        // TODO Auto-generated constructor stub
        this.instrument_amount = instrumentAmount;
        this.instrument_bank_code = instrumentBankCode;
        this.instrument_bank_branch = instrumentbankBranch;
        this.instrument_remarks = instrumentRemarks;
        this.instrument_type = instrumenType;
        this.sqNumber = sqNumber;
        this.instrument_number = instrumentNumber;
        this.instrument_date = instrumentDate;
        this.instrument_date_of_receving = instrumentDateofReceving;
        this.instrument_image_url = instrumentImage;
        this.instrument_bank_account_number = instrumentAccountNumber;
        this.instrument_micr_no = instrumentmicrno;
        this.instrument_payee_name = instrumentpayeename;
        this.instrumentid = instrumentid;
        this.genId = genId;
        this.id = iD;
        this.instrumentstatus = instrumentStatus;
    }
	
	public String getChequeNumber() {
		return chequeNumber;
	}
	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}
	public String getInstrument_amount() {
		return instrument_amount;
	}
	public void setInstrument_amount(String instrument_amount) {
		this.instrument_amount = instrument_amount;
	}
	public String getInstrument_bank_code() {
		return instrument_bank_code;
	}
	public void setInstrument_bank_code(String instrument_bank_code) {
		this.instrument_bank_code = instrument_bank_code;
	}
	public String getInstrument_bank_branch() {
		return instrument_bank_branch;
	}
	public void setInstrument_bank_branch(String instrument_bank_branch) {
		this.instrument_bank_branch = instrument_bank_branch;
	}
	public String getInstrument_remarks() {
		return instrument_remarks;
	}
	public void setInstrument_remarks(String instrument_remarks) {
		this.instrument_remarks = instrument_remarks;
	}
	public String getInstrument_type() {
		return instrument_type;
	}
	public void setInstrument_type(String instrument_type) {
		this.instrument_type = instrument_type;
	}
	public Integer getSqNumber() {
		return sqNumber;
	}
	public void setSqNumber(Integer sqNumber) {
		this.sqNumber = sqNumber;
	}
	public String getInstrument_number() {
		return instrument_number;
	}
	public void setInstrument_number(String instrument_number) {
		this.instrument_number = instrument_number;
	}
	public String getInstrument_date() {
		return instrument_date;
	}
	public void setInstrument_date(String instrument_date) {
		this.instrument_date = instrument_date;
	}
	public String getInstrument_date_of_receving() {
		return instrument_date_of_receving;
	}
	public void setInstrument_date_of_receving(String instrument_date_of_receving) {
		this.instrument_date_of_receving = instrument_date_of_receving;
	}
	public String getInstrument_image_url() {
		return instrument_image_url;
	}
	public void setInstrument_image_url(String instrument_image_url) {
		this.instrument_image_url = instrument_image_url;
	}
	public String getInstrument_bank_account_number() {
		return instrument_bank_account_number;
	}
	public void setInstrument_bank_account_number(String instrument_bank_account_number) {
		this.instrument_bank_account_number = instrument_bank_account_number;
	}
	public String getInstrument_micr_no() {
		return instrument_micr_no;
	}
	public void setInstrument_micr_no(String instrument_micr_no) {
		this.instrument_micr_no = instrument_micr_no;
	}
	public String getInstrument_payee_name() {
		return instrument_payee_name;
	}
	public void setInstrument_payee_name(String instrument_payee_name) {
		this.instrument_payee_name = instrument_payee_name;
	}
	public String getInstrumentid() {
		return instrumentid;
	}
	public void setInstrumentid(String instrumentid) {
		this.instrumentid = instrumentid;
	}

	public String getGenId() {
		return genId;
	}

	public void setGenId(String genId) {
		this.genId = genId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
    
}
