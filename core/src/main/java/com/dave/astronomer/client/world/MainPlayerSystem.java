package com.dave.astronomer.client.world;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.client.GameState;
import com.dave.astronomer.client.world.entity.AbstractClientPlayer;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.common.DeltaTimer;
import com.dave.astronomer.common.network.packet.ServerboundMovePlayerPacket;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.MockableSystem;
import com.dave.astronomer.common.world.SingleEntitySystem;

import java.util.concurrent.TimeUnit;


public class MainPlayerSystem extends SingleEntitySystem<MainPlayer> implements MockableSystem {

    private DeltaTimer timer = new DeltaTimer(50, TimeUnit.MILLISECONDS);

    public MainPlayerSystem() {

    }

    @Override
    public void processEntity(MainPlayer p, float delta) {
        Vector2 velocity = new Vector2();

        float speed = p.getEntityType().speed;
        if (p.getDashKey().isDown()) speed = 5.5f;

        if (p.getWalkRightKey().isDown()) velocity.x += 1;
        if (p.getWalkLeftKey().isDown()) velocity.x -= 1;
        if (p.getWalkUpKey().isDown()) velocity.y += 1;
        if (p.getWalkDownKey().isDown()) velocity.y -= 1;

        velocity.nor();
        velocity.scl(speed);


        p.getBody().setLinearVelocity(velocity);

        Sprite sprite = p.getSpriteComponent().getSprite();
        sprite.setPosition(p.getPosition().x, p.getPosition().y);
        sprite.setRotation(p.getBody().getAngle() * MathUtils.radiansToDegrees);



        if (timer.update(delta)) {
            //send update to server
            ServerboundMovePlayerPacket packet = new ServerboundMovePlayerPacket();
            packet.position = p.getPosition();
            packet.uuid = p.getUuid();
            packet.speed = speed;

            GameState.getInstance().getClient().sendUDP(packet);

        }


        determineAnimation(p, speed);
    }

    public static void determineAnimation(AbstractClientPlayer p, float speed) {

        String animation = "idle";


        if (speed >= 5f) {
            animation = "dash";
        } else if (speed > 0.1f) {
            animation = "walk";
        }

        p.getSpriteComponent().selectAnimationIfAbsent(animation, true);
    }


    @Override
    public Class<MainPlayer> getGenericType() {
        return MainPlayer.class;
    }


}
