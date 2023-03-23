package com.dave.astronomer.common.network.packet;



import com.dave.astronomer.client.multiplayer.ClientGamePacketHandler;

import java.util.UUID;

public class ClientboundRemoveEntityPacket extends Packet<ClientGamePacketHandler> {
    public UUID uuid;

    @Override
    public void handle(ClientGamePacketHandler handler) {
        handler.onRemoveEntity(this);
    }
}
