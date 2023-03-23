package com.dave.astronomer.common.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public abstract class BaseEntity extends Entity {


    @Getter @Setter private UUID uuid = UUID.randomUUID();
    @Getter private final CoreEngine engine;
    @Getter private final EntityType<?> entityType;
    @Getter private Vector2 deltaMovement = Vector2.Zero;

    @Getter @Setter private Vector2 velocity;

    public void lerpPosition(float x, float y) {
        deltaMovement = new Vector2(x, y);
    }

    public BaseEntity(EntityType<?> entityType, CoreEngine engine) {
        this.engine = engine;
        this.entityType = entityType;

    }

    public abstract Vector2 getPosition();



    public void addComponents(BaseComponent... components) {
        for (BaseComponent component : components) {
            add(component);
        }
    }

}
