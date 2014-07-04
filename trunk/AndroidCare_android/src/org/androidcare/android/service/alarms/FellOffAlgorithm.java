package org.androidcare.android.service.alarms;

import android.util.Log;
import com.j256.ormlite.field.DatabaseField;

public class FellOffAlgorithm {
    @DatabaseField(id = true)
    private long id;

    private static final String TAG = FellOffAlgorithm.class.getName();

    private static final int SAMPLING_RATE = 30;
    private static final double DELTA = 8.0;

    private static int currentIndex = 0;

    private static float[] xIndexes = new float[SAMPLING_RATE];
    private static float[] yIndexes = new float[SAMPLING_RATE];
    private static float[] zIndexes = new float[SAMPLING_RATE];

    @DatabaseField
    private int fellOffThreshold;

    public FellOffAlgorithm() { }

    public FellOffAlgorithm(int threshold) {
        this.fellOffThreshold = threshold;
    }

    public static Integer run(float[] values) {
        int xAboveMin = 0;
        int yAboveMin = 0;
        int zAboveMin = 0;

        int sum = 0;

        xIndexes[currentIndex] = values[0];
        yIndexes[currentIndex] = values[1];
        zIndexes[currentIndex] = values[2];

        for (int position = 0 ; position < SAMPLING_RATE - 1; position++) {
            float currentX = xIndexes[position];
            float currentY = yIndexes[position];
            float currentZ = zIndexes[position];

            float nextX = xIndexes[position + 1];
            float nextY = yIndexes[position + 1];
            float nextZ = zIndexes[position + 1];

            if (Math.abs(nextX - currentX) > DELTA) {
                xAboveMin++;
            }

            if (Math.abs(nextY - currentY) > DELTA) {
                yAboveMin++;
            }

            if (Math.abs(nextZ - currentZ) > DELTA) {
                zAboveMin++;
            }

            sum = xAboveMin + yAboveMin + zAboveMin;
            if(sum > 0) {
                Log.d(TAG, currentIndex + " - " + sum + " = " + xAboveMin + ", " + yAboveMin + ", " + zAboveMin);
            }
        }

        currentIndex = (currentIndex + 1) % SAMPLING_RATE;

        return sum;
    }

    public int getFellOffThreshold() {
        return fellOffThreshold;
    }
}
