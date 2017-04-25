package com.example.ece420final.businesscard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;
import android.net.Uri;
import android.util.Log;

import android.content.Intent;
import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * Created by hanfei on 4/23/17.
 */

public class CropActivity extends AppCompatActivity {
    private String imageUri;
    private CropImageView myImg;
    private Uri resultUri;

    private static final String TAG = "CropActivity";
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropping);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        Bundle extras = getIntent().getExtras();
        imageUri = extras.getString("cropping image");
        if(imageUri != null){
            Log.d(TAG,imageUri);
        }
        CropImage.activity(Uri.parse(imageUri)).start(this);
        //CropImage.activity(Uri.parse(imageUri)).start
        //CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);

        myImg = (CropImageView) findViewById(R.id.cropImageView);
        myImg.setImageUriAsync(resultUri);

        Log.d(TAG,"result uri"+resultUri);

        //myImg.getCroppedImageAsync();
        Bitmap cropped = myImg.getCroppedImage();
        myImg.setImageBitmap(cropped);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d(TAG,"REQUESTCODE MATCHES");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Log.d(TAG,result.toString());
            if (resultCode == RESULT_OK) {
                Log.d(TAG,"RESULTCODE IS OK");
                resultUri = result.getUri();
                if(resultUri != null){
                    Log.d(TAG,"ji" + resultUri.toString());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
