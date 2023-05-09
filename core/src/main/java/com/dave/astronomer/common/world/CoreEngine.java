package com.dave.astronomer.common.world;


import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.dave.astronomer.common.ashley.core.Engine;
import com.dave.astronomer.common.ashley.core.Entity;
import com.dave.astronomer.common.ashley.core.EntitySystem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CoreEngine extends Engine implements Disposable {

    private ObjectMap<UUID, BaseEntity> entitiesByUUID = new ObjectMap<>();


    public static EngineMetaData getEngineMetaData(CoreEngine engine) {
        EngineMetaData data = new EngineMetaData();
        for (EntitySystem system : engine.getSystems()) {
            if (engine.isPrioritySystem(system)) {
                data.prioritySystems.add(system);
            } else {
                data.systems.add(system);
            }
        }
        return data;
    }
    public void addSystems(EntitySystem... systems) {
        for (EntitySystem system : systems) {
            addSystem(system);
        }
    }
    public BaseEntity getEntityByUUID(UUID id) {
        return entitiesByUUID.get(id);
    }

    @Override
    public void addEntity(Entity e) {
        super.addEntity(e);

        if (e instanceof BaseEntity entity) {
            entitiesByUUID.put(entity.getUuid(), entity);
        }

    }


    @Override
    public void removeEntity(Entity e) {
        internalRemove(e);
    }


    public void removeEntity(UUID uuid) {
        BaseEntity entity = getEntityByUUID(uuid);
        internalRemove(entity);
    }
    private void internalRemove(Entity entity) {
        super.removeEntity(entity);
        if (entity instanceof BaseEntity b) {
            entitiesByUUID.remove(b.getUuid());
        }
    }


    @Override
    public void dispose() {
        dispose(getEntities());
        dispose(getSystems());
    }
    private <T> void dispose(Iterable<T> iterable) {
        for (T t : iterable) {
            dispose(t);
        }
    }
    private void dispose(Object o) {
        if (o instanceof Disposable disposable) {
            disposable.dispose();
        }
    }

    public static class EngineMetaData {
        public final List<EntitySystem> prioritySystems = new ArrayList<>();
        public final List<EntitySystem> systems = new ArrayList<>();
    }
}
