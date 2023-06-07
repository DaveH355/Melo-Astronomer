package com.dave.astronomer.common.data;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.common.PhysicsUtils;

public class PlayerData {
    private PlayerData(){}
    private static final Circle circle;
    private static final Rectangle rectangle;


    static {
        Sprite sprite = MainPlayer.createSpriteComponent().getSprite();
        circle = PhysicsUtils.traceCircle(sprite, true);
        rectangle = PhysicsUtils.traceRectangle(sprite);
    }

    public static Body createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;



        Body b = world.createBody(bodyDef);

        b.setUserData("player");


        b.createFixture(PhysicsUtils.toShape(circle), 0);

        //hit box sensor
        FixtureDef fdef = new FixtureDef();
        fdef.isSensor = true;
        fdef.shape = PhysicsUtils.toShape(rectangle);

        b.createFixture(fdef);

        return b;
    }



}
