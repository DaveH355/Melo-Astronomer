package com.dave.astronomer.common.ashley.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.dave.astronomer.common.ashley.utils.ImmutableArray;
import lombok.Getter;

import java.util.Comparator;

class SystemManager {
	private Array<EntitySystem> systems = new Array<EntitySystem>(true, 16);
    @Getter private Array<EntitySystem> prioritySystems = new Array<>(true, 16);
	private ImmutableArray<EntitySystem> immutableSystems = new ImmutableArray<EntitySystem>(systems);
	private ObjectMap<Class<?>, EntitySystem> systemsByClass = new ObjectMap<Class<?>, EntitySystem>();
	private SystemListener listener;

	public SystemManager(SystemListener listener) {
		this.listener = listener;
	}

    public void addPrioritySystem(EntitySystem system) {
        if (prioritySystems.contains(system, true)) {
            prioritySystems.removeValue(system, true);
        }

        prioritySystems.add(system);

        listener.systemAdded(system);
    }

	public void addSystem(EntitySystem system){
		Class<? extends EntitySystem> systemType = system.getClass();
		EntitySystem oldSystem = getSystem(systemType);

		if (oldSystem != null) {
			removeSystem(oldSystem);
		}

		systems.add(system);
		systemsByClass.put(systemType, system);

		listener.systemAdded(system);
	}

	public void removeSystem(EntitySystem system){
        if (prioritySystems.contains(system, true)) {
            prioritySystems.removeValue(system, true);
        }

		if(systems.removeValue(system, true)) {
			systemsByClass.remove(system.getClass());
		}
        listener.systemRemoved(system);
	}

	public void removeAllSystems() {
		while(systems.size > 0) {
			removeSystem(systems.first());
		}
        while (prioritySystems.size > 0) {
            removeSystem(prioritySystems.first());
        }
	}

	@SuppressWarnings("unchecked")
	public <T extends EntitySystem> T getSystem(Class<T> systemType) {
		return (T) systemsByClass.get(systemType);
	}

	public ImmutableArray<EntitySystem> getSystems() {
		return immutableSystems;
	}



	interface SystemListener {
		void systemAdded(EntitySystem system);
		void systemRemoved(EntitySystem system);
	}
}
