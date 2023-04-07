package com.dave.astronomer.server.system;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.dave.astronomer.common.PhysicsUtils;
import com.dave.astronomer.common.PolledTimer;
import com.dave.astronomer.common.data.PlayerData;
import com.dave.astronomer.common.network.PlayerConnection;
import com.dave.astronomer.common.network.packet.ClientboundPlayerForceStatePacket;
import com.dave.astronomer.common.network.packet.ClientboundUpdateEntityPosPacket;
import com.dave.astronomer.common.world.SingleEntitySystem;
import com.dave.astronomer.common.world.entity.Player;
import com.dave.astronomer.server.MAServer;
import com.dave.astronomer.server.entity.ServerPlayer;
import com.esotericsoftware.minlog.Log;

import java.util.concurrent.TimeUnit;

public class ServerPlayerSystem extends SingleEntitySystem<ServerPlayer> {
    private static final float POSITION_TOLERANCE = 0.5f;
    private PolledTimer timer = new PolledTimer(50, TimeUnit.MILLISECONDS);
    private MAServer server;
    public ServerPlayerSystem(MAServer server) {
        this.server = server;
    }

    @Override
    public void update(float deltaTime) {
        if (timer.update()) super.update(deltaTime);
    }

    @Override
    public void processEntity(ServerPlayer player, float delta) {
        float maxSpeed = PlayerData.METERS_PER_SEC;
        Body body = player.getBody();

        if (player.getClientState().isEmpty()) return;
        Vector2 current = player.getPosition();
        Vector2 target = player.getClientState().getLast().position;

        Vector2 velocity = PhysicsUtils.velocityToPosition(body, target, maxSpeed);

        //stop velocity at a threshold to prevent oscillation at the cost of some accuracy
        if (current.dst(target) < 0.05f) {
            velocity.set(0, 0);
        }

        body.setLinearVelocity(velocity);

        if (Math.abs(target.x - player.getPosition().x) <= POSITION_TOLERANCE &&
            Math.abs(target.y - player.getPosition().y) <= POSITION_TOLERANCE) {
            validateState(player);

        }


        PlayerConnection connection = player.getConnection();
        ClientboundUpdateEntityPosPacket packet = new ClientboundUpdateEntityPosPacket(player);


        server.sendToAllExceptUDP(connection.getID(), packet);

    }
    private void validateState(ServerPlayer player) {
        boolean flag = false;

        //validation begin
        Player.State clientState = player.getClientState().getLast();
        Player.State serverState = player.getServerState().getLast();


        if (clientState.id != serverState.id) {
            flag = true;
            Log.warn("Player " + player.getUuid() + " state buffer out of order");
        }

        if (!validatePosition(clientState, serverState)) {
            flag = true;
        }
        //validation end

        if (flag) {
            player.setState(serverState);

            ClientboundPlayerForceStatePacket packet = new ClientboundPlayerForceStatePacket(serverState);
            player.getConnection().sendTCP(packet);

            player.getClientState().clear();
            player.getServerState().clear();
        } else {
            //remove last state, it has been validated
            player.getClientState().removeLast();
            player.getServerState().removeLast();
        }
    }
    private boolean validatePosition(Player.State clientState, Player.State serverState) {
        float xDiff = Math.abs(serverState.position.x - clientState.position.x);
        float yDiff = Math.abs(serverState.position.y - clientState.position.y);


        if (xDiff > POSITION_TOLERANCE || yDiff > POSITION_TOLERANCE) {
            return false;
        }
        return true;
    }

    @Override
    public Class<ServerPlayer> getGenericType() {
        return ServerPlayer.class;
    }
}
