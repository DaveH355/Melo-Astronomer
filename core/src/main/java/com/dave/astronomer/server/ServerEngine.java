package com.dave.astronomer.server;

import com.dave.astronomer.common.ashley.core.EntitySystem;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.MockableSystem;


public class ServerEngine extends CoreEngine {

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
}
