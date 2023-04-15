package com.dave.astronomer.common.world.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Disposable;
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
    //get all entities of broad type. E.g. getting by the type Animal returns Animals and Fish
    private final Map<Class<?>, List<BaseEntity>> entitiesByTypeBroad = new HashMap<>();

    //list of entities managed by single entity systems
    //broad systems need to know, so they can exclude processing
    @Getter private List<Class<?>> singleEntityList = new ArrayList<>();
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
        if (system instanceof SingleEntitySystem<?> single) {
            singleEntityList.add(single.getGenericType());
        }
        super.addSystem(system);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getEntitiesByType(Class<T> type) {
        return (List<T>) entitiesByType.getOrDefault(type, Collections.emptyList());

    }
    @SuppressWarnings("unchecked")
    public <T> List<T> getEntitiesByTypeBroad(Class<T> type) {
        return (List<T>) entitiesByTypeBroad.getOrDefault(type, Collections.emptyList());
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

            Set<Class<?>> set = ReflectionUtils.getAllSuperTypes(type);
            //add to broad
            for (Class<?> superType : set) {
                list = entitiesByTypeBroad.computeIfAbsent(superType, k -> new ArrayList<>());
                list.add(entity);
                Log.debug("Added " + entity.getClass().getSimpleName() + " to broad " + superType.getSimpleName());
            }
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

        //remove from broad
        for (Map.Entry<Class<?>, List<BaseEntity>> entry : entitiesByTypeBroad.entrySet()) {
            if (entry.getKey().isAssignableFrom(entry.getClass())) {
                entry.getValue().remove(entity);
                Log.debug("Removed entity " + entity.getClass().getSimpleName() + " from broad" + entry.getKey().getSimpleName());
            }
        }

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
