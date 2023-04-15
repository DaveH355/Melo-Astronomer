package com.dave.astronomer.client.world.entity;


import com.dave.astronomer.client.world.component.SpriteComponent;
import com.dave.astronomer.common.data.PlayerData;
import com.dave.astronomer.common.world.ecs.CoreEngine;
import com.dave.astronomer.common.world.entity.Player;

import java.util.UUID;

public abstract class AbstractClientPlayer extends Player {

    public AbstractClientPlayer(CoreEngine engine, UUID uuid) {
        super(engine, uuid);
        setSpeed(PlayerData.METERS_PER_SEC);
    }
    public abstract SpriteComponent getSpriteComponent();
}
