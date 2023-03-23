package com.dave.astronomer.common.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Disposable;
import lombok.Getter;

import java.util.*;

public class CoreEngine extends Engine implements Disposable {

    //get entity by uuid
    private Map<UUID, BaseEntity> uuidToEntity = new HashMap<>();
    //get all entities by exact class type
    private Map<Class<?>, List<BaseEntity>> classToEntity = new HashMap<>();
    //get all entities of broad type. E.g. getting by the type Animal returns Animals and Fish
    private Map<Class<?>, List<BaseEntity>> classToEntityBroad = new HashMap<>();

    //list of entities managed by single entity systems
    //broad systems need to know, so they can exclude processing
    @Getter private List<Class<?>> singleEntityList = new ArrayList<>();

    public void addSystems(EntitySystem... systems) {
        for (EntitySystem system : systems) {
            addSystem(system);
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
        return (List<T>) classToEntity.getOrDefault(type, Collections.emptyList());

    }
    @SuppressWarnings("unchecked")
    public <T> List<T> getEntitiesByTypeBroad(Class<T> type) {
        return (List<T>) classToEntityBroad.getOrDefault(type, Collections.emptyList());
    }
    public BaseEntity getEntityByUUID(UUID id) {
        return uuidToEntity.get(id);
    }

    @Override
    public void addEntity(Entity e) {
        super.addEntity(e);

        if (e instanceof BaseEntity entity) {

            uuidToEntity.put(entity.getUuid(), entity);

            addToMapBroad(entity, classToEntityBroad);
            addToMap(entity, classToEntity);
        }

    }
    private <T> void addToMap(T object, Map<Class<?>, List<T>> map) {
        //add entity to its own list
        if (map.get(object.getClass()) == null) {
            List<T> list = new ArrayList<>();
            list.add(object);

            map.put(object.getClass(), list);
        } else {
            map.get(object.getClass()).add(object);
        }
    }
    private <T> void removeFromMap(T object, Map<Class<?>, List<T>> map) {
        if (map.get(object.getClass()) != null) {
            map.get(object.getClass()).remove(object);
        }
    }
    private <T> void addToMapBroad(T object, Map<Class<?>, List<T>> map) {
        for (Map.Entry<Class<?>, List<T>> entry : map.entrySet()) {
            if (entry.getKey().isAssignableFrom(object.getClass())) {
                entry.getValue().add(object);
            }
        }
    }
    private <T> void removeFromMapBroad(T object, Map<Class<?>, List<T>> map) {
        for (Map.Entry<Class<?>, List<T>> entry : map.entrySet()) {
            if (entry.getKey().isAssignableFrom(object.getClass())) {
                entry.getValue().remove(object);
            }
        }
    }




    @Override
    public void removeEntity(Entity e) {
        if (e instanceof BaseEntity baseEntity) {
            removeEntityAndHandle(baseEntity);
        }

    }

    public void removeEntity(UUID uuid) {
        BaseEntity entity = getEntityByUUID(uuid);
        if (entity != null) {
            removeEntityAndHandle(entity);
        }
    }
    public void removeEntityAndHandle(BaseEntity entity) {
        super.removeEntity(entity);

        uuidToEntity.remove(entity.getUuid(), entity);

        removeFromMap(entity, classToEntity);
        removeFromMapBroad(entity, classToEntityBroad);
        //TODO: save entity data if needed
        dispose(entity);
    }


    @Override
    public void dispose() {
        dispose(getEntities());
        dispose(getSystems());
    }
    private <T> void dispose(Iterable<T> iterable) {
        for (T t : iterable) {
            if (t instanceof Disposable disposable) {

                disposable.dispose();
            }
        }
    }
    private void dispose(Object o) {
        if (o instanceof Disposable disposable) {
            disposable.dispose();
        }
    }
}
