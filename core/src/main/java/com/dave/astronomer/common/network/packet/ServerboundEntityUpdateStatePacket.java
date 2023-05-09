package com.dave.astronomer.common.network.packet;


import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.server.ServerGamePacketHandler;

public class ServerboundEntityUpdateStatePacket extends Packet<ServerGamePacketHandler> {
    public BaseEntity.State state;

    public ServerboundEntityUpdateStatePacket(BaseEntity.State state) {
        this.state = state;
    }

    public ServerboundEntityUpdateStatePacket(){}
    @Override
    public void handle(ServerGamePacketHandler handler) {
        handler.onUpdateEntityState(this);
    }
}
