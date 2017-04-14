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
import android.graphics.*;



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

    private Bitmap getProcessedBitmap(String imgPath){
        if(imgPath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgPath);
            //~/420FinalProject/test.py
            //myBitmap = adjustedContrast(myBitmap,1);




             /*gray = cv2.cvtColor(image,cv2.COLOR_BGR2GRAY) # grayscale
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

            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS,new Size(1,6));
            dilate(imgMat,imgMat,kernel,10);

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(imgMat,contours,hierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);


            //draw contours
            for(int i = 0;i < contours.size();i++){
                MatOfPoint matOfPoint = contours.get(i);


                Rect rect = Imgproc.boundingRect(matOfPoint);

                //Log.d(TAG,Arrays.toString(contours.get(i).toArray()));

                /*
                * corresponding python script
                * # draw rectangle around contour on original image
                * cv2.rectangle(image,(x,y),(x+w,y+h),(255,0,255),2)
                */

                if(rect.height > 500 && rect.width > 500){continue;}
                if(rect.height <10 || rect.width < 10){continue;}
                Imgproc.rectangle(imgOriginal,
                        new Point(rect.x,rect.y),
                        new Point(rect.x+rect.width,rect.y+rect.height),
                        new Scalar(255,0,255),
                        2);
                Imgproc.resize(imgOriginal,imgOriginal,imgOriginal.size(),7,7,Imgproc.INTER_CUBIC);

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

    private Bitmap adjustedContrast(Bitmap src, double value)
    {

        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap


        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.green(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.blue(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }

}
