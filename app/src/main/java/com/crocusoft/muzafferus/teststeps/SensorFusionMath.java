package com.crocusoft.muzafferus.teststeps;

public class SensorFusionMath {

    private SensorFusionMath() {
    }

    public static float sum(float[] array) {
        float retval = 0;
        for (float anArray : array) {
            retval += anArray;
        }
        return retval;
    }

    public static float norm(float[] array) {
        float retval = 0;
        for (float anArray : array) {
            retval += anArray * anArray;
        }
        return (float) Math.sqrt(retval);
    }


    public static float dot(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

}