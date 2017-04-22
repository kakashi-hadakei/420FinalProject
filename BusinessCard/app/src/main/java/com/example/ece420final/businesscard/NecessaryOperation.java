package com.example.ece420final.businesscard;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by hanfei on 4/15/17.
 * methods that one or more activities share
 * for proper function of the application
 */

class NecessaryOperation {
    protected static Bitmap rotateBitmap(Bitmap myBitmap, Bitmap bmpOut) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bmpOut, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
    }
}
