package com.example.ece420final.businesscard;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.Comparator;

/**
 * Created by hanfei on 4/27/17.
 * Comparator for sorting the bounding box
 * coordinates by increasing y values
 */

public class SortByY implements Comparator<MatOfPoint> {
        public int compare(MatOfPoint a,MatOfPoint b){
            Rect rectA = Imgproc.boundingRect(a);
            Rect rectB = Imgproc.boundingRect(b);
            return rectA.y-rectB.y;
        }
}
