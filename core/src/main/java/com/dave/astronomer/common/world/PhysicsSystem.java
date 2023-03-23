package com.dave.astronomer.common.world;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import lombok.Getter;

//TODO: handle collision here
public class PhysicsSystem extends EntitySystem implements Disposable {
    @Getter private final World world = new World(Vector2.Zero, true);
    private float accumulator;
    public static final int STEP_FREQUENCY = 240;
    public static final float TIME_STEP = 1f / STEP_FREQUENCY;

    @Override
    public void update(float delta) {
        float frameTime = Math.min(delta, 0.25f);
        accumulator += frameTime;

        while (accumulator > TIME_STEP) {
            world.step(TIME_STEP, 8, 3);
            accumulator -= TIME_STEP;
        }
    }


    @Override
    public void dispose() {
        world.dispose();
    }
}
