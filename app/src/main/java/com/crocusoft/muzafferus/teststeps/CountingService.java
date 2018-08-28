package com.crocusoft.muzafferus.teststeps;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import static com.crocusoft.muzafferus.teststeps.MainActivity.ui;

public class CountingService extends Service implements SensorEventListener, StepListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    static SharedPreferences sharedPreferences;
    private StepDetector stepDetector;
    private static SensorManager sensorManager;
    private int eventStarter;
    int stepWalk, stepRun;
    public GoogleApiClient mApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences("StepCounter", Context.MODE_PRIVATE);
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Sensor counter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                sensorManager.registerListener(this, counter, SensorManager.SENSOR_DELAY_FASTEST);
            } else {
                Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                stepDetector = new StepDetector();
                stepDetector.registerListener(this);

                sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                if (sharedPreferences.getBoolean("firsStep", false)) {
                    eventStarter = (int) event.values[0];
                    sharedPreferences.edit().putBoolean("firsStep", false).apply();
                    if (sharedPreferences.getBoolean("pause", false)) {
                        stepRun = sharedPreferences.getInt("numRunSteps", 0);
                        stepWalk = sharedPreferences.getInt("numWalkSteps", 0);
                        sharedPreferences.edit().putBoolean("firsStep", false).apply();
                        sharedPreferences.edit().putBoolean("pause", false).apply();
                    }
                }

                if (sharedPreferences.getBoolean("stop", false)) {
                    eventStarter = (int) event.values[0];
                    stepRun = sharedPreferences.getInt("numRunSteps", 0);
                    stepWalk = sharedPreferences.getInt("numWalkSteps", 0);
                    if (sharedPreferences.getBoolean("running", false)) {
                        stepRun++;
                        sharedPreferences.edit().putInt("numRunSteps", stepRun).apply();
                    } else {
                        stepWalk++;
                        sharedPreferences.edit().putInt("numWalkSteps", stepWalk).apply();
                    }
                } else {

                    if (sharedPreferences.getBoolean("running", false)) {
                        sharedPreferences.edit().putInt("numRunSteps", stepRun - stepWalk + (int) event.values[0] - eventStarter).apply();
                    } else {
                        sharedPreferences.edit().putInt("numWalkSteps", stepWalk - stepRun + (int) event.values[0] - eventStarter).apply();
                    }
                }
                if (!sharedPreferences.getBoolean("stop", false)) {
                    ui();
                }

                Log.i("MUZAFFERUS--", "(int) event.values[0]): " + (int) event.values[0]);
                Log.i("MUZAFFERUS--", "eventStarter: " + eventStarter);
                Log.i("MUZAFFERUS--", "sharedPreferences.getBoolean(\"running\", false): " + sharedPreferences.getBoolean("running", false));
                Log.i("MUZAFFERUS--", "sharedPreferences.getInt(\"numWalkSteps\", 0): " + sharedPreferences.getInt("numWalkSteps", 0));
                Log.i("MUZAFFERUS--", "sharedPreferences.getInt(\"numRunSteps\", 0): " + sharedPreferences.getInt("numRunSteps", 0));
            }
        } else {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                stepDetector.updateAccel(
                        event.timestamp, event.values[0], event.values[1], event.values[2]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void step(long timeNs) {
        if (sharedPreferences.getBoolean("running", false)) {
            int i = sharedPreferences.getInt("numRunSteps", 0);
            i++;
            sharedPreferences.edit().putInt("numRunSteps", i).apply();
        } else {
            int i = sharedPreferences.getInt("numWalkSteps", 0);
            i++;
            sharedPreferences.edit().putInt("numWalkSteps", i).apply();
        }
        if (!sharedPreferences.getBoolean("stop", false)) {
            ui();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent);

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
