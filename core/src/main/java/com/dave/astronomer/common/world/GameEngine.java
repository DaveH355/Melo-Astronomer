package com.dave.astronomer.common.world;

import com.badlogic.ashley.core.EntitySystem;
import com.dave.astronomer.client.world.SpriteRenderSystem;
import com.dave.astronomer.common.world.ecs.CoreEngine;
import lombok.Getter;

public class GameEngine extends CoreEngine {
    @Getter private SpriteRenderSystem spriteRenderSystem = registerSystem(new SpriteRenderSystem());
    @Getter private PhysicsSystem physicsSystem = registerSystem(new PhysicsSystem());


    private <T extends EntitySystem> T registerSystem(T t) {
        addSystem(t);
        return t;
    }
    private <T extends EntitySystem> T registerPrioritySystem(T t) {
        addPrioritySystems(t);
        return t;
    }

}
