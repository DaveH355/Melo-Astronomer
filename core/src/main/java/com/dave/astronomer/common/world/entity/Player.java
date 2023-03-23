package com.dave.astronomer.common.world.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.dave.astronomer.client.world.component.BodyComponent;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.EntityType;


import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Player extends BaseEntity implements Disposable {
    private static final AtomicInteger ID_POOL = new AtomicInteger(1);

    public Player(CoreEngine engine, UUID uuid) {
       super(EntityType.PLAYER, engine);
       setUuid(uuid);
    }

    public abstract BodyComponent getBodyComponent();

    @Override
    public Vector2 getPosition() {
        return getBodyComponent().getBody().getPosition();
    }
    public void forcePosition(Vector2 position, float angle) {
        getBodyComponent().getBody().setTransform(position, angle);
    }
    @Override
    public Vector2 getVelocity() {
        return getBodyComponent().getBody().getLinearVelocity();
    }

    @Override
    public void setVelocity(Vector2 velocity) {
        getBodyComponent().getBody().setLinearVelocity(velocity);
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
        state.velocity = getVelocity();
        state.captureDateMillis = TimeUtils.millis();
        state.uuid = getUuid();
        state.stateID = ID_POOL.incrementAndGet();
        return state;
    }
    public void setState(State state) {
        forcePosition(state.position, 0);
        setUuid(state.uuid);
    }

    @Override
    public void dispose() {
        World world = getBodyComponent().getBody().getWorld();
        world.destroyBody(getBodyComponent().getBody());
    }
}
