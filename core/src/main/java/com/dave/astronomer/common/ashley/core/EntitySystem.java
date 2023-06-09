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

/**
 * Abstract class for processing sets of {@link Entity} objects.
 * @author Stefan Bachmann
 */
public abstract class EntitySystem {
	private Engine engine;

	public EntitySystem () {
	}



	/**
	 * Called when this EntitySystem is added to an {@link Engine}.
	 * @param engine The {@link Engine} this system was added to.
	 */
	public void addedToEngine (Engine engine) {
	}

	/**
	 * Called when this EntitySystem is removed from an {@link Engine}.
	 * @param engine The {@link Engine} the system was removed from.
	 */
	public void removedFromEngine (Engine engine) {
	}

	/**
	 * The update method called every tick.
	 * @param deltaTime The time passed since last frame in seconds.
	 */
	public void update (float deltaTime) {
	}



	/** @return engine instance the system is registered to.
	 * It will be null if the system is not associated to any engine instance. */
	public Engine getEngine () {
		return engine;
	}

	protected boolean addedToEngineInternal(Engine engine) {
        if (this.engine == null) {
            this.engine = engine;
            addedToEngine(engine);
            return true;
        }
        return false;
	}

	protected boolean removedFromEngineInternal(Engine engine) {
        if (this.engine == engine) {
            this.engine = null;
            removedFromEngine(engine);
            return true;
        }
        return false;
	}
}
