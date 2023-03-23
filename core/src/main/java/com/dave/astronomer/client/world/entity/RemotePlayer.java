package com.dave.astronomer.client.world.entity;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import com.dave.astronomer.client.world.ClientPhysicsSystem;
import com.dave.astronomer.client.world.component.BodyComponent;
import com.dave.astronomer.client.world.component.SpriteComponent;
import com.dave.astronomer.common.PhysicsUtils;
import com.dave.astronomer.common.world.CoreEngine;
import lombok.Getter;

import java.util.UUID;

public class RemotePlayer extends AbstractClientPlayer {
    @Getter
    private SpriteComponent spriteComponent;
    @Getter
    private BodyComponent bodyComponent;

    public RemotePlayer(CoreEngine engine, UUID uuid) {
        super(engine, uuid);



        spriteComponent = MainPlayer.createSpriteComponent();
        bodyComponent = createBodyComponent(spriteComponent);

        addComponents(
                spriteComponent,
                bodyComponent
        );

    }



    public BodyComponent createBodyComponent(SpriteComponent spriteComponent) {
        ClientPhysicsSystem physicsSystem = getEngine().getSystem(ClientPhysicsSystem.class);
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;

        Body body = physicsSystem.getWorld().createBody(def);

        FixtureDef fdef = new FixtureDef();


        Circle circle = PhysicsUtils.traceCircle(spriteComponent.getSprite(), true);
        fdef.shape = PhysicsUtils.toShape(circle);

        body.createFixture(fdef);
        return new BodyComponent(body);
    }


}
