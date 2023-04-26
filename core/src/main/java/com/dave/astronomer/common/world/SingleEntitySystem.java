package com.dave.astronomer.common.world;


import com.dave.astronomer.common.ashley.core.Engine;
import com.dave.astronomer.common.ashley.core.EntitySystem;
import lombok.Getter;

import java.util.List;

public abstract class SingleEntitySystem<T extends BaseEntity> extends EntitySystem {
    @Getter
    private CoreEngine engine;

    public SingleEntitySystem() {
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = (CoreEngine) engine;

    }

    @Override
    public void update(float deltaTime) {
        List<T> list = engine.getEntitiesByType(getGenericType());
        for (T t : list) {
            processEntity(t, deltaTime);
        }
    }

    public abstract void processEntity(T entity, float delta);

    public abstract Class<T> getGenericType();

}
