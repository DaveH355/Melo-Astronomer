package com.dave.astronomer.client.multiplayer;

import com.dave.astronomer.client.GameState;
import com.dave.astronomer.client.MAClient;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.client.world.entity.RemotePlayer;
import com.dave.astronomer.common.network.PacketHandler;
import com.dave.astronomer.common.network.packet.*;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.entity.Player;
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
        remotePlayer.lerpPosition(packet.position.x, packet.position.y);

        engine.addEntity(remotePlayer);
    }
    public void onUpdateEntityPos(ClientboundUpdateEntityPosPacket packet) {
        BaseEntity entity = engine.getEntityByUUID(packet.uuid);
        if (entity == null) return;

        entity.lerpPosition(packet.deltaPosition.x, packet.deltaPosition.y);
    }

    public void onForceState(ClientboundPlayerForceStatePacket packet) {
        Player.State state = packet.state;

        Player player = (Player) engine.getEntityByUUID(state.uuid);
        player.setState(state);
        Log.debug("Player state forced");
    }

}
