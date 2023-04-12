package com.dave.astronomer.common;

import java.util.concurrent.TimeUnit;

public class DeltaTimer {

    private float deltaCount;
    private float millis;

    public DeltaTimer(float every, TimeUnit timeUnit) {
        this.millis = timeUnit.toMillis((long) every);
    }
    public boolean update(float delta) {
        deltaCount += delta * 1000; // Convert delta to milliseconds
        if (deltaCount >= millis) {
            deltaCount -= millis;
            return true;
        }
        return false;
    }

    public void reset() {
        deltaCount = 0;
    }
}
