package com.crocusoft.muzafferus.teststeps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private static TextView textStep, textKm;
    Button btnStart, btnStop, btnpause;
    private static final String TEXT_NUM_STEPS = "Steps: ";
    private static final String KM = " km";
    Intent intent;

    static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textStep = findViewById(R.id.tv_steps);
        textKm = findViewById(R.id.tv_km);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        btnpause = findViewById(R.id.btn_pause);

        sharedPreferences = getSharedPreferences("StepCounter", Context.MODE_PRIVATE);
        intent = new Intent(MainActivity.this, CountingService.class);

        if (sharedPreferences.getInt("numSteps", 0) != 0) {
            ui();
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startService(intent);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (sharedPreferences.getInt("numSteps", 0) != 0) {
                    stopService(intent);
                    sharedPreferences.edit().putInt("numSteps", 0).apply();
                    ui();
                    btnStart.setText(R.string.start);
                }

            }
        });

        btnpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sharedPreferences.getInt("numSteps", 0) != 0) {
                    stopService(intent);
                    btnStart.setText(R.string.pause);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.edit().putBoolean("stop", false).apply();
        ui();
    }

    public static void ui() {
        double numKm = sharedPreferences.getInt("numSteps", 0) * 0.000762;
        DecimalFormat df = new DecimalFormat("0.000");
        String km = df.format(numKm) + KM;
        String steps = TEXT_NUM_STEPS + sharedPreferences.getInt("numSteps", 0);
        textStep.setText(steps);
        textKm.setText(km);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.edit().putBoolean("stop", true).apply();
    }


}