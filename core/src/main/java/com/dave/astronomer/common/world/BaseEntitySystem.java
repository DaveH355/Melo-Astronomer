package com.dave.astronomer.common.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import lombok.Getter;

public abstract class BaseEntitySystem extends EntitySystem {
    private Family family;
    @Getter private CoreEngine engine;

    public BaseEntitySystem(Family family) {
        this.family = family;
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = (CoreEngine) engine;
    }

    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> entities = engine.getEntitiesFor(family);

        for (Entity entity : entities) {
            processEntity((BaseEntity) entity, deltaTime);
        }
    }

    public abstract void processEntity(BaseEntity entity, float deltaTime);
}
