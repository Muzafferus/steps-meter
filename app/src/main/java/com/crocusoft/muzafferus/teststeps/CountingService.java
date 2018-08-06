package com.crocusoft.muzafferus.teststeps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.crocusoft.muzafferus.teststeps.MainActivity.ui;

public class CountingService extends Service implements SensorEventListener, StepListener {
    static SharedPreferences sharedPreferences;
    private SimpleStepDetector simpleStepDetector;
    private static SensorManager sensorManager;
    private boolean firsStep = true;
    private int eventStarter;

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
                simpleStepDetector = new SimpleStepDetector();
                simpleStepDetector.registerListener(this);
                sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);

            Log.i("muzaffar", "TYPE_ACCELEROMETER");

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (firsStep) {
                eventStarter = (int) event.values[0];
                firsStep = false;
            }
            sharedPreferences.edit().putInt("numSteps", (int) event.values[0] - eventStarter).apply();
            if (!sharedPreferences.getBoolean("stop", false)) {
                ui();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void step(long timeNs) {
        int i = sharedPreferences.getInt("numSteps", 0);
        i++;
        sharedPreferences.edit().putInt("numSteps", i).apply();
        if (!sharedPreferences.getBoolean("stop", false)) {
            ui();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}
