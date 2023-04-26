package com.dave.astronomer.client.world;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.client.GameState;
import com.dave.astronomer.client.world.entity.AbstractClientPlayer;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.common.DeltaTimer;
import com.dave.astronomer.common.ashley.core.Engine;
import com.dave.astronomer.common.data.PlayerData;
import com.dave.astronomer.common.network.packet.ServerboundPlayerUpdateStatePacket;
import com.dave.astronomer.common.world.SingleEntitySystem;
import com.dave.astronomer.common.world.entity.Player;

import java.util.concurrent.TimeUnit;


public class MainPlayerSystem extends SingleEntitySystem<MainPlayer> {

    private DeltaTimer timer = new DeltaTimer(50, TimeUnit.MILLISECONDS);

    public MainPlayerSystem() {

    }

    @Override
    public void removedFromEngine(Engine engine) {

    }

    @Override
    public void processEntity(MainPlayer p, float delta) {
        Vector2 velocity = new Vector2();

        if (p.getWalkRightKey().isDown()) velocity.x += 1;
        if (p.getWalkLeftKey().isDown()) velocity.x -= 1;
        if (p.getWalkUpKey().isDown()) velocity.y += 1;
        if (p.getWalkDownKey().isDown()) velocity.y -= 1;

        velocity.nor();
        velocity.scl(PlayerData.METERS_PER_SEC);


        p.getBody().setLinearVelocity(velocity);

        Sprite sprite = p.getSpriteComponent().getSprite();
        sprite.setPosition(p.getPosition().x, p.getPosition().y);
        sprite.setRotation(p.getBody().getAngle() * MathUtils.radiansToDegrees);



        if (timer.update(delta)) {
            sendUpdateToServer(p.captureState());
        }


        determineAnimation(p);


    }

    public static void determineAnimation(AbstractClientPlayer p) {
        Vector2 velocity = p.getExactVelocity();
        String animation = "idle";

        if (velocity.x > 0) {
            animation = "walk";
        }
        if (velocity.x < 0) {
            animation = "walk";
        }
        if (velocity.y < 0) {
            animation = "walk";
        }
        if (velocity.y > 0) {
            animation = "walk";
        }

        p.getSpriteComponent().selectAnimationIfAbsent(animation, true);
    }
    private void sendUpdateToServer(Player.State state) {
        ServerboundPlayerUpdateStatePacket packet = new ServerboundPlayerUpdateStatePacket(state);

        GameState.getInstance().getClient().sendUDP(packet);
    }

    @Override
    public Class<MainPlayer> getGenericType() {
        return MainPlayer.class;
    }


}
