package com.dave.astronomer.common.data;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.common.PhysicsUtils;

public class PlayerData {
    private PlayerData(){}
    private static final Circle boundingCircle;


    static {
        Sprite sprite = MainPlayer.createSpriteComponent().getSprite();
        boundingCircle = PhysicsUtils.traceCircle(sprite, true);
    }

    public static Body createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;


        Body b = world.createBody(bodyDef);
        FixtureDef fdef = new FixtureDef();



        fdef.shape = PhysicsUtils.toShape(getBoundingCircle());


        b.setUserData("player");
        b.createFixture(fdef);

        return b;
    }

    public static Circle getBoundingCircle() {
        return new Circle(boundingCircle);
    }
}
