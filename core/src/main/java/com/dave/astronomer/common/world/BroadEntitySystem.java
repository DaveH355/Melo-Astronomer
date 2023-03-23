package com.dave.astronomer.common.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.ObjectSet;
import com.esotericsoftware.minlog.Log;
import lombok.Getter;

import java.util.List;


public abstract class BroadEntitySystem<T extends BaseEntity> extends EntitySystem {

    private ObjectSet<Class<?>> blackList = new ObjectSet<>();
    boolean validated = false;
    @Getter
    private CoreEngine engine;
    @Override
    public void addedToEngine(Engine engine) {
        this.engine = (CoreEngine) engine;

    }

    @Override
    public void update(float deltaTime) {
        if (!validated) {
            validate();
            validated = true;
        }

        List<T> list = engine.getEntitiesByTypeBroad(getGenericType());
        for (T t : list) {
            if (blackList.contains(t.getClass())) continue;

            processEntity(t, deltaTime);
        }
    }

    public abstract void processEntity(T entity, float delta);

    public abstract Class<T> getGenericType();
    //check if any single entity systems manage entities that are subclasses of the entities this system manages
    //if so exclude processing of said entity from this system.
    private void validate() {
        for (Class<?> aClass : getEngine().getSingleEntityList()) {
            if (getGenericType().isAssignableFrom(aClass)) {
                Log.debug(getGenericType().getSimpleName() + " (broad) system excluding " + aClass.getSimpleName() + " because it belongs to a single entity system");
                blackList.add(aClass);
            }
        }
    }




}
