/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.dave.astronomer.common.ashley.core;

import com.dave.astronomer.common.ashley.utils.ImmutableArray;

/**
 * A simple {@link EntitySystem} that processes a {@link Family} of entities not once per frame, but after a given interval.
 * Entity processing logic should be placed in {@link IntervalIteratingSystem#processEntity(Entity)}.
 * @author David Saltares
 */
public abstract class IntervalIteratingSystem extends IntervalSystem {
	private Family family;
	private ImmutableArray<Entity> entities;



	/**
	 * @param family represents the collection of family the system should process
	 * @param interval time in seconds between calls to {@link IntervalIteratingSystem#updateInterval()}.
	 */
	public IntervalIteratingSystem (Family family, float interval) {
		super(interval);
		this.family = family;
	}

    @Override
    protected void addedToEngineInternal(Engine engine) {
        if (family.isOptInOnly()) {
            family.setSystemType(this.getClass());
        }

        entities = engine.getEntitiesFor(family);

        super.addedToEngineInternal(engine);
    }

    @Override
	protected void updateInterval () {
		startProcessing();
		for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);

            boolean process = true;
            if (family.isOptInOnly()) {
                process = entity.getOptInSystems().contains(this.getClass(), true);
            }
            if (process) {
                processEntity(entity);

            }
		}
		endProcessing();
	}

	/**
	 * @return set of entities processed by the system
	 */
	public ImmutableArray<Entity> getEntities () {
		return entities;
	}

    /**
	 * @return the Family used when the system was created
	 */
	public Family getFamily () {
		return family;
	}

	/**
	 * The user should place the entity processing logic here.
	 * @param entity
	 */
	protected abstract void processEntity (Entity entity);

	/**
	 * This method is called once on every update call of the EntitySystem, before entity processing begins. Override this method to
	 * implement your specific startup conditions.
	 */
	public void startProcessing() {}

	/**
	 * This method is called once on every update call of the EntitySystem after entity processing is complete. Override this method to
	 * implement your specific end conditions.
	 */
	public void endProcessing() {}
}
