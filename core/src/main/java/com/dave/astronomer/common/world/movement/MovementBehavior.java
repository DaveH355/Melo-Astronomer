package com.dave.astronomer.common.world.movement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.dave.astronomer.common.PhysicsUtils;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.PhysicsSystem;

public class MovementBehavior {
    public static final MovementBehavior CUSTOM = new MovementBehavior();

    public void apply(BaseEntity entity) {

    }

    public static class BasicLerp extends MovementBehavior {

        @Override
        public void apply(BaseEntity entity) {
            Body body = entity.getBody();

            Vector2 target = entity.getDeltaMovement();
            if (target == null) return;


            Vector2 velocity = PhysicsUtils.velocityToPosition(body, target, entity.getDeltaSpeed());
            body.setLinearVelocity(velocity);
        }
    }
}
