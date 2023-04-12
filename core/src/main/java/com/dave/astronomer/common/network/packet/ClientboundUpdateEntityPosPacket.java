package com.dave.astronomer.common.network.packet;

import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.client.multiplayer.ClientGamePacketHandler;

import java.util.UUID;

public class ClientboundUpdateEntityPosPacket extends Packet<ClientGamePacketHandler> {
    public Vector2 position;
    public UUID uuid;
    public ClientboundUpdateEntityPosPacket() {}

    @Override
    public void handle(ClientGamePacketHandler handler) {
        handler.onUpdateEntityPos(this);
    }
}
