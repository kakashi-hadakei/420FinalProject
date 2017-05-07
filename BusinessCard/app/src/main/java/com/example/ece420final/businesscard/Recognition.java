package com.example.ece420final.businesscard;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by hanfei on 4/15/17.
 * Recogniton Module after detection
 */

class Recognition  {
    private static final String TAG = "RecognitionActivity";
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString()+"/";
    public static final String lang = "eng";

    private TessBaseAPI tessBaseApi;
    private ArrayList<Bitmap> receiveSubImg;
    protected ArrayList<ContactInfo> info;
    private String recognized;
    private Context myContext;

    public Recognition(Context context){
        myContext = context;
        initialize();
    }


    public void recognize(){
        for(int i = 0;i < receiveSubImg.size();i++){
            recognized = extractText(receiveSubImg.get(i));
            String phoneNumber = keepNumbers(recognized);

            if(i == 0){
                Log.d(TAG,"NAME "+ recognized);
                info.add(new ContactInfo("NAME",recognized));
            }

            if(recognized.indexOf("@") != -1){
                int space = recognized.indexOf(" ");
                int dot = recognized.lastIndexOf(".");
                if(space != -1){
                    info.add(new ContactInfo("EMAIL",recognized.substring(space+1,dot+4)));
                    Log.d(TAG,"EMAIL "+recognized.substring(space));
                }
                else{
                    info.add(new ContactInfo("EMAIL",recognized.substring(0,dot+4)));
                    Log.d(TAG,"EMAIL "+recognized);
                }
            }

            if(!phoneNumber.equals(recognized)){
                Log.d(TAG,"PHONE NUMBER " + phoneNumber);
                info.add(new ContactInfo("PHONENUMBER",phoneNumber));
            }

        }

        tessBaseApi.end();
    }

    private void initialize() {
        info = new ArrayList<ContactInfo>();
        receiveSubImg = DetectionRecognitionActivity.mySubImg;
        loadData();

        try{
            initTessBase();
        }
        catch(Exception e){
            Log.d(TAG,"tesseBaseApi init failed "+e.getMessage());
        }
    }

    private void initTessBase() throws Exception{
        tessBaseApi = new TessBaseAPI();
        tessBaseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_CHAR);
        tessBaseApi.init(DATA_PATH, "eng");
    }

    private void loadData(){
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {
                AssetManager assetManager = myContext.getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }
    }

    private String extractText(Bitmap bitmap) {
        tessBaseApi.setImage(bitmap);
        String extractedText = tessBaseApi.getUTF8Text();
        return extractedText;
    }

    private String keepNumbers(String input){
        char currentChar;
        String numbers = "";

        int i = 0;
        while(i < input.length() && numbers.length() < 10){
            currentChar = input.charAt(i);
            if(Character.isDigit(currentChar)){
                numbers += currentChar;
            }
            i++;
        }

        if(numbers.length() >= 10)
            return numbers.substring(0,10);
        else
            return input;
    }

}
