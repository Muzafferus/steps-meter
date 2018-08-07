package com.crocusoft.muzafferus.teststeps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private static TextView textStep, textKm;
    Button btnStart, btnStop, btnpause, btn_setting;
    private static final String TEXT_NUM_STEPS = "Steps: ";
    private static final String KM = " km";
    Intent intent;
    ViewDialogHeightInfo viewDialogHeightInfo;

    static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textStep = findViewById(R.id.tv_steps);
        textKm = findViewById(R.id.tv_km);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        btnpause = findViewById(R.id.btn_pause);
        btn_setting = findViewById(R.id.btn_setting);

        sharedPreferences = getSharedPreferences("StepCounter", Context.MODE_PRIVATE);
        intent = new Intent(MainActivity.this, CountingService.class);

        if (sharedPreferences.getInt("numSteps", 0) != 0) {
            ui();
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (sharedPreferences.getInt("numSteps", 0) == 0 ||
                        sharedPreferences.getBoolean("pause", false)) {
                    sharedPreferences.edit().putBoolean("firsStep", true).apply();
                    startService(intent);
                }
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
                    sharedPreferences.edit().putBoolean("pause", false).apply();
                }
            }
        });

        btnpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sharedPreferences.getInt("numSteps", 0) != 0) {
                    stopService(intent);
                    btnStart.setText(R.string.resume);
                    sharedPreferences.edit().putBoolean("firsStep", false).apply();
                    sharedPreferences.edit().putBoolean("pause", true).apply();
                }
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDialogHeightInfo = new ViewDialogHeightInfo();
                viewDialogHeightInfo.showDialog(MainActivity.this);
                viewDialogHeightInfo.heightText.setText(String.valueOf(sharedPreferences.getInt("height", 180)));
                float a = sharedPreferences.getFloat("stepLeighForSex", 0.0f);
                if (a == 0.00000413f) {
                    viewDialogHeightInfo.rbFemale.setChecked(true);
                    viewDialogHeightInfo.rbMale.setChecked(false);
                } else {
                    viewDialogHeightInfo.rbMale.setChecked(true);
                    viewDialogHeightInfo.rbFemale.setChecked(false);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.edit().putBoolean("stop", false).apply();
        ui();

        if (sharedPreferences.getBoolean("firstRun", true)) {
            viewDialogHeightInfo = new ViewDialogHeightInfo();
            viewDialogHeightInfo.showDialog(MainActivity.this);
        }
    }

    public static void ui() {
        double avarage = sharedPreferences.getFloat("stepLeighForSex", 0.00000414f) *
                sharedPreferences.getInt("height", 175);
        double numKm = sharedPreferences.getInt("numSteps", 0) * avarage;
        DecimalFormat df = new DecimalFormat("0.000");
        String km = df.format(numKm) + KM;
        String steps = TEXT_NUM_STEPS + sharedPreferences.getInt("numSteps", 0);
        textStep.setText(steps);
        textKm.setText(km);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.edit().putBoolean("stop", true).apply();
    }

    private class ViewDialogHeightInfo {
        private EditText heightText;
        private RadioButton rbMale, rbFemale;
        SharedPreferences sharedPreferences;

        public void showDialog(final Activity activity) {
            sharedPreferences = activity.getApplicationContext().getSharedPreferences("StepCounter", Context.MODE_PRIVATE);
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.layout_basic_info);
            Button saveButton = dialog.findViewById(R.id.btn_save);
            heightText = dialog.findViewById(R.id.etxt_height);
            rbFemale = dialog.findViewById(R.id.rdbtn_female);
            rbMale = dialog.findViewById(R.id.rdbtn_male);
            rbMale.setChecked(true);


            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(heightText.getText())) {
                        if (rbFemale.isChecked()) {
                            sharedPreferences.edit().putFloat("stepLeighForSex", 0.00000413f).apply();
                        } else if (rbMale.isChecked()) {
                            sharedPreferences.edit().putFloat("stepLeighForSex", 0.00000415f).apply();
                        } else {
                            sharedPreferences.edit().putFloat("stepLeighForSex", 0.00000414f).apply();
                        }
                        sharedPreferences.edit().putInt("height", Integer.parseInt(heightText.getText().toString())).apply();
                        sharedPreferences.edit().putBoolean("firstRun", false).apply();
                        dialog.dismiss();
                         } else {
                        Toast.makeText(activity.getApplicationContext(), "Please write your height!", Toast.LENGTH_LONG).show();
                    }
                }
            });
            dialog.show();
        }
    }
}