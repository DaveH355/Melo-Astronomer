package com.dave.astronomer.common.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;
import com.dave.astronomer.common.ashley.core.Entity;
import com.dave.astronomer.common.network.packet.ClientboundAddEntityPacket;
import com.dave.astronomer.common.world.movement.MovementBehavior;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

public abstract class BaseEntity extends Entity implements Disposable {
    @Getter @Setter
    private UUID uuid = UUID.randomUUID();
    @Getter
    private final CoreEngine engine;
    @Getter
    private final EntityType<?> entityType;
    @Setter
    private MovementBehavior movementBehavior = MovementBehavior.CUSTOM;
    @Getter
    private Vector2 deltaMovement;
    @Getter float deltaSpeed;

    public BaseEntity(EntityType<?> entityType, CoreEngine engine) {
        this.engine = engine;
        this.entityType = entityType;
    }

    public void lerpPosition(Vector2 vector2, float speed) {
        deltaMovement = vector2;
        deltaSpeed = speed;
    }

    @Override
    public void update(float delta) {
        movementBehavior.apply(this);
    }

    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        forcePosition(packet.position, packet.angleRad);
        setUuid(packet.uuid);
    }

    public Vector2 getPosition() {
        return getBody().getPosition();
    }

    public void forcePosition(Vector2 position, float angle) {
        getBody().setTransform(position, angle);
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

    public void forceState(State state) {
        getBody().setTransform(state.position, state.angleRad);
        getBody().setLinearVelocity(state.velocity);
        setUuid(state.uuid);
        deltaMovement = state.position;
    }
    public State captureState() {
        State state = new State();
        state.position = getPosition();
        state.angleRad = getBody().getAngle();
        state.velocity = getBody().getLinearVelocity();
        state.uuid = getUuid();
        return state;
    }
    @ToString
    public static class State {
        public Vector2 position;
        public float angleRad;
        public UUID uuid;
        public Vector2 velocity;
    }

}
