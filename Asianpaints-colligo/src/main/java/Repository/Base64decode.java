package Repository;

import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_FLAG;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Services.Base64;
import Utilres.Jsonresponse;
import Utilres.ValidationCtrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
//import sun.misc.BASE64Decoder;

public class Base64decode implements Base64 {
    private Jsonresponse jsonValit = new Jsonresponse();
    private ValidationCtrl validationlog = new ValidationCtrl();
    Random random = new Random();   
    @Override
    public Object getLoadDataBase64image(JsonObject data, String devimagepath) {
        // TODO Auto-generated method stub
        byte[] imageByte;
        BufferedImage image = null;
        String imageString = data.get("imageData").getAsString();
        String userid = data.get("userid").getAsString();
        String extension = data.get("extension").getAsString().toLowerCase();
//        BASE64Decoder decoder = new BASE64Decoder();
        try {
            int randonNumber = random.nextInt(1000);
//          imageByte = decoder.decodeBuffer(imageString);
            imageByte =  java.util.Base64.getDecoder().decode(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
            String fileName = "captureImage_"+randonNumber;   
            String fileImagepath = "";
            File outputfile = null;
            if(extension.equals("pdf")) {
        	fileImagepath = devimagepath+fileName+"."+extension;
            outputfile = new File(fileImagepath);
            FileOutputStream fop = new FileOutputStream(outputfile);
            fop.write(imageByte);
            fop.flush();
            fop.close();
            }
            /** Compressing the Image **/
            if(extension.equals("heic")) {
            	fileImagepath = devimagepath+fileName+"."+extension;
                outputfile = new File(fileImagepath);
            	FileOutputStream heic = new FileOutputStream(outputfile);
            	heic.write(imageByte);
            	heic.flush();
            	heic.close();
            }
            if(extension.equals("heif")) {
            	fileImagepath = devimagepath+fileName+"."+extension;
                outputfile = new File(fileImagepath);
            	FileOutputStream heic = new FileOutputStream(outputfile);
            	heic.write(imageByte);
            	heic.flush();
            	heic.close();
            }
            /** Closed Compressing the Image **/
            /** Compressing the Image **/
            
//            if(extension.equals("png")) {
//            	fileImagepath = devimagepath+fileName+"."+"jpg";
//                outputfile = new File(fileImagepath);
//                ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage("jpg");
//                ImageWriter writer = ImageIO.getImageWriters(type, "png").next();
////            	ImageIO.write(image, "png", outputfile);
//                ImageOutputStream out = ImageIO.createImageOutputStream(Files.newOutputStream(Paths.get(fileImagepath)));
//                ImageWriteParam param = writer.getDefaultWriteParam();
//                if (param.canWriteCompressed()) {
//                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//                    param.setCompressionQuality(0.4f);
//                }
//                writer.setOutput(out);
//                writer.write(null, new IIOImage(image, null, null), param);
//                writer.dispose();
//                
//            }
            
            if( extension.equals("jpeg") || extension.equals("jpg") || extension.equals("png") ) {
            	fileImagepath = devimagepath+fileName+"."+"jpg";
                outputfile = new File(fileImagepath);
            	OutputStream os = new FileOutputStream(outputfile);
                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
                if(writers.hasNext()) {
                	ImageWriter writer = (ImageWriter) writers.next();
                	ImageOutputStream ios = ImageIO.createImageOutputStream(os);
                	writer.setOutput(ios);
                	ImageWriteParam param = writer.getDefaultWriteParam();
                	param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                	param.setCompressionQuality(0.85f);  // Change the quality value you prefer
                	writer.write(null, new IIOImage(image, null, null), param);
                	ios.close();
                	writer.dispose();
                }
                os.close();	
            }
            /** Compressing the Image **/
            Boolean ImageCapture = true; //ImageIO.write(image, "png", outputfile);
            JSONObject responseMap = new JSONObject();
            if(ImageCapture.equals(true)) {
                String randomFolder = userid+"_"+randonNumber;
                String createdAting = jsonValit.getStartDate(new Date());
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                System.out.println("camera_capture/"+randomFolder+"/"+createdAting+"/"+fileName);
                System.out.println(fileImagepath);
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                  .addFormDataPart("fileData",fileImagepath,RequestBody.create(MediaType.parse("application/octet-stream"),outputfile))
                  .addFormDataPart("fileContainer","aplms")
                  .addFormDataPart("fileLoc","camera_capture/"+randomFolder+"/"+createdAting+"/"+fileName)
                  .build();
                System.out.println(body.toString());
                Request requestcfr = new Request.Builder()
                        .url("https://api.asianpaints.com/v1/contentstorage?apikey=jJs5QR9LY5YJcMej3TjnMdXDZ8Air1Zz")
                        .method("POST", body)
                        .build();
                Response responsebht = client.newCall(requestcfr).execute();
                String jsonData = responsebht.body().string();
                System.out.println(jsonData);
                JsonObject dataResponse = new Gson().fromJson(jsonData.toString(), JsonObject.class);
                if(jsonData.toString().length() > 0) {
                    outputfile.delete();
                }
                responseMap.put(STATUS_MESSAGE, SUCCESS);
                responseMap.put("imageUrl", dataResponse.get("assetUrl").getAsString());
                responseMap.put(STATUS_CODE, SUCCESS_CODE);
                responseMap.put(STATUS_FLAG, 0);
                return responseMap;
            } else {
                return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
            }
            
        } catch (IOException | JSONException e) {
            // TODO Auto-generated catch block
        	System.out.println(e.getMessage().toString());
            e.printStackTrace();
            return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
        }
    }

}
