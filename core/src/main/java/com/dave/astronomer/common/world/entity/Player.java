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
    public Vector2 getExactVelocity() {
        return getBody().getLinearVelocity();
    }

    //TODO: remove this temp method
    public Knife throwKnife(float targetAngleRad) {
        Vector2 playerPos = getPosition();
        Vector2 knifePos = new Vector2(playerPos.x, playerPos.y + 1);
        Knife knife = new Knife(getEngine(), knifePos, targetAngleRad);

        getEngine().addEntity(knife);
        return knife;

    }


    public void setState(State state) {
        forcePosition(state.position, 0);
        setUuid(state.uuid);
    }

}
