package com.dave.astronomer.common.ashley.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.dave.astronomer.common.ashley.utils.ImmutableArray;
import lombok.Getter;
class SystemManager {
	private Array<EntitySystem> systems = new Array<EntitySystem>(true, 16);
    private ImmutableArray<EntitySystem> immutableSystems = new ImmutableArray<EntitySystem>(systems);

    @Getter private ObjectSet<EntitySystem> prioritySystems = new ObjectSet<>(16);
	private ObjectMap<Class<?>, EntitySystem> systemsByClass = new ObjectMap<Class<?>, EntitySystem>();
	private SystemListener listener;

	public SystemManager(SystemListener listener) {
		this.listener = listener;
	}

    public boolean isPrioritySystem(EntitySystem entitySystem) {
        return prioritySystems.contains(entitySystem);
    }
    public void addPrioritySystem(EntitySystem system) {
        if (prioritySystems.contains(system)) {
            prioritySystems.remove(system);
        }

        prioritySystems.add(system);

        addSystem(system);
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
        if (prioritySystems.contains(system)) {
            prioritySystems.remove(system);
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
