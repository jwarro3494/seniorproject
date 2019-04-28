// Author: Jamie Arrowood-Forrester

package com.google.firebase.samples.apps.mlkit.java.facedetection;

import android.util.Log;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;

/** Class for calculating the slopes, y-intercepts, line lengths
 * and the angles of each line
 */

public class FaceCalculations {

    private static FirebaseVisionPoint leftEye, rightEye;
    private static FirebaseVisionPoint noseBridgePoint, middleBottomPoint;
    private static FirebaseVisionPoint face2Middle, face3Middle;

    private static float upperSlope;
    private static float lowerSlope;
    private static float upperYIntercept;
    private static float lowerYIntercept;

    private static FirebaseVisionPoint lineIntersectionPoint;

    private static double topAngle;
    private static double intersectionAngle;
    private static double bottomAngle;

    private static double maxValue = Double.MIN_VALUE;
    private static double minValue = Double.MAX_VALUE;

    // Default constructor for inserting data in the database
    public FaceCalculations() {

    }

    FaceCalculations(FirebaseVisionPoint leftEye,
                     FirebaseVisionPoint rightEye,
                     FirebaseVisionPoint noseBridgePoint,
                     FirebaseVisionPoint middleBottomPoint,
                     FirebaseVisionPoint face2Middle,
                     FirebaseVisionPoint face3Middle) {

        FaceCalculations.leftEye = leftEye;
        FaceCalculations.rightEye = rightEye;
        FaceCalculations.noseBridgePoint = noseBridgePoint;
        FaceCalculations.middleBottomPoint = middleBottomPoint;
        FaceCalculations.face2Middle = face2Middle;
        FaceCalculations.face3Middle = face3Middle;
    }

    void calculateUpperSlope() {
        float y = (leftEye.getY() - rightEye.getY());
        float x = (leftEye.getX() - rightEye.getX());
        float slope = y/x;
        upperSlope = slope;
        Log.d("CalculateSlope", "Slope of Face 1: " + upperSlope);
        calculateUpperYIntercept(slope);
    }

    private void calculateUpperYIntercept(float slope) {
        float y = leftEye.getY();
        float x = (slope * leftEye.getX());
        float b = y-x;
        // y = slope*(x) + b  find b.
        upperYIntercept = b;
        Log.d("CalculateSlope", "Upper Y intercept: " + b);
        Log.d("CalculateSlope", "Upper slope intercept form: y = " +
                slope + "x + " + b);
        calculateLowerSlope();
    }

    private void calculateLowerSlope() {
        float y = (face2Middle.getY() - face3Middle.getY());
        float x = (face2Middle.getX() - face3Middle.getX());
        float slope = y/x;
        lowerSlope = slope;
        Log.d("CalculateSlope", "Slope of Face 2 and 3: " + lowerSlope);
        calculateLowerYIntercept(slope);
    }

    private void calculateLowerYIntercept(float slope) {
        float y = face2Middle.getY();
        float x = (slope * face2Middle.getX());
        float b = y-x;
        lowerYIntercept = b;
        Log.d("CalculateSlope", "Lower Y intercept: " + b);
        Log.d("CalculateSlope", "Lower slope intercept form: y = " +
                slope + "x + " + b);
        setIntersectionPoint();
    }

    private void setIntersectionPoint() {
        float x;
        float y;
        x = (lowerYIntercept - upperYIntercept) / (upperSlope - lowerSlope);
        y = (upperSlope * x) + upperYIntercept;
        float yValue2 = (lowerSlope * x) + lowerYIntercept;
        Log.d("CalculateSlope", "(X , Y) =  (" + x + "," + y + ")");
        Log.d("CalculateSlope", "(X , Y) =  (" + x + "," + yValue2 + ")");
        lineIntersectionPoint = new FirebaseVisionPoint(x, y, 0f);
        setLineIntersectionPoint(lineIntersectionPoint);
        triangleSideCalculations();
    }

