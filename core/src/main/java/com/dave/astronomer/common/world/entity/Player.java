package com.dave.astronomer.common.world.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.EntityType;
import lombok.ToString;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Player extends BaseEntity implements Disposable {

    public Player(CoreEngine engine, UUID uuid) {
       super(EntityType.PLAYER, engine);
       setUuid(uuid);
    }
    public Vector2 getExactVelocity() {
        return getBody().getLinearVelocity();
    }



    public void setState(State state) {
        forcePosition(state.position, 0);
        setUuid(state.uuid);
    }

}
