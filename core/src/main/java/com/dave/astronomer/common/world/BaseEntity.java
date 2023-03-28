package com.dave.astronomer.common.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public abstract class BaseEntity extends Entity {
    @Getter @Setter private UUID uuid = UUID.randomUUID();
    @Getter private final CoreEngine engine;
    @Getter private final EntityType<?> entityType;
    @Getter private Vector2 deltaMovement = new Vector2(0, 0);

    @Getter private Vector2 velocity = new Vector2(0, 0);

    public void lerpPosition(float x, float y) {
        deltaMovement = new Vector2(x, y);
    }

    public BaseEntity(EntityType<?> entityType, CoreEngine engine) {
        this.engine = engine;
        this.entityType = entityType;
    }
    public void setVelocity(Vector2 vector2) {
        velocity.set(vector2);
    }

    public Vector2 getPosition() {
        return getBody().getPosition();
    }

    public abstract Body getBody();


    public void addComponents(BaseComponent... components) {
        for (BaseComponent component : components) {
            add(component);
        }
    }

}
