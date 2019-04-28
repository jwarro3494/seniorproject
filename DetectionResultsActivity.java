// Author: Jamie Arrowood-Forrester

package com.google.firebase.samples.apps.mlkit.java;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.samples.apps.mlkit.R;
import com.google.firebase.samples.apps.mlkit.java.database.DAO;
import com.google.firebase.samples.apps.mlkit.java.database.DetectionResults;
import com.google.firebase.samples.apps.mlkit.java.facedetection.FaceCalculations;

/**
 * Persistent result set activity which allows the user to see previous text results and
 * delete them if space becomes an issue.
 */

public class DetectionResultsActivity extends AppCompatActivity {

    private final FaceCalculations faceCalculations = new FaceCalculations();

    final DAO dao = new DAO(this);
    private final static String TOOLBAR_TITLE = "Project CMT";

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_results);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(TOOLBAR_TITLE);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.detectionListView);
        populateListView();

        FloatingActionButton fab = findViewById(R.id.fab);
        final Intent startDetection =
                new Intent(this, LivePreviewActivity.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                faceCalculations.resetMaxIntersectionAngle();
                faceCalculations.resetMinIntersectionAngle();
                startActivity(startDetection);
            }
        });

        FloatingActionButton depopFab = findViewById(R.id.depopFab);
        depopFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                depopulateListView();
                Toast.makeText(DetectionResultsActivity.this,
                        "All records were deleted successfully!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateListView() {
        ArrayAdapter<DetectionResults> resultsAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1, dao.getAllResults());

        listView.setAdapter(resultsAdapter);

    }

    private void depopulateListView() {
        ArrayAdapter<DetectionResults> resultsAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_expandable_list_item_1,
                        android.R.id.text1, dao.purgeRecords());

        listView.setAdapter(resultsAdapter);
    }

}
