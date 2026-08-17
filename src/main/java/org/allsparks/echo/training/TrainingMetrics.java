package org.allsparks.echo.training;

public final class TrainingMetrics {
    public int trials;
    public int correctDirection;
    public int silenceExpected;

    public void recordDirection(boolean correct) {
        trials++;
        if (correct) {
            correctDirection++;
        }
    }

    public double directionAccuracy() {
        return trials == 0 ? 0.0 : (double) correctDirection / trials;
    }
}
