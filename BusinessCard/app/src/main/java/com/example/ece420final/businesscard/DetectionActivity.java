package com.example.ece420final.businesscard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.util.Log;
import java.util.Arrays;
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


/**
 * Created by hanfei on 4/10/17.
 * process received taken Image according to
 * the pre-tested python script
 */

public class DetectionActivity extends AppCompatActivity  {

    private static final String TAG = "DetectionActivity";
    private ImageView myImg;
    private Button myRecognitionButton;
    private String receivedImgPath;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        Bundle extras = getIntent().getExtras();
        receivedImgPath = extras.getString("imgFilePathDetect");
        if(receivedImgPath != null){
            Log.d(TAG,receivedImgPath);
        }

        myImg = (ImageView)findViewById(R.id.imageView2);
        myImg.setImageBitmap(getProcessedBitmap(receivedImgPath));

        myRecognitionButton = (Button)findViewById(R.id.buttonRecognition);
        myRecognitionButton.setText("Now Let's Recognize!!");
        myRecognitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /*gray = cv2.cvtColor(image,cv2.COLOR_BGR2GRAY) # grayscale
    _,thresh = cv2.threshold(gray,150,255,cv2.THRESH_BINARY_INV) # threshold
    kernel = cv2.getStructuringElement(cv2.MORPH_CROSS,(6,1))
    dilated = cv2.dilate(thresh,kernel,iterations = 10) # dilate

    img,contours,hierarchy = cv2.findContours(dilated,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_NONE) # get contours
    */

    private Bitmap getProcessedBitmap(String imgPath){
        if(imgPath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgPath);

            Mat imgMat = new Mat();
            Mat imgOriginal = new Mat();
            Utils.bitmapToMat(myBitmap,imgMat);
            Utils.bitmapToMat(myBitmap,imgOriginal);
            Bitmap bmpOut = Bitmap.createBitmap(imgMat.cols(),imgMat.rows(),Bitmap.Config.ARGB_8888);
            Imgproc.cvtColor(imgMat,imgMat,Imgproc.COLOR_BGR2GRAY);//to gray scale
            Imgproc.threshold(imgMat,imgMat,150,255,Imgproc.THRESH_BINARY_INV);

            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS,new Size(1,6));
            dilate(imgMat,imgMat,kernel,15);

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(imgMat,contours,hierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);

            //draw contours
            for(int i = 0;i < contours.size();i++){
                MatOfPoint matOfPoint = contours.get(i);

                Rect rect = Imgproc.boundingRect(matOfPoint);

                //Log.d(TAG,Arrays.toString(contours.get(i).toArray()));

                /*
                * # draw rectangle around contour on original image
                 cv2.rectangle(image,(x,y),(x+w,y+h),(255,0,255),2)
                  */
                //Imgproc.rectangle(imgMat,new Point());
                if(rect.height > 500 && rect.width > 500){continue;}
                if(rect.height <30 || rect.width < 30){continue;}
                Imgproc.rectangle(imgOriginal,
                        new Point(rect.x,rect.y),
                        new Point(rect.x+rect.width,rect.y+rect.height),
                        new Scalar(255,0,255),
                        3);

            }

            Utils.matToBitmap(imgOriginal,bmpOut);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bmpOut, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);


            return rotatedBitmap;
        }
        return null;
    }

    private void dilate(Mat src,Mat dst,Mat kernel,int iterations){
        for(int i = 0;i < iterations;i++){
            Imgproc.dilate(src,dst,kernel);
        }
    }

}
