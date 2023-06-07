package com.dave.astronomer.common.world;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.dave.astronomer.client.world.entity.Knife;
import com.dave.astronomer.common.VectorUtils;
import com.dave.astronomer.common.ashley.core.EntitySystem;
import com.dave.astronomer.common.ashley.core.PooledEngine;
import lombok.Getter;


public class PhysicsSystem extends EntitySystem implements Disposable {

    @Getter private final World world = new World(Vector2.Zero, true);
    private float accumulator;
    public static final int STEP_FREQUENCY = 300;
    public static final float TIME_STEP = 1f / STEP_FREQUENCY;

    public PhysicsSystem() {
        //TODO: remove this temp contact filter
        world.setContactFilter((fixtureA, fixtureB) -> {
            if (fixtureA.isSensor() || fixtureB.isSensor()) return true;

            if (fixtureA.getBody().getUserData() != null &&
                fixtureB.getBody().getUserData() != null) {

                Body body1 = fixtureA.getBody();
                Body body2 = fixtureB.getBody();

                if (body1.getType() == BodyDef.BodyType.DynamicBody && body2.getType() == BodyDef.BodyType.DynamicBody) return false;
            }

            return true;
        });

        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if (fixtureA.getBody().getUserData() instanceof BaseEntity entityA) {
                    BaseEntity entityB = null;
                    if (fixtureB.getBody().getUserData() instanceof BaseEntity) {
                        entityB = ((BaseEntity) fixtureB.getBody().getUserData());
                    }

                    CollisionContact contactA = new CollisionContact(fixtureA, fixtureB, contact);
                    entityA.beginCollision(contactA, entityB);

                }
                if (fixtureB.getBody().getUserData() instanceof BaseEntity entityB) {
                    BaseEntity entityA = null;
                    if (fixtureA.getBody().getUserData() instanceof BaseEntity) {
                        entityA = ((BaseEntity) fixtureA.getBody().getUserData());
                    }

                    CollisionContact contactB = new CollisionContact(fixtureB, fixtureA, contact);
                    entityB.beginCollision(contactB, entityA);

                }

            }


            @Override
            public void endContact(Contact contact) {

                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if (fixtureA.getBody().getUserData() instanceof BaseEntity entityA) {
                    BaseEntity entityB = null;
                    if (fixtureB.getBody().getUserData() instanceof BaseEntity) {
                        entityB = ((BaseEntity) fixtureB.getBody().getUserData());
                    }

                    CollisionContact contactA = new CollisionContact(fixtureA, fixtureB, contact);
                    entityA.endCollision(contactA, entityB);

                }
                if (fixtureB.getBody().getUserData() instanceof BaseEntity entityB) {
                    BaseEntity entityA = null;
                    if (fixtureA.getBody().getUserData() instanceof BaseEntity) {
                        entityA = ((BaseEntity) fixtureA.getBody().getUserData());
                    }

                    CollisionContact contactB = new CollisionContact(fixtureB, fixtureA, contact);
                    entityB.endCollision(contactB, entityA);

                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    @Override
    public void update(float delta) {
        float frameTime = Math.min(delta, 0.25f);
        accumulator += frameTime;

        while (accumulator > TIME_STEP) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }
    }


    @Override
    public void dispose() {
        world.dispose();
    }
}
