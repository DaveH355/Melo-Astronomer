package com.dave.astronomer.server.system;


import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.common.DeltaTimer;
import com.dave.astronomer.common.network.PlayerConnection;
import com.dave.astronomer.common.network.packet.ClientboundEntityForceStatePacket;
import com.dave.astronomer.common.network.packet.ClientboundMoveEntityPacket;
import com.dave.astronomer.common.world.SingleEntitySystem;
import com.dave.astronomer.server.MAServer;
import com.dave.astronomer.server.entity.ServerPlayer;

import java.util.concurrent.TimeUnit;

import static com.dave.astronomer.common.world.BaseEntity.State;

public class ServerPlayerValidationSystem extends SingleEntitySystem<ServerPlayer> {
    private static final float POSITION_TOLERANCE = 0.5f;
    private DeltaTimer timer = new DeltaTimer(50, TimeUnit.MILLISECONDS);
    private MAServer server;
    public ServerPlayerValidationSystem(MAServer server) {
        this.server = server;
    }

    @Override
    public void update(float deltaTime) {
        if (timer.update(deltaTime)) super.update(deltaTime);
    }

    @Override
    public void processEntity(ServerPlayer serverPlayer, float delta) {
        if (serverPlayer.lastestClientPosition == null) return;

        Vector2 serverPos = serverPlayer.getPosition();
        Vector2 clientPos = serverPlayer.lastestClientPosition;


        if (serverPos.dst(clientPos) > POSITION_TOLERANCE) {
            invalidateState(serverPlayer);

        }

        ClientboundMoveEntityPacket packet = new ClientboundMoveEntityPacket();
        //always tend to trust client
        packet.position = clientPos;
        packet.uuid = serverPlayer.getUuid();
        packet.speed = serverPlayer.getDeltaSpeed();

        PlayerConnection connection = serverPlayer.getConnection();
        server.sendToAllExceptUDP(connection.getID(), packet);

    }
    private void invalidateState(ServerPlayer serverPlayer) {
        State serverState = serverPlayer.captureState();

        ClientboundEntityForceStatePacket packet = new ClientboundEntityForceStatePacket(serverState);
        server.sendToAllTCP(packet);
    }

    @Override
    public Class<ServerPlayer> getGenericType() {
        return ServerPlayer.class;
    }
}
