package com.dave.astronomer.common.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;
import com.dave.astronomer.common.ashley.core.Entity;
import com.dave.astronomer.common.world.movement.MovementBehavior;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseEntity extends Entity implements Disposable {
    private static final AtomicInteger STATE_ID_POOL = new AtomicInteger(1);

    @Getter @Setter private UUID uuid = UUID.randomUUID();
    @Getter private final CoreEngine engine;
    @Getter private final EntityType<?> entityType;
    @Setter MovementBehavior movementBehavior = MovementBehavior.CUSTOM;

    @Getter private Deque<Vector2> deltaMovementBuffer = new ArrayDeque<>();

    public void lerpPosition(float x, float y) {
        if (movementBehavior == MovementBehavior.CUSTOM) return;
        deltaMovementBuffer.push(new Vector2(x, y));
    }
    public void lerpPosition(Vector2 vector2) {
        if (movementBehavior == MovementBehavior.CUSTOM) return;

        deltaMovementBuffer.push(new Vector2(vector2));
    }
    public Vector2 getDeltaMovement() {
        return deltaMovementBuffer.getLast();
    }

    @Override
    public void update(float delta) {
        movementBehavior.apply(this);

    }

    @ToString
    public static class State {
        public Vector2 position;
        public float angleRad;

        public UUID uuid;
        public Vector2 velocity;
        public int id;
    }
    public State captureState() {
        State state = new State();
        state.position = getPosition();
        state.angleRad = getBody().getAngle();
        state.velocity = getBody().getLinearVelocity();
        state.uuid = getUuid();
        state.id = STATE_ID_POOL.incrementAndGet();
        return state;
    }
    public State captureState(int id) {
        State state = captureState();
        state.id = id;
        return state;
    }

    public void forceState(State state) {
        getBody().setTransform(state.position, state.angleRad);
        getBody().setLinearVelocity(state.velocity);
        setUuid(state.uuid);
        deltaMovementBuffer.clear();
    }


    public BaseEntity(EntityType<?> entityType, CoreEngine engine) {
        this.engine = engine;
        this.entityType = entityType;
    }

    public Vector2 getPosition() {
        return getBody().getPosition();
    }

    public void forcePosition(Vector2 position, float angle) {
        getBody().setTransform(position, angle);
    }
    public void forcePosition(float x, float y, float angle) {
        getBody().setTransform(x, y, angle);
    }

    public abstract Body getBody();

    @Override
    public void onRemovedFromEngine() {
        dispose();
    }

    @Override
    public void dispose() {
        getBody().getWorld().destroyBody(getBody());
    }
}
