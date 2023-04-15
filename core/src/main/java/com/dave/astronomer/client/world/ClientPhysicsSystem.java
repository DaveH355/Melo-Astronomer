package com.dave.astronomer.client.world;


import com.badlogic.ashley.core.Engine;
import com.dave.astronomer.common.world.ecs.CoreEngine;
import com.dave.astronomer.common.world.PhysicsSystem;


public class ClientPhysicsSystem extends PhysicsSystem {
    private CoreEngine engine;

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = (CoreEngine) engine;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

    }


}
