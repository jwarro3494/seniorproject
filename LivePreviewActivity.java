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
// Modified by Jamie Arrowood-Forrester for Dr. Tamirat Abegaz under the
// apache 2.0 license above.
package com.google.firebase.samples.apps.mlkit.java;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ToggleButton;

import com.google.android.gms.common.annotation.KeepName;
import com.google.firebase.samples.apps.mlkit.R;
import com.google.firebase.samples.apps.mlkit.common.CameraSource;
import com.google.firebase.samples.apps.mlkit.common.CameraSourcePreview;
import com.google.firebase.samples.apps.mlkit.common.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.java.database.DAO;
import com.google.firebase.samples.apps.mlkit.java.database.DetectionResults;
import com.google.firebase.samples.apps.mlkit.java.facedetection.FaceCalculations;
import com.google.firebase.samples.apps.mlkit.java.facedetection.FaceDetectionProcessor;
import com.google.firebase.samples.apps.mlkit.java.facedetection.FaceGraphic;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Demo app showing the various features of ML Kit for Firebase. This class is used to
 * set up continuous frame processing on frames from a camera source. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
    implements OnRequestPermissionsResultCallback,
        View.OnClickListener {
  private static final String FACE_DETECTION = "Face Detection";
  private static final String TAG = "LivePreviewActivity";
  private static final int PERMISSION_REQUESTS = 1;

  private static DetectionResults detectionResults = new DetectionResults();
  private final DAO dao = new DAO(this);
  private static FaceCalculations faceCalculations = new FaceCalculations();
  private CameraSource cameraSource = null;
  public CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private String selectedModel = FACE_DETECTION;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.activity_live_preview);

    preview = (CameraSourcePreview) findViewById(R.id.firePreview);
    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = (GraphicOverlay) findViewById(R.id.fireFaceOverlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }

    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
    } else {
      getRuntimePermissions();
    }

    findViewById(R.id.startSwitch).setOnClickListener(this);
    findViewById(R.id.stopSwitch).setOnClickListener(this);
    findViewById(R.id.facingSwitch).setOnClickListener(this);

  }

  public void createCameraSource(String model) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }

    try {
        Log.i(TAG, "Using Face Detector Processor");
        cameraSource.setMachineLearningFrameProcessor(new FaceDetectionProcessor());
    } catch (Exception e) {
      Log.e(TAG, "can not create camera source: " + model);
      cameraSource.release();
    }
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  public void startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null");
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null");
        }
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    startCameraSource();
  }

  /** Stops the camera. */
  @Override
  protected void onPause() {
    super.onPause();
    preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
  }

  private String[] getRequiredPermissions() {
    try {
      PackageInfo info =
          this.getPackageManager()
              .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
      String[] ps = info.requestedPermissions;
      if (ps != null && ps.length > 0) {
        return ps;
      } else {
        return new String[0];
      }
    } catch (Exception e) {
      return new String[0];
    }
  }

  private boolean allPermissionsGranted() {
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        return false;
      }
    }
    return true;
  }

  private void getRuntimePermissions() {
    List<String> allNeededPermissions = new ArrayList<>();
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        allNeededPermissions.add(permission);
      }
    }

    if (!allNeededPermissions.isEmpty()) {
      ActivityCompat.requestPermissions(
          this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
    }
  }

  @Override
  public void onRequestPermissionsResult(
          int requestCode, String[] permissions, int[] grantResults) {
    Log.i(TAG, "Permission granted!");
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private static boolean isPermissionGranted(Context context, String permission) {
    if (ContextCompat.checkSelfPermission(context, permission)
        == PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "Permission granted: " + permission);
      return true;
    }
    Log.i(TAG, "Permission NOT granted: " + permission);
    return false;
  }

  private String getLocalDate() {
    DateFormat resultDateFormat =
            DateFormat.getDateTimeInstance();
    Date time = new Date();
    return String.format(Locale.ENGLISH, "%s%n",
            resultDateFormat.format(time));
  }

  private void setResultsToDatabase() {
    double safetyCheck = (Double.MAX_VALUE + Double.MIN_VALUE) / 2.0;
    if (faceCalculations.getAverageValue() != safetyCheck) {
      detectionResults.setTimeStamp(getLocalDate());
      detectionResults.setMaxIntersectionAngle(faceCalculations.
              updateMaxIntersectionAngle());
      detectionResults.setMinIntersectionAngle(faceCalculations.
              getMinIntersectionAngle());
      detectionResults.setAverageIntersectionAngle(faceCalculations.
              getAverageValue());
      dao.insertRecord(detectionResults);
    }
  }

  @Override
  public void onClick(View v) {
    Intent showResults =
            new Intent(this, DetectionResultsActivity.class);
    if (cameraSource != null && allPermissionsGranted()) {
      if (v.getId() == R.id.startSwitch) {
        createCameraSource(selectedModel);
      }

      if (v.getId() == R.id.stopSwitch) {
        setResultsToDatabase();
        Log.d("Database", "Max Intersection: " + detectionResults);
        onPause();
        startActivity(showResults);
      }

      if (v.getId() == R.id.facingSwitch) {
        onPause();
        if (cameraSource.getCameraFacing() == CameraSource.CAMERA_FACING_FRONT) {
          cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
          startCameraSource();
        } else {
          cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
          startCameraSource();
        }
      }
    }
  }

}
