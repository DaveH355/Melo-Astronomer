package com.dave.astronomer.common.data;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
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
    public static Body createBody(World world, Sprite sprite) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        sprite.setOriginCenter();
        bodyDef.position.set(sprite.getOriginX(), sprite.getOriginY());


        Body b = world.createBody(bodyDef);

        b.setUserData("player");


        b.createFixture(PhysicsUtils.toShape(boundingShape), 0);


        return b;
    }



}
