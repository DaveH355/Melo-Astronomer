package com.dave.astronomer.common.network.packet;

import com.dave.astronomer.server.ServerGamePacketHandler;

public class ServerboundUseItemPacket extends Packet<ServerGamePacketHandler> {

    //TODO: this is a temp field
    public float targetAngleRad;

    @Override
    public void handle(ServerGamePacketHandler handler) {
        handler.onUseItem(this);
    }
}
