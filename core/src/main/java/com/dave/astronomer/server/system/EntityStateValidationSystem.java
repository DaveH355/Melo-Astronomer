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
    private static final int BUFFER_SIZE_TOLERANCE = 10;
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

        Deque<State> clientBuffer = entityWrapper.getClientStateBuffer();
        Deque<State> serverBuffer = entityWrapper.getServerStateBuffer();
        if (clientBuffer.isEmpty() || serverBuffer.isEmpty()) return;

        Vector2 serverPos = serverBuffer.getLast().position;
        Vector2 clientPos = clientBuffer.getLast().position;

        serverEntity.lerpPosition(clientPos);

        //if the client state buffer gets too large it may be network throttling
        if (serverPos.dst(clientPos) <= POSITION_TOLERANCE || clientBuffer.size() > BUFFER_SIZE_TOLERANCE) {
            validateState(entityWrapper);
        }
        boolean trustClient = serverPos.dst(clientPos) <= POSITION_TOLERANCE;

        //TODO: better way to handle large buffer sizes
        if (clientBuffer.size() > BUFFER_SIZE_TOLERANCE) {
            Log.warn(String.format("Entity state buffer too large (%d)", clientBuffer.size()));
        }

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
    private void validateState(ServerEntityWrapper wrapper) {
        boolean flag = false;

        //validation begin
        Deque<State> clientBuffer = wrapper.getClientStateBuffer();
        Deque<State> serverBuffer = wrapper.getServerStateBuffer();

        State clientState = clientBuffer.getLast();
        State serverState = serverBuffer.getLast();


        if (clientState.id != serverState.id) {
            flag = true;
            Log.warn("Entity " + wrapper.getServerEntity().getUuid() + " state buffer out of order");
        }

        if (!validatePosition(clientState, serverState)) {
            flag = true;
        }
        //validation end

        if (flag) {
            invalidateState(wrapper);
        } else {
            //remove last state, it has been validated
            clientBuffer.removeLast();
            serverBuffer.removeLast();
        }
    }
    private void invalidateState(ServerEntityWrapper wrapper) {
        State serverState = wrapper.getServerStateBuffer().getFirst();
        wrapper.getServerEntity().forceState(serverState);

        ClientboundEntityForceStatePacket packet = new ClientboundEntityForceStatePacket(serverState);
        server.sendToAllTCP(packet);

        wrapper.getServerStateBuffer().clear();
        wrapper.getClientStateBuffer().clear();
    }
    private boolean validatePosition(State clientState, State serverState) {
        return serverState.position.dst(clientState.position) <= POSITION_TOLERANCE;
    }

    @Override
    public Class<ServerEntityWrapper> getGenericType() {
        return ServerEntityWrapper.class;
    }
}
