package com.dave.astronomer.client.world.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.dave.astronomer.client.world.MainPlayerSystem;
import com.dave.astronomer.client.world.component.SpriteComponent;
import com.dave.astronomer.common.data.PlayerData;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.PhysicsSystem;
import com.dave.astronomer.common.world.movement.MovementBehavior;
import lombok.Getter;

import java.util.UUID;

public class RemotePlayer extends AbstractClientPlayer {
    @Getter
    private SpriteComponent spriteComponent;
    @Getter
    private Body body;


    public RemotePlayer(CoreEngine engine, UUID uuid) {
        super(engine, uuid);
        setMovementBehavior(new MovementBehavior.BasicLerp());

        spriteComponent = MainPlayer.createSpriteComponent();
        body = PlayerData.createBody(engine.getSystem(PhysicsSystem.class).getWorld());


        addComponents(
                spriteComponent
        );
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        spriteComponent.getSprite().setPosition(getPosition().x, getPosition().y);


        MainPlayerSystem.determineAnimation(this, getDeltaSpeed());
    }
}
