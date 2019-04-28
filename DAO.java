// Author: Jamie Arrowood-Forrester

package com.google.firebase.samples.apps.mlkit.java.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Database Controller instance for saving calculation values from the detector
 */

public class DAO extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "detection_results_db";
    private static int VERSION = 1;
    private static String TABLE_NAME = "results";
    private String query = "CREATE TABLE " + TABLE_NAME +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TIMESTAMP TEXT," +
            " MAX_INTERSECTION_ANGLE NUMBER, MIN_INTERSECTION_ANGLE NUMBER," +
            "AVERAGE_INTERSECTION_ANGLE)";

    public DAO(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertRecord(DetectionResults detectionResults) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("TIMESTAMP", detectionResults.getTimeStamp());
        contentValues.put("MAX_INTERSECTION_ANGLE",
                detectionResults.getMaxIntersectionAngle());
        contentValues.put("MIN_INTERSECTION_ANGLE",
                detectionResults.getMinIntersectionAngle());
        contentValues.put("AVERAGE_INTERSECTION_ANGLE",
                detectionResults.getAverageIntersectionAngle());
        long resultSet = sqLiteDatabase.insert(TABLE_NAME, null,
                contentValues);
        Log.d("Database", "Inserting: " + resultSet);
        return resultSet != -1;
    }

    public ArrayList<DetectionResults> getAllResults() {
        ArrayList<DetectionResults> detectionResultsList =
                new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DetectionResults detectionResults = new DetectionResults();
                detectionResults.setTimeStamp(cursor.getString(1));
                detectionResults.setMaxIntersectionAngle(cursor.getDouble(2));
                detectionResults.setMinIntersectionAngle(cursor.getDouble(3));
                detectionResults.setAverageIntersectionAngle(cursor.getDouble(4));
                detectionResultsList.add(detectionResults);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d("Database", "Getting All Results: " + detectionResultsList);
        return detectionResultsList;
    }

    public ArrayList<DetectionResults> purgeRecords() {
        ArrayList<DetectionResults> purgeResultsList
                = new ArrayList<>();
        String purgeAll = "DELETE FROM " + TABLE_NAME;
        SQLiteDatabase purge = this.getWritableDatabase();
        Cursor cursor = purge.rawQuery(purgeAll, null);
        if (cursor.moveToFirst()) {
            do {
                DetectionResults detectionResults = new DetectionResults();
                detectionResults.setTimeStamp(cursor.getString(1));
                detectionResults.setMaxIntersectionAngle(cursor.getDouble(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        purge.close();
        return purgeResultsList;
    }

}
