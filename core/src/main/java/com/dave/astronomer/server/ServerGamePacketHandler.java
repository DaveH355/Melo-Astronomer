package com.dave.astronomer.server;

import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.common.world.entity.Knife;
import com.dave.astronomer.common.ashley.utils.ImmutableArray;
import com.dave.astronomer.common.network.PlayerConnection;
import com.dave.astronomer.common.network.packet.*;
import com.dave.astronomer.server.entity.ServerPlayer;
import com.esotericsoftware.minlog.Log;


public class ServerGamePacketHandler implements PacketHandler {

    private ServerEngine engine;
    private MAServer server;

    public ServerGamePacketHandler(ServerEngine engine, MAServer server) {
        this.engine = engine;
        this.server = server;
    }
    public void onConnection(PlayerConnection connection) {
        Log.debug("Connection from  " + connection.getRemoteAddressTCP());
    }

    public void onDisconnection(PlayerConnection connection) {
        engine.removeEntity(connection.serverPlayer);

        ClientboundRemoveEntityPacket packet = new ClientboundRemoveEntityPacket();
        packet.uuid = connection.serverPlayer.getUuid();

        server.sendToAllExceptTCP(connection.getID(), packet);
    }

    //very hacky
    public void onUseItem(ServerboundUseItemPacket packet) {
        PlayerConnection connection = (PlayerConnection) packet.sender;

        ServerPlayer player = connection.serverPlayer;
        Knife knife = player.throwKnife(packet.targetAngleRad);


        server.sendToAllExceptTCP(connection.getID(), knife.getAddEntityPacket());
    }


    public void onMovePlayer(ServerboundMovePlayerPacket packet) {
        PlayerConnection connection = (PlayerConnection) packet.sender;
        ServerPlayer serverPlayer = connection.serverPlayer;

        if (serverPlayer == null) return;

        Vector2 clientPos = packet.position;
        serverPlayer.lerpPosition(clientPos, packet.speed);



        serverPlayer.lastestClientPosition = clientPos;
    }

    public void onHello(ServerboundHelloPacket packet) {
        PlayerConnection connection = (PlayerConnection) packet.sender;

        if (connection.serverPlayer != null) {
            Log.warn("Unexpected: " + packet + " from client");
            return;
        }

        ServerPlayer newPlayer = createMainPlayer(connection);

        //send client itself
        ClientboundAddMainPlayerPacket mainPlayerPacket = new ClientboundAddMainPlayerPacket();
        mainPlayerPacket.position = newPlayer.getPosition();
        mainPlayerPacket.uuid = newPlayer.getUuid();

        connection.sendTCP(mainPlayerPacket);


        ImmutableArray<ServerPlayer> list = engine.getEntitiesByType(ServerPlayer.class);

        for (ServerPlayer existingPlayer : list) {
            //tell existing players about new guy
            ClientboundAddPlayerPacket newPlayerPacket = new ClientboundAddPlayerPacket();
            newPlayerPacket.position = newPlayer.getPosition();
            newPlayerPacket.uuid = newPlayer.getUuid();

            existingPlayer.getConnection().sendTCP(newPlayerPacket);


            //tell new guy about existing players
            ClientboundAddPlayerPacket existingPlayerPacket = new ClientboundAddPlayerPacket();
            existingPlayerPacket.position = existingPlayer.getPosition();
            existingPlayerPacket.uuid = existingPlayer.getUuid();

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
