package com.dave.astronomer.common.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;
import com.dave.astronomer.common.PhysicsUtils;
import com.dave.astronomer.common.ashley.core.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

public abstract class BaseEntity extends Entity implements Disposable {
    @Getter @Setter private UUID uuid = UUID.randomUUID();
    @Getter private final CoreEngine engine;
    @Getter private final EntityType<?> entityType;
    @Getter @Setter private float speed = 1;
    public float arrivalRadius = 0.2f;
    public float positionTolerance = 0.5f;

    private Deque<Vector2> deltaMovementBuffer = new ArrayDeque<>();

    public void lerpPosition(float x, float y) {
        deltaMovementBuffer.push(new Vector2(x, y));
    }
    public void lerpPosition(Vector2 vector2) {
        deltaMovementBuffer.push(new Vector2(vector2));
    }
    public Vector2 getDeltaMovement() {
        return deltaMovementBuffer.getLast();
    }

    public void update(float delta) {
        if (deltaMovementBuffer.isEmpty()) return;
        Vector2 target = getDeltaMovement();

        Vector2 velocity = PhysicsUtils.velocityToPosition(getBody(), target, getSpeed(), arrivalRadius);
        getBody().setLinearVelocity(velocity);

        float distance = getPosition().dst(target);
        if (distance <= positionTolerance) deltaMovementBuffer.removeLast();

        else if (distance > positionTolerance * 2) {
            forcePosition(target, getBody().getAngle());
            deltaMovementBuffer.clear();
        }

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
    public void dispose() {
        getBody().getWorld().destroyBody(getBody());
    }
}
