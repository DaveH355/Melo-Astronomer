package com.dave.astronomer.common.world;


import com.badlogic.gdx.utils.Disposable;
import com.dave.astronomer.common.ashley.core.Engine;
import com.dave.astronomer.common.ashley.core.Entity;
import com.dave.astronomer.common.ashley.core.EntitySystem;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.SingleEntitySystem;
import com.esotericsoftware.minlog.Log;
import lombok.Getter;
import org.reflections.ReflectionUtils;

import java.util.*;

public class CoreEngine extends Engine implements Disposable {

    //get entity by uuid
    private Map<UUID, BaseEntity> uuidToEntity = new HashMap<>();
    //get all entities by exact class type
    private final Map<Class<?>, List<BaseEntity>> entitiesByType = new HashMap<>();


    private Map<Class<? extends EntitySystem>,EntitySystem> prioritySystems = new HashMap<>();

    private List<BaseEntity> removeQueue = new ArrayList<>();

    public void addSystems(EntitySystem... systems) {
        for (EntitySystem system : systems) {
            addSystem(system);
        }
    }
    public void addPrioritySystems(EntitySystem... systems) {
        for (EntitySystem system : systems) {
            prioritySystems.put(system.getClass(), system);
        }

    }

    @Override
    public void addSystem(EntitySystem system) {
        super.addSystem(system);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getEntitiesByType(Class<T> type) {
        return (List<T>) entitiesByType.getOrDefault(type, Collections.emptyList());

    }
    public BaseEntity getEntityByUUID(UUID id) {
        return uuidToEntity.get(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends EntitySystem> T getSystem(Class<T> systemType) {
        if (prioritySystems.containsKey(systemType)) {
            return (T) prioritySystems.get(systemType);
        }
        return super.getSystem(systemType);
    }

    @Override
    public void addEntity(Entity e) {
        super.addEntity(e);

        if (e instanceof BaseEntity entity) {

            uuidToEntity.put(entity.getUuid(), entity);


            Class<?> type = e.getClass();

            List<BaseEntity> list = entitiesByType.computeIfAbsent(type, k -> new ArrayList<>());
            list.add(entity);
        }

    }

    @Override
    public void update(float delta) {
        for (BaseEntity entity : removeQueue) {
            internalRemove(entity);
        }
        removeQueue.clear();

        for (EntitySystem system : prioritySystems.values()){
            system.update(delta);
        }
        for (List<BaseEntity> list : entitiesByType.values()) {
            for (BaseEntity baseEntity : list) {
                baseEntity.update(delta);
            }
        }

        super.update(delta);
    }

    @Override
    public void removeEntity(Entity e) {
        if (e instanceof BaseEntity baseEntity) {
            queueEntityRemove(baseEntity);
        }
    }


    public void removeEntity(UUID uuid) {
        BaseEntity entity = getEntityByUUID(uuid);
        queueEntityRemove(entity);
    }
    private void queueEntityRemove(BaseEntity entity) {
        removeQueue.add(entity);
    }
    private void internalRemove(BaseEntity entity) {
        super.removeEntity(entity);
        uuidToEntity.remove(entity.getUuid(), entity);

        entitiesByType.get(entity.getClass()).remove(entity);

        dispose(entity);

    }


    @Override
    public void dispose() {
        dispose(getEntities());
        dispose(getSystems());
        dispose(prioritySystems);
    }
    private <T> void dispose(Iterable<T> iterable) {
        for (T t : iterable) {
            dispose(t);
        }
    }
    private void dispose(Object o) {
        if (o instanceof Disposable disposable) {
            disposable.dispose();
            Log.debug(o.getClass().getSimpleName() + " disposed");
        }
    }
}
