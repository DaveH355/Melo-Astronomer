package com.dave.astronomer.client.world;



import com.dave.astronomer.common.ashley.core.Engine;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.PhysicsSystem;


public class ClientPhysicsSystem extends PhysicsSystem {
    private CoreEngine engine;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.engine = (CoreEngine) engine;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

    }


}
