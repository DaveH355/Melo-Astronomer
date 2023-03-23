package com.dave.astronomer.common;

import com.badlogic.gdx.utils.TimeUtils;

import java.util.concurrent.TimeUnit;

public class PolledTimer {
    private long millis;
    private long lastCheck = TimeUtils.millis();
    public PolledTimer(double every, TimeUnit timeUnit) {
        this.millis = timeUnit.toMillis((long) every);

    }

    public boolean update() {
        if (TimeUtils.timeSinceMillis(lastCheck) < millis) return false;

        else {
            lastCheck = TimeUtils.millis();
            return true;
        }
    }

}
