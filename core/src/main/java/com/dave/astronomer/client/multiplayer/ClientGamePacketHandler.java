package com.dave.astronomer.client.multiplayer;

import com.dave.astronomer.client.GameState;
import com.dave.astronomer.client.MAClient;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.client.world.entity.RemotePlayer;
import com.dave.astronomer.common.network.packet.*;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.EntityType;
import com.esotericsoftware.minlog.Log;


public class ClientGamePacketHandler implements PacketHandler {
    private MAClient client;
    private CoreEngine engine;

    public ClientGamePacketHandler(CoreEngine engine, MAClient client) {
        this.client = client;
        this.engine = engine;

    }

    public void onConfirmLogin(ClientboundConfirmLoginPacket packet) {
        client.setReadyForGame(true);
    }

    public void onRemoveEntity(ClientboundRemoveEntityPacket packet) {
        engine.removeEntity(packet.uuid);
    }

    public void onAddEntity(ClientboundAddEntityPacket packet) {
        EntityType<?> entityType = packet.entityType;
        BaseEntity entity = entityType.create(this.engine);
        if (entity != null) {
            entity.recreateFromPacket(packet);
            this.engine.addEntity(entity);
        }
    }

    public void onAddMainPlayer(ClientboundAddMainPlayerPacket packet) {
        //main player
        MainPlayer player = new MainPlayer(engine, packet.uuid);
        player.forcePosition(packet.position,0);

        engine.addEntity(player);
        GameState.getInstance().setMainPlayer(player);
    }

    public void onAddPlayer(ClientboundAddPlayerPacket packet) {
        RemotePlayer remotePlayer = new RemotePlayer(engine, packet.uuid);
        remotePlayer.forcePosition(packet.position, 0);

        engine.addEntity(remotePlayer);
    }
    public void onUpdateEntityPos(ClientboundMoveEntityPacket packet) {
        BaseEntity entity = engine.getEntityByUUID(packet.uuid);
        if (entity == null) return;

        entity.lerpPosition(packet.position, packet.speed);
    }

    public void onForcePosRot(ClientboundForceEntityPosRotPacket packet) {
        BaseEntity entity = engine.getEntityByUUID(packet.uuid);
        entity.forcePosition(packet.position, packet.angleRad);
        Log.debug("Entity position and rotation forced");
    }

}
