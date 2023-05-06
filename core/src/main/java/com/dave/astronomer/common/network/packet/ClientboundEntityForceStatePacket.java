package com.dave.astronomer.common.network.packet;


import com.dave.astronomer.client.multiplayer.ClientGamePacketHandler;
import com.dave.astronomer.common.world.BaseEntity;

public class ClientboundEntityForceStatePacket extends Packet<ClientGamePacketHandler> {
    public BaseEntity.State state;
    public ClientboundEntityForceStatePacket(){}
    public ClientboundEntityForceStatePacket(BaseEntity.State state) {
        this.state = state;
    }

    @Override
    public void handle(ClientGamePacketHandler handler) {
        handler.onForceState(this);
    }
}
