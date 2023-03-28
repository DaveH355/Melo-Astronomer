package com.dave.astronomer.client.world;

import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.client.world.entity.RemotePlayer;
import com.dave.astronomer.common.PhysicsUtils;
import com.dave.astronomer.common.data.PlayerData;
import com.dave.astronomer.common.world.SingleEntitySystem;

public class RemotePlayerSystem extends SingleEntitySystem<RemotePlayer> {
    @Override
    public void processEntity(RemotePlayer player, float delta) {


        Vector2 position = player.getPosition();
        Vector2 targetPosition = player.getDeltaMovement();

        if (Math.abs(position.x - targetPosition.x) < 0.01f && Math.abs(position.y - targetPosition.y) < 0.01f) {
            player.setVelocity(new Vector2(0, 0));
        } else {
            float maxSpeed = PlayerData.METERS_PER_SEC;
            Vector2 requiredVelocity = PhysicsUtils.calculateVelocityToPosition(targetPosition, maxSpeed, delta, player.getBody());

            player.setVelocity(requiredVelocity);
        }

        float xDiff = Math.abs(position.x - targetPosition.x);
        float yDiff = Math.abs(position.y - targetPosition.y);



        if (xDiff > 2 || yDiff > 2) {
            player.forcePosition(targetPosition, 0);
        }



        player.getSpriteComponent().getSprite().setPosition(position.x, position.y);


        MainPlayerSystem.determineAnimation(player);
    }

    @Override
    public Class<RemotePlayer> getGenericType() {
        return RemotePlayer.class;
    }
}
