package com.example.ece420final.businesscard;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
 * Created by hanfei on 4/27/17.
 * recognition module after detection
 * asyncTask version
 */

public class RecognitionTask
        extends AsyncTask<ArrayList<Bitmap>,Integer,ArrayList<ContactInfo>>{
    private Context context;
    private static final String TAG = "RecognitionActivity";
    private static final String DATA_PATH = Environment.getExternalStorageDirectory()
            .toString()+"/";
    public static final String lang = "eng";

    private TessBaseAPI tessBaseApi;
    private boolean isTessInit = false;

    public RecognitionTask(Context myContext){
        context = myContext;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        loadData();

        try{
            initTessBase();
            isTessInit = true;
        }
        catch(Exception e){
            Log.d(TAG,"tesseBaseApi init failed "+e.getMessage());
        }

    }

    @Override
    protected ArrayList doInBackground(ArrayList<Bitmap>... myList){
        ArrayList<Bitmap> subImgList = myList[0];
        String recognized,phoneNumber;
        ArrayList<ContactInfo> recognizedInfo = new ArrayList<ContactInfo>();
        for(int i = 0;i < subImgList.size();i++){
            recognized = extractText(subImgList.get(i));
            phoneNumber = keepNumbers(recognized);
            if(i == 0){
                Log.d(TAG,"NAME "+ recognized);
                recognizedInfo.add(new ContactInfo("NAME",recognized));

            }

            if(recognized.indexOf("@") != -1){
                Log.d(TAG,"EMAIL "+recognized);
                recognizedInfo.add(new ContactInfo("EMAIL",recognized));
            }

            if(!phoneNumber.equals(recognized)){
                Log.d(TAG,"PHONE NUMBER " + phoneNumber);
                recognizedInfo.add(new ContactInfo("PHONENUMBER",phoneNumber));
            }

            publishProgress(i);
        }
        return recognizedInfo;
    }

    @Override
    protected void onProgressUpdate(Integer... values){
        super.onProgressUpdate(values);
        //progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<ContactInfo> infoList){
        super.onPostExecute(infoList);
        if(isTessInit){
            tessBaseApi.end();
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
                AssetManager assetManager = context.getAssets();
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
