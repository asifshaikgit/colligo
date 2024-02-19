package Constants;

import java.util.HashMap;
import java.util.Map;

public class Languageslist {
	
	public Map<String, String> getloadLanguage(String lang){
		if(lang.equals("en")) {
			Map<String, String> langListEn = new HashMap<>();
			langListEn.put("address","Address");
			langListEn.put("phone","Phone");
			langListEn.put("fax","Fax");
			langListEn.put("email","E-mail");
			langListEn.put("crno","C.R.No");
			langListEn.put("receiptNumber","Receipt No");
			langListEn.put("date", "Date");
			langListEn.put("customercode", "Customer Code");
			langListEn.put("customername", "Customer Name");
			langListEn.put("paymentmode", "Payment Mode");
			langListEn.put("finalreceiptamount", "Final Receipt Amount");
			langListEn.put("issuedby","Issued by");
			langListEn.put("receiptremarks","Receipt Remarks");
			langListEn.put("disclimer","Disclaimer");
			langListEn.put("customerrepresentative","Customer Representative");
			
			langListEn.put("no","No");
			langListEn.put("instrumenttype","Instrument Type");
			langListEn.put("instrumentno","Instrument NO");
			langListEn.put("amount","AMOUNT");
			langListEn.put("micr","MICR");
			langListEn.put("bankname","Bank Name");
			langListEn.put("total","Total");
			langListEn.put("instrumentremarks","Instrument Remarks");
			return langListEn;
		} else if(lang.equals("ar")) {
			Map<String, String> langListUrdu = new HashMap<>();
			langListUrdu.put("address","عنوان");
			langListUrdu.put("phone","هاتف");
			langListUrdu.put("fax","فاكس");
			langListUrdu.put("email","بريد إلكتروني");
			langListUrdu.put("crno","C.R.No");
			langListUrdu.put("receiptNumber","رقم الإيصال");
			langListUrdu.put("date", "تاريخ");
			langListUrdu.put("customercode", "كود العميل");
			langListUrdu.put("customername", "اسم الزبون");
			langListUrdu.put("paymentmode", "طريقة الدفع");
			langListUrdu.put("finalreceiptamount", "مبلغ الاستلام النهائي");
			langListUrdu.put("issuedby","أصدرت من قبل");
			langListUrdu.put("receiptremarks","ملاحظات الاستلام");
			langListUrdu.put("disclimer","تنصل");
			langListUrdu.put("customerrepresentative","ممثل العملاء");
			
			langListUrdu.put("no","لا");
			langListUrdu.put("instrumenttype","نوع الصك");
			langListUrdu.put("instrumentno","الصك لا");
			langListUrdu.put("amount","كمية");
			langListUrdu.put("micr","MICR");
			langListUrdu.put("bankname","اسم البنك");
			langListUrdu.put("total", "المجموع");
			langListUrdu.put("instrumentremarks","ملاحظات الصك");
			return langListUrdu;
		} else if(lang.equals("id")) {
			Map<String, String> langListbahasa = new HashMap<>();
			langListbahasa.put("address","Alamat");
			langListbahasa.put("phone","Telepon");
			langListbahasa.put("fax","Fax");
			langListbahasa.put("email","Surel");
			langListbahasa.put("crno","C.R.No");
			langListbahasa.put("receiptNumber","No Tanda Terima");
			langListbahasa.put("date", "Tanggal");
			langListbahasa.put("customercode", "Kode Pelanggan");
			langListbahasa.put("customername", "Nama Pelanggan");
			langListbahasa.put("paymentmode", "Mode Pembayaran");
			langListbahasa.put("finalreceiptamount", "Jumlah Penerimaan Akhir");
			langListbahasa.put("issuedby","Dikeluarkan oleh");
			langListbahasa.put("receiptremarks","Keterangan Tanda Terima");
			langListbahasa.put("disclimer","disclimer");
			langListbahasa.put("customerrepresentative","Perwakilan Pelanggan");
			
			langListbahasa.put("no","tidak");
			langListbahasa.put("instrumenttype","Jenis Instrumen");
			langListbahasa.put("instrumentno","No Instrumen");
			langListbahasa.put("amount","Jumlah");
			langListbahasa.put("micr","MICR");
			langListbahasa.put("total","Total");
			langListbahasa.put("bankname","Nama Bank");
			langListbahasa.put("instrumentremarks","Keterangan Instrumen");
			return langListbahasa;
		} else {
			return null;
		}
	}
	
}
