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
 * background Task for recognition
 */

public class RecognitionTask extends AsyncTask {
    private static final String TAG = "RecognitionActivity";
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString()+"/";
    public static final String lang = "eng";
    private TessBaseAPI tessBaseApi;
    private ArrayList<Bitmap> receiveSubImg;
    private Context context;
    private boolean isTesseractEnd;

    public RecognitionTask(Context myContext){
        this.context = myContext;
    }

    @Override
    protected ArrayList<String> doInBackground(Object[] params) {
        ArrayList<String> recognized = new ArrayList<String>();
        String subImgMessage;
        for(int i = 0;i < receiveSubImg.size();i++){
            subImgMessage = extractText(NecessaryOperation.rotateBitmap(receiveSubImg.get(i),receiveSubImg.get(i)));
            recognized.add(subImgMessage);
        }

        return recognized;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        loadData();
        try{
            initTessBase();
            isTesseractEnd = true;
        }
        catch(Exception e){
            Log.d(TAG,"tesseBaseApi init failed "+e.getMessage());
        }
        receiveSubImg = DetectionActivity.mySubImg;

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if(!isTesseractEnd)
            tessBaseApi.end();
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Object o) {
        super.onCancelled(o);
        if(!isTesseractEnd)
            tessBaseApi.end();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(!isTesseractEnd)
            tessBaseApi.end();
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
                //while ((lenf = gin.read(buff)) > 0) {
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

    private String extractText(Bitmap bitmap)
    {
        //bitmap resize;
        //Bitmap resized = Bitmap.createScaledBitmap(bitmap,5*bitmap.getWidth(),5*bitmap.getHeight(),false);
        //tessBaseApi.setImage(resized);
        tessBaseApi.setImage(bitmap);
        String extractedText = tessBaseApi.getUTF8Text();
        //resized.recycle();
        Log.d(TAG,"extract Text:\t"+extractedText);
        return extractedText;
    }
}
