package com.dave.astronomer.common.world.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.asset.AssetFinder;
import com.dave.astronomer.client.world.component.SpriteComponent;
import com.dave.astronomer.common.Constants;
import com.dave.astronomer.common.DeltaTimer;
import com.dave.astronomer.common.PhysicsUtils;
import com.dave.astronomer.common.VectorUtils;
import com.dave.astronomer.common.network.packet.ClientboundAddEntityPacket;
import com.dave.astronomer.common.network.packet.Packet;
import com.dave.astronomer.common.world.*;
import com.dave.astronomer.common.world.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Knife extends BaseEntity {

    private Body body;
    private SpriteComponent spriteComponent;
    public int bounces = 0;
    boolean velocityApplied = false;
    public float targetAngleRad;

    private BaseEntity owner;
    private boolean leftOwner = false;
    private DeltaTimer checkTimer = new DeltaTimer(25, TimeUnit.MILLISECONDS);

    public Knife(EntityType<?> type, CoreEngine engine) {
        super(type, engine);

        spriteComponent = createSpriteComponent();
        body = createBody();

        addComponents(
            spriteComponent
        );
    }

    public Knife(CoreEngine engine, Vector2 position, float targetAngleRad, BaseEntity owner)  {
        this(EntityType.KNIFE, engine);
        this.targetAngleRad = targetAngleRad;
        this.owner = owner;

        forcePosition(position, targetAngleRad);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(this);
        packet.setData("owner_uuid", owner.getUuid());
        return packet;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.targetAngleRad = packet.angleRad;

        UUID uuid = packet.getData("owner_uuid");
        this.owner = getEngine().getEntityByUUID(uuid);

    }
    private boolean checkLeftOwner() {
        Vector2 lower = new Vector2();
        Vector2 upper = new Vector2();

        PhysicsUtils.calculateBodyAABB(body, lower, upper);

        AtomicBoolean bodyInsideOwner = new AtomicBoolean(false);
        body.getWorld().QueryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                //stop searching
                if (fixture.getBody() == owner.getBody()) {
                    bodyInsideOwner.set(true);
                    return false;
                }
                //continue searching
                return true;
            }
        }, lower.x, lower.y, upper.x, upper.y);

        return !bodyInsideOwner.get();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (bounces > 3) {
            getEngine().removeEntity(this);
            return;
        }

        if (!leftOwner && checkTimer.update(delta)) {
            leftOwner = checkLeftOwner();
        }



        float speed = getEntityType().speed;
        float velocityX = speed * MathUtils.cos(targetAngleRad);
        float velocityY = speed * MathUtils.sin(targetAngleRad);

        Vector2 velocity = new Vector2(velocityX, velocityY);
        if (!velocityApplied) {
            body.setLinearVelocity(velocity);
            velocityApplied = true;
        }



        body.setAngularVelocity(PhysicsUtils.angularVelocityToAngle(body, targetAngleRad, getEntityType().speed));



        spriteComponent.getSprite().setPosition(getPosition().x, getPosition().y);
        spriteComponent.getSprite().setRotation(body.getAngle() * MathUtils.radiansToDegrees);


    }

    //Remember! This code runs on server side and client side
    //TODO: server side collision detection only
    @Override
    public void beginCollision(CollisionContact contact, BaseEntity entity) {

        if (contact.getOtherBody().getType() == BodyDef.BodyType.StaticBody) {
            Vector2 newVelocity = VectorUtils.reflectVector(this.body.getLinearVelocity(), contact.getContactNormal(), 0.8f);

            this.body.setLinearVelocity(newVelocity);
            this.targetAngleRad = MathUtils.degreesToRadians * newVelocity.angleDeg();
            this.bounces++;

            return;
        }

        if (entity instanceof Player player && contact.getOtherFixture().isSensor() && leftOwner) {
            player.hurt();
            getEngine().removeEntity(this);
        }


    }

    @Override
    public Body getBody() {
        return body;
    }
    private SpriteComponent createSpriteComponent() {
        AssetFinder assetFinder = MeloAstronomer.getInstance().getAssetFinder();

        Texture texture = assetFinder.get("knife.png", Texture.class);


        Sprite sprite = new Sprite(texture);

        sprite.setBounds(0, 0, sprite.getWidth() / Constants.PIXELS_PER_METER, sprite.getHeight() / Constants.PIXELS_PER_METER);
        SpriteComponent spriteComponent = new SpriteComponent(texture);
        spriteComponent.setSprite(sprite);

        return spriteComponent;
    }

    private Body createBody() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;

        World world = getEngine().getSystem(PhysicsSystem.class).getWorld();
        Body b = world.createBody(bdef);
        b.setUserData(this);

        Rectangle rect = PhysicsUtils.traceRectangle(spriteComponent.getSprite());
        PolygonShape shape = PhysicsUtils.toShape(rect);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 150;
        fixtureDef.restitution = 0.75f;



        b.createFixture(fixtureDef);
        shape.dispose();

        PhysicsUtils.centerSprite(spriteComponent.getSprite(), b);
        return b;
    }
}
