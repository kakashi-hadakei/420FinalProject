package com.example.ece420final.businesscard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.util.Log;
import android.provider.MediaStore;
import android.provider.ContactsContract.Intents;
import android.provider.ContactsContract;



import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import android.net.Uri;

/**
 * Created by hanfei on 4/10/17.
 * process received taken Image according to
 * the pre-tested python script
 *
 * detecting texts from the input image;
 */

public class DetectionRecognitionActivity extends AppCompatActivity  {
    private static final String TAG = "DRActivity";
    private ImageView myImg;
    private Button myRecognitionButton;
    private String receivedImgPath;
    protected static ArrayList<Bitmap> mySubImg;
    private Uri cropped ;
    private Bitmap processed;
    private Bitmap myBitmap;
    private static int numEmails = 0;
    private static int numPhone = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        Bundle extras = getIntent().getExtras();
        receivedImgPath = extras.getString("imgFilePathDetect");
        /*if(receivedImgPath != null){
            Log.d(TAG,receivedImgPath);
        }*/
        cropped = Uri.parse(extras.getString("CroppedUri"));
        /*if(cropped != null){
            Log.d(TAG,"received Uri "+cropped.toString());
        }*/

        mySubImg = new ArrayList<Bitmap>();
        myImg = (ImageView)findViewById(R.id.imageView2);
        processed = getProcessedBitmap(receivedImgPath);
        myImg.setImageBitmap(processed);


        myRecognitionButton = (Button)findViewById(R.id.buttonRecognition);
        myRecognitionButton.setText("Create Contact");
        myRecognitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recognition recognizer = new Recognition(getCurrentActivity());
                recognizer.recognize();
                ArrayList<ContactInfo> info = recognizer.info;

                // Creates a new Intent to insert a contact
                Intent intent = new Intent(Intents.Insert.ACTION);
                // Sets the MIME type to match the Contacts Provider
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                for(int i = 0;i < info.size();i++){
                    ContactInfo in = info.get(i);
                    //Log.d(TAG,in.toString());
                    String content = in.getMyContent();
                    String title = in.getMyTitle();
                    if(title.equals("NAME")){
                        intent.putExtra(Intents.Insert.NAME,content);
                    }

                    else if(title.equals("PHONENUMBER") && numPhone == 0){
                        intent.putExtra(Intents.Insert.PHONE,content);
                        numPhone++;
                    }

                    else if(title.equals("EMAIL") && numEmails == 0){
                        intent.putExtra(Intents.Insert.EMAIL,content);
                        numEmails++;
                    }

                }

                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        processed.recycle();
        myBitmap.recycle();
        for(int i = 0;i < mySubImg.size();i++){
            mySubImg.get(i).recycle();
        }


    }

    @Override
    protected void onResume(){
        Log.d(TAG,"RESUMING ");
        super.onResume();
        numPhone = 0;
        numEmails = 0;

    }

    @Override
    protected void onPause(){
        Log.d(TAG,"PAUSING");
        super.onPause();


    }

    private Activity getCurrentActivity(){
        return DetectionRecognitionActivity.this;
    }

    private Bitmap getProcessedBitmap(String imgPath){
        if(imgPath != null) {
            try{
                myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),cropped);
                 /*
                gray = cv2.cvtColor(image,cv2.COLOR_BGR2GRAY) # grayscale
                _,thresh = cv2.threshold(gray,150,255,cv2.THRESH_BINARY_INV) # threshold
                kernel = cv2.getStructuringElement(cv2.MORPH_CROSS,(6,1))
                dilated = cv2.dilate(thresh,kernel,iterations = 10) # dilate
                img,contours,hierarchy = cv2.findContours(dilated,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_NONE) # get contours
                */

                //dilation and detection;
                Mat imgMat = new Mat();
                Mat imgOriginal = new Mat();
                Utils.bitmapToMat(myBitmap,imgMat);
                Utils.bitmapToMat(myBitmap,imgOriginal);
                Bitmap bmpOut = Bitmap.createBitmap(imgMat.cols(),imgMat.rows(),Bitmap.Config.ARGB_8888);
                Imgproc.cvtColor(imgMat,imgMat,Imgproc.COLOR_BGR2GRAY);//to gray scale
                Imgproc.threshold(imgMat,imgMat,100,255,Imgproc.THRESH_BINARY_INV);

                Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS,new Size(6,1));
                dilate(imgMat,imgMat,kernel,10);

                List<MatOfPoint> contours = new ArrayList<>();
                Mat hierarchy = new Mat();
                Imgproc.findContours(imgMat,contours,hierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
                Collections.sort(contours,new SortByY());
                //draw contours
                for(int i = 0;i < contours.size();i++){
                    MatOfPoint matOfPoint = contours.get(i);
                    Rect rect = Imgproc.boundingRect(matOfPoint);

                /*
                * corresponding python script
                * # draw rectangle around contour on original image
                * cv2.rectangle(image,(x,y),(x+w,y+h),(255,0,255),2)
                */

                    if(rect.height > 100 && rect.width > 300){continue;}
                    if(rect.height <30 || rect.width < 30){continue;}
                    Imgproc.rectangle(imgOriginal, new Point(rect.x,rect.y),
                            new Point(rect.x+rect.width,rect.y+rect.height),
                            new Scalar(255,0,255),
                            2);
                    Bitmap subMap = Bitmap.createBitmap(myBitmap,rect.x,rect.y,rect.width,rect.height);
                    mySubImg.add(subMap);
                }
                Utils.matToBitmap(imgOriginal,bmpOut);
                return bmpOut;

            }catch(Exception e){
                Log.d(TAG,e.getMessage());
            }

        }
        return null;
    }

    private void dilate(Mat src,Mat dst,Mat kernel,int iterations){
        for(int i = 0;i < iterations;i++){
            Imgproc.dilate(src,dst,kernel);
        }
    }



}
