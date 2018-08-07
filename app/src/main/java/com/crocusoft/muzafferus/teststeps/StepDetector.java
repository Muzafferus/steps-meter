package com.crocusoft.muzafferus.teststeps;

import android.hardware.SensorManager;

public class StepDetector {
    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;

    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = {new float[3 * 2], new float[3 * 2]};
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    private StepListener listener;

    StepDetector() {
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    public void registerListener(StepListener listener) {
        this.listener = listener;
    }

    public void updateAccel(long timeNs, float x, float y, float z) {
        float[] event = new float[3];
        event[0] = x;
        event[1] = y;
        event[2] = z;
        float vSum = 0;
        for (int i = 0; i < 3; i++) {
            final float v = mYOffset + event[i] * mScale[1];
            vSum += v;
        }
        int k = 0;
        float v = vSum / 3;

        float direction = (Float.compare(v, mLastValues[k]));
        if (direction == -mLastDirections[k]) {
            // Direction changed
            int extType = (direction > 0 ? 0 : 1);
            mLastExtremes[extType][k] = mLastValues[k];
            float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

            float mLimit = 10;
            if (diff > mLimit) {

                boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                boolean isNotContra = (mLastMatch != 1 - extType);

                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                    listener.step(timeNs);
                    mLastMatch = extType;
                } else {
                    mLastMatch = -1;
                }
            }
            mLastDiff[k] = diff;
        }
        mLastDirections[k] = direction;
        mLastValues[k] = v;

    }
}