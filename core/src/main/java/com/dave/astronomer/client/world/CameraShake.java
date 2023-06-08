package com.dave.astronomer.client.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class CameraShake {
    private static float duration = 0;
    private static float counter = 0;
    private static float power = 0;
    private static final Vector3 pos = new Vector3();

    public static void shake(float shakePower, float durationLength) {
        power = shakePower;
        duration = durationLength;
        counter = 0;
    }

    public static void tick(float delta) {
        if (counter <= duration) {
            float currentPower = power * ((duration - counter) / duration);

            pos.x = (MathUtils.random(1f) - 0.5f) * 2 * currentPower;
            pos.y = (MathUtils.random(1f) - 0.5f) * 2 * currentPower;

            counter += delta;
        } else {
            duration = 0;
        }
    }

    public static float getTimeLeft() {
        return duration;
    }

    public static Vector3 getShake() {
        return pos;
    }
}
