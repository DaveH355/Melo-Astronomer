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

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ServerPlayerSystem extends SingleEntitySystem<ServerPlayer> {
    private static final float POSITION_TOLERANCE = 1f;
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
        Vector2 target = player.getClientPosition();
        if (target.y == 0) System.out.println("oh no");

        Vector2 velocity = PhysicsUtils.calculateVelocityToPosition(target, maxSpeed, delta, body);
        body.setLinearVelocity(velocity);


        validateState(player);



        PlayerConnection connection = player.getConnection();
        ClientboundUpdateEntityPosPacket packet = new ClientboundUpdateEntityPosPacket(player);


        server.sendToAllExceptUDP(connection.getID(), packet);




    }
    private void validateState(ServerPlayer player) {
        //make sure client and server state all match
        Player.State latest = null;
        boolean flag = false;
        for (Map.Entry<Integer, Player.State> entry : player.getClientState().entrySet()) {
            int id = entry.getKey();
            Player.State clientState = entry.getValue();
            Player.State serverState = player.getServerState().get(id);

            //set the latest state
            if (latest == null) latest = serverState;
            if (serverState.captureDateMillis > latest.captureDateMillis) {
                latest = serverState;
            }

            if (!validatePosition(clientState, serverState)) {
                flag = true;
            }

        }

        if (flag) {
            player.setState(latest);

            ClientboundPlayerForceStatePacket packet = new ClientboundPlayerForceStatePacket(latest);
            player.getConnection().sendTCP(packet);
        }
        //clear states, they have been validated
        player.getClientState().clear();
        player.getServerState().clear();
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
