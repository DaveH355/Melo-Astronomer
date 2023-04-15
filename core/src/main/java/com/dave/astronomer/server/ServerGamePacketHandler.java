package com.dave.astronomer.server;

import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.common.network.PacketHandler;
import com.dave.astronomer.common.network.PlayerConnection;
import com.dave.astronomer.common.network.packet.*;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.ecs.CoreEngine;
import com.dave.astronomer.common.world.entity.Player;
import com.dave.astronomer.server.entity.ServerPlayer;
import com.esotericsoftware.minlog.Log;

import java.util.List;

public class ServerGamePacketHandler implements PacketHandler {

    private CoreEngine engine;
    private MAServer server;

    public ServerGamePacketHandler(CoreEngine engine, MAServer server) {
        this.engine = engine;
        this.server = server;
    }
    public void onConnection(PlayerConnection connection) {
        Log.debug("Connection from  " + connection.getRemoteAddressTCP());
    }

    public void onDisconnection(PlayerConnection connection) {
        BaseEntity entity = engine.getEntityByUUID(connection.uuid);

        if (entity != null) {
            engine.removeEntity(entity);

            ClientboundRemoveEntityPacket packet = new ClientboundRemoveEntityPacket();
            packet.uuid = entity.getUuid();

            server.sendToAllExceptTCP(connection.getID(), packet);
        }
    }

    public void onUpdatePlayerState(ServerboundPlayerUpdateStatePacket packet) {

        Player.State clientState = packet.state;


        BaseEntity entity = engine.getEntityByUUID(clientState.uuid);
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        //save server and client state for later comparison
        Player.State serverState = player.captureState(clientState.id);



        player.getServerState().push(serverState);
        player.getClientState().push(clientState);

    }

    public void onHello(ServerboundHelloPacket packet) {
        PlayerConnection connection = ((PlayerConnection) packet.sender);

        if (engine.getEntityByUUID(connection.uuid) != null) {
            Log.warn("Unexpected: " + packet + " from client");
            return;
        }

        ServerPlayer newPlayer = createMainPlayer(connection);

        //send client itself
        ClientboundAddMainPlayerPacket mainPlayerPacket = new ClientboundAddMainPlayerPacket();
        mainPlayerPacket.position = newPlayer.getPosition();
        mainPlayerPacket.uuid = newPlayer.getUuid();

        connection.sendTCP(mainPlayerPacket);



        List<ServerPlayer> list = engine.getEntitiesByType(ServerPlayer.class);

        for (ServerPlayer p : list) {
            //tell existing players about new guy
            ClientboundAddPlayerPacket newPlayerPacket = new ClientboundAddPlayerPacket();
            newPlayerPacket.position = newPlayer.getPosition();
            newPlayerPacket.uuid = newPlayer.getUuid();

            p.getConnection().sendTCP(newPlayerPacket);


            //tell new guy about existing players
            ClientboundAddPlayerPacket existingPlayerPacket = new ClientboundAddPlayerPacket();
            existingPlayerPacket.position = p.getPosition();
            existingPlayerPacket.uuid = p.getUuid();

            connection.sendTCP(existingPlayerPacket);
        }

        engine.addEntity(newPlayer);



        connection.sendTCP(new ClientboundConfirmLoginPacket());

    }

    private ServerPlayer createMainPlayer(PlayerConnection connection) {

        //TODO: load pos from save file or default spawn location if player is new
        Vector2 position = new Vector2(25, 20);

        ServerPlayer player = new ServerPlayer(engine, connection);
        player.forcePosition(position, 0);

        return player;
    }
}
