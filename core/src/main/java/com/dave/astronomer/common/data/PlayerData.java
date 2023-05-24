package com.dave.astronomer.common.data;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.common.PhysicsUtils;

public class PlayerData {
    private PlayerData(){}
    private static final Circle boundingShape;


    static {
        Sprite sprite = MainPlayer.createSpriteComponent().getSprite();
        boundingShape = PhysicsUtils.traceCircle(sprite, true);
    }

    public static Body createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;


        Body b = world.createBody(bodyDef);

        b.setUserData("player");


        b.createFixture(PhysicsUtils.toShape(boundingShape), 0);


        return b;
    }


}
