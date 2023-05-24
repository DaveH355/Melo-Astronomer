package com.dave.astronomer.server.system;


import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.common.DeltaTimer;
import com.dave.astronomer.common.network.PlayerConnection;
import com.dave.astronomer.common.network.packet.ClientboundEntityForceStatePacket;
import com.dave.astronomer.common.network.packet.ClientboundUpdateEntityPosPacket;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.SingleEntitySystem;
import com.dave.astronomer.server.MAServer;
import com.dave.astronomer.server.ServerEntityWrapper;
import com.esotericsoftware.minlog.Log;

import java.util.Deque;
import java.util.concurrent.TimeUnit;

import static com.dave.astronomer.common.world.BaseEntity.State;

public class EntityStateValidationSystem extends SingleEntitySystem<ServerEntityWrapper> {
    private static final float POSITION_TOLERANCE = 0.5f;
    private DeltaTimer timer = new DeltaTimer(50, TimeUnit.MILLISECONDS);
    private MAServer server;
    public EntityStateValidationSystem(MAServer server) {
        this.server = server;
    }

    @Override
    public void update(float deltaTime) {
        if (timer.update(deltaTime)) super.update(deltaTime);
    }

    @Override
    public void processEntity(ServerEntityWrapper entityWrapper, float delta) {
        BaseEntity serverEntity = entityWrapper.getServerEntity();


        if (entityWrapper.getLatestClientPos() == null) return;;

        Vector2 serverPos = serverEntity.getPosition();
        Vector2 clientPos = entityWrapper.getLatestClientPos();


        //if the client state buffer gets too large it may be network throttling
        if (serverPos.dst(clientPos) > POSITION_TOLERANCE) {
            invalidateState(entityWrapper);

        }
        boolean trustClient = serverPos.dst(clientPos) <= POSITION_TOLERANCE;


        ClientboundUpdateEntityPosPacket packet = new ClientboundUpdateEntityPosPacket();
        if (trustClient) {
            packet.position = clientPos;
        } else {
            packet.position = serverEntity.getPosition();
        }

        packet.uuid = serverEntity.getUuid();

        PlayerConnection connection = entityWrapper.getConnection();
        if (connection != null) {
            server.sendToAllExceptUDP(connection.getID(), packet);
        } else {
            server.sendToAllUDP(packet);
        }
    }
    private void invalidateState(ServerEntityWrapper wrapper) {
        State serverState = wrapper.getServerEntity().captureState();

        ClientboundEntityForceStatePacket packet = new ClientboundEntityForceStatePacket(serverState);
        server.sendToAllTCP(packet);

    }
    private boolean validatePosition(State clientState, State serverState) {
        return serverState.position.dst(clientState.position) <= POSITION_TOLERANCE;
    }

    @Override
    public Class<ServerEntityWrapper> getGenericType() {
        return ServerEntityWrapper.class;
    }
}
