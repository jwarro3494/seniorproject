// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified by Jamie Arrowood-Forrester for Dr. Tamirat Abegaz in
// compliance to the apache2 license above.

package com.google.firebase.samples.apps.mlkit.java.facedetection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.samples.apps.mlkit.common.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.common.GraphicOverlay.Graphic;

import java.util.Locale;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceGraphic extends Graphic {

  private static final float ID_TEXT_SIZE = 40.0f;
  private static final float ID_Y_OFFSET = 50.0f;
  private static final float ID_X_OFFSET = -50.0f;

  private static final int[] COLOR_CHOICES = {
          Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA,
          Color.RED, Color.WHITE, Color.YELLOW
  };
  private static int currentColorIndex = 0;

  private final Paint idPaint;

  private volatile FirebaseVisionFace firebaseVisionFace;
  private volatile FirebaseVisionFace firebaseVisionFace2;
  private volatile FirebaseVisionFace firebaseVisionFace3;

  private static FirebaseVisionPoint leftEye, leftEye2, leftEye3;
  private static FirebaseVisionPoint rightEye, rightEye2, rightEye3;
  private static FirebaseVisionPoint noseBridgePoint, middleBottomPoint;
  private static FirebaseVisionPoint face2Middle, face3Middle;

  private static final float TEXT_SIZE = 24.0f;

  private final Paint textPaint;

  private static FaceCalculations faceCalculations;

  public FaceGraphic(GraphicOverlay overlay) {
    super(overlay);

    currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.length;
    final int selectedColor = COLOR_CHOICES[currentColorIndex];

    Paint facePositionPaint = new Paint();
    facePositionPaint.setColor(selectedColor);

    idPaint = new Paint();
    idPaint.setColor(selectedColor);
    idPaint.setTextSize(ID_TEXT_SIZE);

    textPaint = new Paint();
    textPaint.setColor(Color.WHITE);
    textPaint.setTextSize(TEXT_SIZE);
    textPaint.setFakeBoldText(true);
  }

  /**
   * Updates the face instances from the detection of the most recent frame. Invalidates the relevant
   * portions of the overlay to trigger a redraw.
   */
  void updateFace(FirebaseVisionFace face, FirebaseVisionFace face2,
                  FirebaseVisionFace face3) {
    firebaseVisionFace = face;
    firebaseVisionFace2 = face2;
    firebaseVisionFace3 = face3;
    postInvalidate();
  }

  /**
   * Draws the face annotations for position on the supplied canvas.
   */
  @Override
  public void draw(Canvas canvas) {
    FirebaseVisionFace face = firebaseVisionFace;
    FirebaseVisionFace face2 = firebaseVisionFace2;
    FirebaseVisionFace face3 = firebaseVisionFace3;
    if (face == null) {
      return;
    }
    setFacePoints(canvas, face, face2, face3);
  }

  private void setFacePoints(Canvas canvas,
                             FirebaseVisionFace face, FirebaseVisionFace face2,
                             FirebaseVisionFace face3) {
    FirebaseVisionFaceLandmark landmarkLeftEye = face.getLandmark(4);
    FirebaseVisionFaceLandmark landmarkLeftEye2 = face2.getLandmark(4);
    FirebaseVisionFaceLandmark landmarkLeftEye3 = face3.getLandmark(4);
    FirebaseVisionFaceLandmark landmarkRightEye = face.getLandmark(10);
    FirebaseVisionFaceLandmark landmarkRightEye2 = face2.getLandmark(10);
    FirebaseVisionFaceLandmark landmarkRightEye3 = face3.getLandmark(10);

    if (landmarkLeftEye != null && landmarkRightEye != null &&
            landmarkLeftEye2 != null && landmarkRightEye2 != null &&
            landmarkLeftEye3 != null && landmarkRightEye3 != null) {

      leftEye = landmarkLeftEye.getPosition();
      rightEye = landmarkRightEye.getPosition();
      leftEye2 = landmarkLeftEye2.getPosition();
      rightEye2 = landmarkRightEye2.getPosition();
      leftEye3 = landmarkLeftEye3.getPosition();
      rightEye3 = landmarkRightEye3.getPosition();

      setMiddlePoints();

      if (middleBottomPoint.getY() > noseBridgePoint.getY()) {
        faceCalculations = new FaceCalculations(leftEye, rightEye, noseBridgePoint,
                middleBottomPoint, face2Middle, face3Middle);
        faceCalculations.calculateUpperSlope();
        drawTrianglePoints(canvas);
        drawTriangleLines(canvas);
        drawLowerConnectingLines(canvas);
        drawAngleCalculations(canvas);
        drawExtraCalculations(canvas);
      } else {
        canvas.drawText("Please press the start button again, ",
                translateX(20f), translateY(400f), textPaint);
        canvas.drawText("or move the device slightly.", translateX(20f),
                translateY(420f), textPaint);
      }
    }
  }

  private void setMiddlePoints() {
    float upperMiddleX = (leftEye.getX() + rightEye.getX()) / 2f;
    float upperMiddleY = (leftEye.getY() + rightEye.getY()) / 2f;
    noseBridgePoint = new FirebaseVisionPoint(upperMiddleX, upperMiddleY, 0f);

    float bottomMiddleX = (leftEye2.getX() + rightEye3.getX()) / 2f;
    float bottomMiddleY = (leftEye2.getY() + rightEye3.getY()) / 2f;
    middleBottomPoint = new FirebaseVisionPoint(bottomMiddleX, bottomMiddleY, 0f);

    float face2MiddleX = (leftEye2.getX() + rightEye2.getX()) / 2f;
    float face2MiddleY = (leftEye2.getY() + rightEye2.getY()) / 2f;
    face2Middle = new FirebaseVisionPoint(face2MiddleX, face2MiddleY, 0f);

    float face3MiddleX = (leftEye3.getX() + rightEye3.getX()) / 2f;
    float face3MiddleY = (leftEye3.getY() + rightEye3.getY()) / 2f;
    face3Middle = new FirebaseVisionPoint(face3MiddleX, face3MiddleY, 0f);
  }

  private void drawTrianglePoints(Canvas canvas) {
    FirebaseVisionPoint lineIntersectionPoint =
            faceCalculations.getLineIntersectionPoint();
    canvas.drawCircle(translateX(lineIntersectionPoint.getX()),
            translateY(lineIntersectionPoint.getY()),
            10f,
            idPaint);
    canvas.drawCircle(translateX(noseBridgePoint.getX()),
            translateY(noseBridgePoint.getY()),
            10f,
            idPaint);
    canvas.drawCircle(translateX(middleBottomPoint.getX()),
            translateY(middleBottomPoint.getY()),
            10f,
            idPaint);

  }

  private void drawLowerConnectingLines(Canvas canvas) {
    canvas.drawLine(translateX(face2Middle.getX()),
            translateY(face2Middle.getY()),
            translateX(face3Middle.getX()),
            translateY(face3Middle.getY()),
            idPaint);
  }

  private void drawTriangleLines(Canvas canvas) {
    FirebaseVisionPoint lineIntersectionPoint =
            faceCalculations.getLineIntersectionPoint();
    canvas.drawLine(translateX(noseBridgePoint.getX()),
            translateY(noseBridgePoint.getY()),
            translateX(lineIntersectionPoint.getX()),
            translateY(lineIntersectionPoint.getY()),
            idPaint);
    canvas.drawLine(translateX(noseBridgePoint.getX()),
            translateY(noseBridgePoint.getY()),
            translateX(middleBottomPoint.getX()),
            translateY(middleBottomPoint.getY())
            ,idPaint);
    canvas.drawLine(translateX(middleBottomPoint.getX()),
            translateY(middleBottomPoint.getY()),
            translateX(lineIntersectionPoint.getX()),
            translateY(lineIntersectionPoint.getY()),
            idPaint);

  }

  private void drawAngleCalculations(Canvas canvas) {

    float middleBottomX = (middleBottomPoint.getX()) / 1.1f;
    float middleBottomY = (middleBottomPoint.getY()) / 1.1f;
    double top = faceCalculations.getTopAngle();
    double intersection = faceCalculations.getIntersectionAngle();
    double bottom = faceCalculations.getBottomAngle();

    String roundedTop = String.format(Locale.ENGLISH, "%.2f", top);
    String roundedIntersection =
            String.format(Locale.ENGLISH, "%.2f", intersection);
    String roundedBottom = String.format(Locale.ENGLISH, "%.2f", bottom);

    canvas.drawText("Top Angle: " + roundedTop,
            translateX(noseBridgePoint.getX() + ID_X_OFFSET),
            translateY(noseBridgePoint.getY()),
            textPaint);
    canvas.drawText("Intersection Angle: " + roundedIntersection,
            translateX(middleBottomX + ID_X_OFFSET),
            translateY(middleBottomY + ID_Y_OFFSET * 0.50f),
            textPaint);
    canvas.drawText("Bottom Angle: " + roundedBottom,
            translateX(middleBottomPoint.getX() + ID_X_OFFSET),
            translateY(middleBottomPoint.getY() + ID_Y_OFFSET * 0.75f),
            textPaint);

  }

  private void drawExtraCalculations(Canvas canvas) {

    double maxIntersectionAngle = faceCalculations.updateMaxIntersectionAngle();
    double minIntersectionValue = faceCalculations.getMinIntersectionAngle();
    double averageIntersectionValue = faceCalculations.getAverageValue();

    String roundedMaxIntersection =
            String.format(Locale.ENGLISH, "%.2f", maxIntersectionAngle);
    String roundedMinIntersection = String.format(Locale.ENGLISH, "%.2f",
            minIntersectionValue);
    String roundedAvgIntersection = String.format(Locale.ENGLISH, "%.2f",
            averageIntersectionValue);

    canvas.drawText("Max Intersection Angle: " + roundedMaxIntersection,
            translateX(middleBottomPoint.getX()) + ID_X_OFFSET * 1.50f,
            translateY(middleBottomPoint.getY() + ID_Y_OFFSET * 1.5f),
            textPaint);
    canvas.drawText("Min Intersection Angle: " + roundedMinIntersection,
            translateX(middleBottomPoint.getX()) + ID_X_OFFSET * 1.50f,
            translateY(middleBottomPoint.getY() + ID_Y_OFFSET * 2.0f),
            textPaint);
    canvas.drawText("Avg Intersection Angle: " + roundedAvgIntersection,
            translateX(middleBottomPoint.getX()) + ID_X_OFFSET * 1.50f,
            translateY(middleBottomPoint.getY() + ID_Y_OFFSET * 2.5f),
            textPaint);

  }

}
