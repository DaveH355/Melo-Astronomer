package com.dave.astronomer.common.world.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.ecs.CoreEngine;
import com.dave.astronomer.common.world.EntityType;
import lombok.ToString;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Player extends BaseEntity implements Disposable {
    private static final AtomicInteger STATE_ID_POOL = new AtomicInteger(1);

    public Player(CoreEngine engine, UUID uuid) {
       super(EntityType.PLAYER, engine);
       setUuid(uuid);
    }
    public Vector2 getExactVelocity() {
        return getBody().getLinearVelocity();
    }


    @ToString
    public static class State {
        public Vector2 position;

        public UUID uuid;
        public Vector2 velocity;
        public long captureDateMillis;
        public int id;
    }
    public State captureState() {
        State state = new State();
        state.position = getPosition();
        state.velocity = getExactVelocity();
        state.captureDateMillis = TimeUtils.millis();
        state.uuid = getUuid();
        state.id = STATE_ID_POOL.incrementAndGet();
        return state;
    }
    public State captureState(int id) {
        State state = captureState();
        state.id = id;
        return state;
    }
    public void setState(State state) {
        forcePosition(state.position, 0);
        setUuid(state.uuid);
    }

}
