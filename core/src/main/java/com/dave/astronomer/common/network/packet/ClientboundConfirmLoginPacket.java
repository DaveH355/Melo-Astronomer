package com.dave.astronomer.common.network.packet;


import com.dave.astronomer.client.multiplayer.ClientGamePacketHandler;

public class ClientboundConfirmLoginPacket extends Packet<ClientGamePacketHandler> {

    @Override
    public void handle(ClientGamePacketHandler handler) {
        handler.onConfirmLogin(this);
    }
}
