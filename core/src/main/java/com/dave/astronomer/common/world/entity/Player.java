package com.dave.astronomer.common.world.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.dave.astronomer.client.world.entity.Knife;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.EntityType;

import java.util.UUID;

public abstract class Player extends BaseEntity implements Disposable {

    public Player(CoreEngine engine, UUID uuid) {
       super(EntityType.PLAYER, engine);
       setUuid(uuid);
    }
    public Player(CoreEngine engine) {
        super(EntityType.PLAYER, engine);
    }

    //TODO: remove this temp method
    public Knife throwKnife(float targetAngleRad) {
        Vector2 bodyCenter = getBody().getPosition();
        Vector2 knifePos = new Vector2(bodyCenter.x, bodyCenter.y);
        Knife knife = new Knife(getEngine(), knifePos, targetAngleRad);

        getEngine().addEntity(knife);
        return knife;

    }
}
