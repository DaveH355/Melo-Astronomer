package com.dave.astronomer.common.world.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.EntityType;


import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Player extends BaseEntity implements Disposable {
    private static final AtomicInteger STATE_ID_POOL = new AtomicInteger(1);

    public Player(CoreEngine engine, UUID uuid) {
       super(EntityType.PLAYER, engine);
       setUuid(uuid);
    }


    public void forcePosition(Vector2 position, float angle) {
        getBody().setTransform(position, angle);

    }
    public Vector2 getExactVelocity() {
        return getBody().getLinearVelocity();
    }

    @Override
    public void setVelocity(Vector2 velocity) {
        super.setVelocity(velocity);
        getBody().setLinearVelocity(velocity);
    }

    public static class State {
        public Vector2 position;

        public UUID uuid;
        public Vector2 velocity;
        public long captureDateMillis;
        public int stateID;
    }
    public State captureState() {
        State state = new State();
        state.position = getPosition();
        state.velocity = getExactVelocity();
        state.captureDateMillis = TimeUtils.millis();
        state.uuid = getUuid();
        state.stateID = STATE_ID_POOL.incrementAndGet();
        return state;
    }
    public void setState(State state) {
        forcePosition(state.position, 0);
        setUuid(state.uuid);
    }

    @Override
    public void dispose() {
        World world = getBody().getWorld();
        world.destroyBody(getBody());
    }
}
