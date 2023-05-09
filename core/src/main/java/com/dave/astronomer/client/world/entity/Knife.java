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
import com.dave.astronomer.common.network.packet.ClientboundAddEntityPacket;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.EntityType;
import com.dave.astronomer.common.world.PhysicsSystem;

public class Knife extends BaseEntity {

    private Body body;
    private SpriteComponent spriteComponent;
    public int bounces = 0;
    boolean applied = false;

    public float targetAngleRad;
    public Knife(EntityType<?> type, CoreEngine engine) {
        super(type, engine);

        spriteComponent = createSpriteComponent();
        body = createBody();

        addComponents(
            spriteComponent
        );
    }

    public Knife(CoreEngine engine, Vector2 position, float targetAngleRad)  {
        this(EntityType.KNIFE, engine);
        this.targetAngleRad = targetAngleRad;

        forcePosition(position, targetAngleRad);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.targetAngleRad = packet.angleRad;
    }

    @Override
    public void update(float delta) {
        if (bounces > 3) {
            getEngine().removeEntity(this);
            return;
        }

        float speed = getEntityType().speed;
        float velocityX = speed * MathUtils.cos(targetAngleRad);
        float velocityY = speed * MathUtils.sin(targetAngleRad);

        Vector2 velocity = new Vector2(velocityX, velocityY);
        if (!applied) {
            body.setLinearVelocity(velocity);
            applied = true;
        }


        body.setAngularVelocity(PhysicsUtils.angularVelocityToAngle(body, targetAngleRad, getEntityType().speed, delta));



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

        Rectangle rect = PhysicsUtils.traceRectangle(spriteComponent.getSprite());
        PolygonShape shape = PhysicsUtils.toShape(rect);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 150;
        fixtureDef.restitution = 0.75f;
        fixtureDef.friction = 0.5f;


        b.createFixture(fixtureDef);
        shape.dispose();

        PhysicsUtils.centerSprite(spriteComponent.getSprite(), b);
        return b;
    }
}
