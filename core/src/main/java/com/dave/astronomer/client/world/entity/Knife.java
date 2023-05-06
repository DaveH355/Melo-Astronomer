package com.dave.astronomer.client.world.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.asset.AssetManagerResolving;
import com.dave.astronomer.client.world.component.SpriteComponent;
import com.dave.astronomer.common.Constants;
import com.dave.astronomer.common.PhysicsUtils;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.EntityType;
import com.dave.astronomer.common.world.PhysicsSystem;

public class Knife extends BaseEntity {

    private Body body;
    private SpriteComponent spriteComponent;
    public float angleDeg;

    public int bounces = 0;
    boolean applied = false;

    public float targetAngleRad = -1;
    public Knife(CoreEngine engine) {
        super(EntityType.KNIFE, engine);

        spriteComponent = createSpriteComponent();
        body = createBody();

        addComponents(
            spriteComponent
        );
    }


    @Override
    public void update(float delta) {

        if (bounces > 3) {
            getEngine().removeEntity(this);
            return;
        }


        float angleRad = MathUtils.degreesToRadians * angleDeg;

        float speed = getEntityType().speed;
        float velocityX = speed * MathUtils.cos(angleRad);
        float velocityY = speed * MathUtils.sin(angleRad);

        Vector2 velocity = new Vector2(velocityX, velocityY);
        if (!applied) {
            body.setLinearVelocity(velocity);
            applied = true;
        }
        if (targetAngleRad != -1) {
            body.setAngularVelocity(PhysicsUtils.angularVelocityToAngle(body, targetAngleRad, 10, delta));
        }


        spriteComponent.getSprite().setPosition(getPosition().x, getPosition().y);
        spriteComponent.getSprite().setRotation(body.getAngle() * MathUtils.radiansToDegrees);


    }

    @Override
    public Body getBody() {
        return body;
    }
    private SpriteComponent createSpriteComponent() {
        AssetManagerResolving assetManager = MeloAstronomer.getInstance().getAssetManager();

        Texture texture = assetManager.get("knife.png", Texture.class);


        Sprite sprite = new Sprite(texture);

        sprite.setBounds(0, 0, sprite.getWidth() / Constants.PIXELS_PER_METER, sprite.getHeight() / Constants.PIXELS_PER_METER);
        SpriteComponent spriteComponent = new SpriteComponent(texture);
        spriteComponent.setSprite(sprite);

        return spriteComponent;
    }

    private Body createBody() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.angularDamping = 0.25f;

        World world = getEngine().getSystem(PhysicsSystem.class).getWorld();
        Body b = world.createBody(bdef);
        b.setUserData(this);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 2500;
        fixtureDef.restitution = 0.75f;
        fixtureDef.friction = 1;

        Rectangle rect = PhysicsUtils.traceRectangle(spriteComponent.getSprite());


        PolygonShape shape = PhysicsUtils.toShape(rect);
        fixtureDef.shape = shape;



        b.createFixture(fixtureDef);
        shape.dispose();

        PhysicsUtils.centerSprite(spriteComponent.getSprite(), b);
        return b;
    }
}
