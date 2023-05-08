package com.dave.astronomer.common.world.movement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.dave.astronomer.common.PhysicsUtils;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.EntityType;

import java.util.Deque;

public class MovementBehavior {
    private static final float POSITION_TOLERANCE = 0.5f;
    public static final MovementBehavior CUSTOM = new MovementBehavior();
    public static final MovementBehavior BASIC_INTERPOLATE = new BasicLerp();

    public void apply(BaseEntity entity) {

    }

    static class BasicLerp extends MovementBehavior {
        @Override
        public void apply(BaseEntity entity) {
            Deque<Vector2> deltaMovementBuffer = entity.getDeltaMovementBuffer();
            Body body = entity.getBody();
            EntityType<?> entityType = entity.getEntityType();

            if (deltaMovementBuffer.isEmpty()) return;
            Vector2 target = entity.getDeltaMovement();

            Vector2 velocity = PhysicsUtils.velocityToPosition(body, target, entityType.speed);
            body.setLinearVelocity(velocity);

            float distance = entity.getPosition().dst(target);
            if (distance <= POSITION_TOLERANCE) deltaMovementBuffer.removeLast();
        }
    }
}
