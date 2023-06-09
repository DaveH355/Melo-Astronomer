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
 * A simple EntitySystem that iterates over each entity and calls processEntity() for each entity every time the EntitySystem is
 * updated. This is really just a convenience class as most systems iterate over a list of entities.
 * @author Stefan Bachmann
 */
public abstract class IteratingSystem extends EntitySystem {
	private Family family;
	private ImmutableArray<Entity> entities;

	/**
	 * Instantiates a system that will iterate over the entities described by the Family.
	 * @param family The family of entities iterated over in this System
	 */
	public IteratingSystem (Family family) {
        this.family = family;

	}



    @Override
    protected boolean addedToEngineInternal(Engine engine) {
        boolean success = super.addedToEngineInternal(engine);
        if (success) {
            if (family.isOptInOnly()) {
                family.setSystemType(this.getClass());
            }
            entities = engine.getEntitiesFor(family);
        }

        return success;
    }


    @Override
    protected boolean removedFromEngineInternal(Engine engine) {
        boolean success = super.addedToEngineInternal(engine);
        if (success) {
            entities = null;
        }

        return success;
    }

    @Override
	public void update (float deltaTime) {
		startProcessing();
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);

            boolean process = true;
            if (family.isOptInOnly()) {
                process = entity.getOptInSystems().contains(this.getClass(), true);
            }
            if (process) {
                processEntity(entity, deltaTime);

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
	 * This method is called on every entity on every update call of the EntitySystem. Override this to implement your system's
	 * specific processing.
	 * @param entity The current Entity being processed
	 * @param deltaTime The delta time between the last and current frame
	 */
	protected abstract void processEntity (Entity entity, float deltaTime);

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
