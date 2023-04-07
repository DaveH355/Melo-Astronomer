package com.dave.astronomer.client.world.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.dave.astronomer.client.world.ClientPhysicsSystem;
import com.dave.astronomer.client.world.component.SpriteComponent;
import com.dave.astronomer.common.data.PlayerData;
import com.dave.astronomer.common.world.CoreEngine;
import lombok.Getter;

import java.util.UUID;

public class RemotePlayer extends AbstractClientPlayer {
    @Getter
    private SpriteComponent spriteComponent;
    @Getter
    private Body body;

    public RemotePlayer(CoreEngine engine, UUID uuid) {
        super(engine, uuid);



        spriteComponent = MainPlayer.createSpriteComponent();
        body = PlayerData.createBody(engine.getSystem(ClientPhysicsSystem.class).getWorld());

        addComponents(
                spriteComponent
        );

    }

}
