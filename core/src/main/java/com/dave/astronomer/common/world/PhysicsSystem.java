package com.dave.astronomer.common.world;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.dave.astronomer.client.world.entity.Knife;
import com.dave.astronomer.common.VectorUtils;
import com.dave.astronomer.common.ashley.core.EntitySystem;
import lombok.Getter;


public class PhysicsSystem extends EntitySystem implements Disposable {

    @Getter private final World world = new World(Vector2.Zero, true);
    private float accumulator;
    public static final int STEP_FREQUENCY = 300;
    public static final float TIME_STEP = 1f / STEP_FREQUENCY;

    public PhysicsSystem() {
        //TODO: remove this temp contact filter
        world.setContactFilter((fixtureA, fixtureB) -> {
            if (fixtureA.getBody().getUserData() != null &&
                fixtureB.getBody().getUserData() != null) {

                Body body1 = fixtureA.getBody();
                Body body2 = fixtureB.getBody();

                if (body1.getType() == BodyDef.BodyType.DynamicBody && body2.getType() == BodyDef.BodyType.DynamicBody) return false;
                if (body1.getUserData().equals("player") && body2.getUserData().equals("player")) return false;
            }

            return true;
        });

        world.setContactListener(new ContactListener() {
            //TODO: better collision handling
            @Override
            public void beginContact(Contact contact) {
                Body body1 = contact.getFixtureA().getBody();
                Body body2 = contact.getFixtureB().getBody();

                Vector2 contactNormal = contact.getWorldManifold().getNormal();

                if (body1.getUserData() instanceof Knife knife && body2.getType() == BodyDef.BodyType.StaticBody) {
                    float dot = knife.getBody().getLinearVelocity().dot(contactNormal);

                    if (dot < 0) {
                        Vector2 newVelocity = VectorUtils.reflectVector(knife.getBody().getLinearVelocity(), contactNormal, 0.8f);

                        knife.getBody().setLinearVelocity(newVelocity);
                        knife.targetAngleRad = MathUtils.degreesToRadians * newVelocity.angleDeg();
                        knife.bounces++;
                    }
                } else if (body2.getUserData() instanceof Knife knife && body1.getType() == BodyDef.BodyType.StaticBody) {

                    float dot = knife.getBody().getLinearVelocity().dot(contactNormal);

                    if (dot < 0f) {
                        Vector2 newVelocity = VectorUtils.reflectVector(knife.getBody().getLinearVelocity(), contactNormal, 0.8f);

                        knife.getBody().setLinearVelocity(newVelocity);
                        knife.targetAngleRad = MathUtils.degreesToRadians * newVelocity.angleDeg();
                        knife.bounces++;
                    }
                }
            }


            @Override
            public void endContact(Contact contact) {

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
