package com.example.ece420final.businesscard;



//test
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import android.graphics.Matrix;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import android.graphics.Bitmap;



import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private Button captureButton;
    private Button detectButton;
    private Camera camera;
    private File imgFile;
    private String imgFilePath;
    private String imgFilePathDetect;
    private ImageView imageView;

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        captureButton = (Button)findViewById(R.id.buttonCapture);
        captureButton.setText("Get Image");
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),timeStamp+".jpg");
                Uri imgUri = Uri.fromFile(imgFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
                cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
                if (cameraIntent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(cameraIntent,REQUEST_CODE);
                    if(camera != null){
                        camera.release();
                        camera = null;
                    }
                }
            }
        });

        detectButton = (Button)findViewById(R.id.buttonRecognition);
        detectButton.setText("Find Text");
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass in String

                Intent detectIntent = new Intent(getMainActivity(),DetectionActivity.class);
                detectIntent.putExtra("imgFilePathDetect",imgFilePathDetect);
                startActivity(detectIntent);
            }
        });



    }

    private Activity getMainActivity(){
        return MainActivity.this;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"Resuming");
        imageView =(ImageView) findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setMaxWidth(640);
        imageView.setMaxHeight(480);
        if(imgFilePath != null){
            Log.d(TAG,imgFilePath);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFilePath);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
            imageView.setImageBitmap(rotatedBitmap);
            imgFilePath = null;
        }

    }

    @Override
    protected void onPause() {
        Log.w(TAG, "App paused");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.w(TAG, "App destroyed");
        super.onDestroy();
    }

    protected void onActivityResult(int request,int result,Intent data){
        if(request == REQUEST_CODE){
            switch (result){
                case Activity.RESULT_OK:
                    if(imgFile.exists()){
                        Toast.makeText(this, "The file was saved at"+imgFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        imgFilePath = imgFile.getAbsolutePath();
                        imgFilePathDetect = imgFile.getAbsolutePath();
                    }
                    else{
                        Toast.makeText(this, "Error occurred", Toast.LENGTH_LONG).show();
                    }
                    break;

                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
    }
}