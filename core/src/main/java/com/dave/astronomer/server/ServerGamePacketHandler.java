package com.dave.astronomer.server;

import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.client.world.entity.Knife;
import com.dave.astronomer.common.ashley.utils.ImmutableArray;
import com.dave.astronomer.common.network.PlayerConnection;
import com.dave.astronomer.common.network.packet.*;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.server.entity.ServerPlayer;
import com.esotericsoftware.minlog.Log;

import java.util.UUID;

import static com.dave.astronomer.common.world.BaseEntity.State;


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
        BaseEntity entity = engine.getEntityByUUID(connection.uuid);

        if (entity != null) {
            engine.removeEntity(entity);

            ClientboundRemoveEntityPacket packet = new ClientboundRemoveEntityPacket();
            packet.uuid = entity.getUuid();

            server.sendToAllExceptTCP(connection.getID(), packet);
        }
    }

    //very hacky
    public void onUseItem(ServerboundUseItemPacket packet) {
        PlayerConnection connection = (PlayerConnection) packet.sender;
        BaseEntity entity = engine.getEntityByUUID(connection.uuid);
        ServerPlayer player = ((ServerPlayer) entity);
        Knife knife = player.throwKnife(packet.targetAngleRad);

        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(knife);
        server.sendToAllExceptTCP(connection.getID(), addEntityPacket);
    }


    public void onUpdateEntityState(ServerboundEntityUpdateStatePacket packet) {
        UUID uuid = packet.state.uuid;
        BaseEntity entity = engine.getEntityByUUID(uuid);
        if (entity == null) return;

        Vector2 clientPos = packet.state.position;
        entity.lerpPosition(clientPos);


        ServerEntityWrapper wrapper = engine.getEntityWrapper(uuid);
        wrapper.latestClientPos = clientPos;


    }

    public void onHello(ServerboundHelloPacket packet) {
        PlayerConnection connection = (PlayerConnection) packet.sender;

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

        engine.addEntity(newPlayer, connection);



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
