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

    public RemotePlayer(CoreEngine engine, UUID uuid) {
        super(engine, uuid);
        setMovementBehavior(new MovementBehavior.BasicLerp());

        spriteComponent = MainPlayer.createSpriteComponent();

        addComponents(
                spriteComponent
        );
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (isDead()) return;

        spriteComponent.getSprite().setPosition(getPosition().x, getPosition().y);


        MainPlayerSystem.determineAnimation(this, getDeltaSpeed());
    }

    @Override
    public Body createBody() {
        return PlayerData.createBody(getEngine().getSystem(PhysicsSystem.class).getWorld());
    }
}
