package com.dave.astronomer.common.data;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.common.PhysicsUtils;

public class PlayerData {
    private PlayerData(){}
    private static final Circle boundingCircle;
    public static final float METERS_PER_SEC = 3.5f;

    static {
        Sprite sprite = MainPlayer.createSpriteComponent().getSprite();
        boundingCircle = PhysicsUtils.traceCircle(sprite, true);
    }
    public static Circle getBoundingCircle() {
        return new Circle(boundingCircle);
    }
}
