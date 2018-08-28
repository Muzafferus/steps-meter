package com.crocusoft.muzafferus.teststeps;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class ActivityRecognizedService extends IntentService {
    SharedPreferences sharedPreferences;


    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences = getSharedPreferences("StepCounter", Context.MODE_PRIVATE);
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result.getProbableActivities());
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for (DetectedActivity activity : probableActivities) {
            switch (activity.getType()) {
                case DetectedActivity.RUNNING:
                    Log.e("MUZAFFERUS--", "Running: " + activity.getConfidence());
                    if (activity.getConfidence() > 50) {
                        sharedPreferences.edit().putBoolean("running", true).apply();
                        sharedPreferences.edit().putBoolean("otherActivity", false).apply();
                    }
                    break;
                case DetectedActivity.WALKING:
                    Log.e("MUZAFFERUS--", "Walking: " + activity.getConfidence());
                    if (activity.getConfidence() > 50) {
                        sharedPreferences.edit().putBoolean("running", false).apply();
                        sharedPreferences.edit().putBoolean("otherActivity", false).apply();
                    }
                default:
                    if (activity.getConfidence() > 50)
                        sharedPreferences.edit().putBoolean("otherActivity", true).apply();
            }

        }
    }
}