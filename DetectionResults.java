// Author: Jamie Arrowood-Forrester

package com.google.firebase.samples.apps.mlkit.java.database;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Getters and setters for the accompanying database controller and subsequent
 * activities.
 */

public class DetectionResults {

    private String timeStamp;
    private double maxIntersectionAngle;
    private double minIntersectionAngle;
    private double averageIntersectionAngle;

    public double getAverageIntersectionAngle() {
        return averageIntersectionAngle;
    }

    public void setAverageIntersectionAngle(double averageIntersectionAngle) {
        this.averageIntersectionAngle = averageIntersectionAngle;
    }

    public double getMinIntersectionAngle() {
        return minIntersectionAngle;
    }

    public void setMinIntersectionAngle(double minIntersectionAngle) {
        this.minIntersectionAngle = minIntersectionAngle;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getMaxIntersectionAngle() {
        Log.d("Database", "Getting Max: " + maxIntersectionAngle);
        return maxIntersectionAngle;
    }

    public void setMaxIntersectionAngle(double maxIntersectionAngle) {
        this.maxIntersectionAngle = maxIntersectionAngle;
        Log.d("Database", "Setting Max: " + maxIntersectionAngle);
    }

    @NotNull
    @Override
    public String toString() {
        return timeStamp + "Max Intersection Angle: " +
                String.format(Locale.ENGLISH, "%.2f", maxIntersectionAngle) +
                "\nMin Intersection Angle: " +
                String.format(Locale.ENGLISH, "%.2f", minIntersectionAngle) +
                "\nAverage Intersection Angle: " +
                String.format(Locale.ENGLISH, "%.2f", averageIntersectionAngle);
    }
}
