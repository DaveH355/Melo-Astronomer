package com.dave.astronomer.common.network.packet;


import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.server.ServerGamePacketHandler;

import java.util.UUID;

public class ServerboundMovePlayerPacket extends Packet<ServerGamePacketHandler> {
    public float speed;
    public Vector2 position;
    public UUID uuid;


    public ServerboundMovePlayerPacket(){}
    @Override
    public void handle(ServerGamePacketHandler handler) {
        handler.onMovePlayer(this);
    }
}