    private void triangleSideCalculations() {
        double upperX = (lineIntersectionPoint.getX() - noseBridgePoint.getX());
        double upperY = (lineIntersectionPoint.getY() - noseBridgePoint.getY());
        double bottomX = (lineIntersectionPoint.getX() - middleBottomPoint.getX());
        double bottomY = (lineIntersectionPoint.getY() - middleBottomPoint.getY());
        double middleX = (middleBottomPoint.getX() - noseBridgePoint.getX());
        double middleY = (middleBottomPoint.getY() - noseBridgePoint.getY());

        double noseToIntersectionDistance = Math.sqrt(Math.pow(upperX, 2.0) +
                Math.pow(upperY, 2.0));
        double noseToBottomDistance = Math.sqrt(Math.pow(middleX, 2.0) +
                Math.pow(middleY, 2.0));
        double lineToLowerEyesDistance = Math.sqrt(Math.pow(bottomX, 2.0) +
                Math.pow(bottomY, 2.0));
        angleCalculations(lineToLowerEyesDistance,
                noseToBottomDistance, noseToIntersectionDistance);
    }

    private void angleCalculations(Double lineToLowerEyesDistance,
                                   Double noseToBottomDistance,
                                   Double noseToIntersectionDistance) {
        double bottomAngle;
        double intersectionAngle;
        double topAngle;

        double upperFace = Math.pow(noseToIntersectionDistance, 2.0);
        double middleLine = Math.pow(noseToBottomDistance, 2.0);
        double lowerFaces = Math.pow(lineToLowerEyesDistance, 2.0);
        double innerAngleSum = Math.toRadians(180.00);

        topAngle = Math.acos((middleLine + lowerFaces -
                upperFace) /
                (2 * noseToBottomDistance *
                        noseToIntersectionDistance));
        intersectionAngle = Math.acos((upperFace + lowerFaces -
                middleLine) /
                (2 * lineToLowerEyesDistance *
                        noseToIntersectionDistance));
        bottomAngle = ((innerAngleSum - topAngle) - intersectionAngle);


        convertAnglesToDegrees(topAngle, intersectionAngle, bottomAngle);

    }

    private void convertAnglesToDegrees(double top, double intersection,
                                        double bottom) {
        topAngle = Math.toDegrees(top);
        intersectionAngle = Math.toDegrees(intersection);
        bottomAngle = Math.toDegrees(bottom);
        setTopAngle(topAngle);
        setIntersectionAngle(intersectionAngle);
        setBottomAngle(bottomAngle);
    }

    public double resetMaxIntersectionAngle() {
        return maxValue = Double.MIN_VALUE;
    }

    public double resetMinIntersectionAngle() {
        return minValue = Double.MAX_VALUE;
    }

    public double updateMaxIntersectionAngle() {
        double absoluteMax = 70.00;
        double previousMax = maxValue;
        if (intersectionAngle > previousMax && intersectionAngle < absoluteMax
                && intersectionAngle != 0.0) {
            maxValue = intersectionAngle;
        }
        return maxValue;
    }

    public double getMinIntersectionAngle() {
        double absoluteMin = 0.01;
        double previousMin = minValue;
        if (previousMin > intersectionAngle && intersectionAngle > absoluteMin) {
            minValue = intersectionAngle;
        }
        return minValue;
    }

    public double getAverageValue() {
        return (maxValue + minValue) / 2.0;
    }

    FirebaseVisionPoint getLineIntersectionPoint() {
        return lineIntersectionPoint;
    }

    private void setLineIntersectionPoint(FirebaseVisionPoint lineIntersectionPoint) {
        FaceCalculations.lineIntersectionPoint = lineIntersectionPoint;
    }

    double getTopAngle() {
        return topAngle;
    }

    private static void setTopAngle(double topAngle) {
        FaceCalculations.topAngle = topAngle;
    }

    double getIntersectionAngle() {
        return intersectionAngle;
    }

    private static void setIntersectionAngle(double intersectionAngle) {
        FaceCalculations.intersectionAngle = intersectionAngle;
    }

    double getBottomAngle() {
        return bottomAngle;
    }

    private static void setBottomAngle(double bottomAngle) {
        FaceCalculations.bottomAngle = bottomAngle;
    }

}
