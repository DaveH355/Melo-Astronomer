package com.dave.astronomer.server;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.dave.astronomer.common.ashley.core.Entity;
import com.dave.astronomer.common.ashley.core.EntitySystem;
import com.dave.astronomer.common.ashley.utils.ImmutableArray;
import com.dave.astronomer.common.network.PlayerConnection;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.MockableSystem;

import java.util.UUID;


public class ServerEngine extends CoreEngine {

    private ObjectMap<UUID, ServerEntityWrapper> entityWrappersByUUID = new ObjectMap<>();
    private Array<ServerEntityWrapper> entityWrappers = new Array<>(false, 16);
    private ImmutableArray<ServerEntityWrapper> immutableWrappers = new ImmutableArray<>(entityWrappers);

    public ServerEngine(CoreEngine.EngineMetaData clientEngineMetaData) {
        for (EntitySystem prioritySystem : clientEngineMetaData.prioritySystems) {
            if (prioritySystem instanceof MockableSystem) {
                addPrioritySystems(prioritySystem);
            }
        }
        for (EntitySystem system : clientEngineMetaData.systems) {
            if (system instanceof MockableSystem) {
                addSystems(system);
            }
        }
    }

    @Override
    public boolean shouldProcess(EntitySystem system) {
        return !(system instanceof MockableSystem);
    }
    @Override
    protected void removeEntityInternal(Entity entity) {
        super.removeEntityInternal(entity);
        if (entity instanceof BaseEntity baseEntity) {
            entityWrappers.removeValue(entityWrappersByUUID.get(baseEntity.getUuid()), true);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Entity> ImmutableArray<T> getEntitiesByType(Class<T> type) {
        if (type == ServerEntityWrapper.class) {
            return (ImmutableArray<T>) immutableWrappers;
        }
        return super.getEntitiesByType(type);
    }

    public ServerEntityWrapper getEntityWrapper(UUID uuid) {
        return entityWrappersByUUID.get(uuid);
    }

    private void internalAddWrapper(ServerEntityWrapper entityWrapper) {
        entityWrappers.add(entityWrapper);
        entityWrappersByUUID.put(entityWrapper.getServerEntity().getUuid(), entityWrapper);
    }

    @Override
    public void addEntity(Entity e) {
        super.addEntity(e);
        if (e instanceof BaseEntity baseEntity) {
            ServerEntityWrapper entityWrapper = new ServerEntityWrapper(baseEntity);
            internalAddWrapper(entityWrapper);
        }

    }

    public void addEntity(Entity e, PlayerConnection connection) {
        super.addEntity(e);
        if (e instanceof BaseEntity baseEntity) {
            ServerEntityWrapper entityWrapper = new ServerEntityWrapper(baseEntity, connection);
            internalAddWrapper(entityWrapper);
        }
    }
}
