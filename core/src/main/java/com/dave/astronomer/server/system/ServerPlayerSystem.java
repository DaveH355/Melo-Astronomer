package com.dave.astronomer.server.system;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.dave.astronomer.common.DeltaTimer;
import com.dave.astronomer.common.PhysicsUtils;
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
    private static final int STATEBUFFER_TOLERANCE = 10;
    private DeltaTimer timer = new DeltaTimer(50, TimeUnit.MILLISECONDS);
    private MAServer server;
    public ServerPlayerSystem(MAServer server) {
        this.server = server;
    }

    @Override
    public void update(float deltaTime) {
        if (timer.update(deltaTime)) super.update(deltaTime);
    }

    @Override
    public void processEntity(ServerPlayer player, float delta) {
        float maxSpeed = PlayerData.METERS_PER_SEC;
        Body body = player.getBody();

        if (player.getClientState().isEmpty()) return;
        Vector2 clientPos = player.getClientState().getLast().position;


        Vector2 velocity = PhysicsUtils.velocityToPosition(body, clientPos, maxSpeed, 0.2f);
        body.setLinearVelocity(velocity);

        if (player.getPosition().dst(clientPos) <= POSITION_TOLERANCE) {
            validateState(player);
        }
        //if the client state buffer gets too large it may be network throttling or cheating
        //TODO: a way to process large buffers
        boolean trustClient = false;
        if (player.getClientState().size() < STATEBUFFER_TOLERANCE) {
            trustClient = true;
        }





        PlayerConnection connection = player.getConnection();
        ClientboundUpdateEntityPosPacket packet = new ClientboundUpdateEntityPosPacket();
        packet.position = trustClient ? clientPos : player.getPosition();
        packet.uuid = player.getUuid();


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
            invalidateState(player);
        } else {
            //remove last state, it has been validated
            player.getClientState().removeLast();
            player.getServerState().removeLast();
        }
    }
    private void invalidateState(ServerPlayer player) {
        Player.State serverState = player.getServerState().getFirst();
        player.setState(serverState);

        ClientboundPlayerForceStatePacket packet = new ClientboundPlayerForceStatePacket(serverState);
        player.getConnection().sendTCP(packet);

        player.getClientState().clear();
        player.getServerState().clear();
    }
    private boolean validatePosition(Player.State clientState, Player.State serverState) {

        if (serverState.position.dst(clientState.position) > POSITION_TOLERANCE) return false;
        return true;
    }

    @Override
    public Class<ServerPlayer> getGenericType() {
        return ServerPlayer.class;
    }
}
