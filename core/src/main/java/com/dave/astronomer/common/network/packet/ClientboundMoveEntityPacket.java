package com.dave.astronomer.common.network.packet;

import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.client.multiplayer.ClientGamePacketHandler;

import java.util.UUID;

public class ClientboundMoveEntityPacket extends Packet<ClientGamePacketHandler> {
    public Vector2 position;
    public float speed;
    public UUID uuid;
    public ClientboundMoveEntityPacket() {}

    @Override
    public void handle(ClientGamePacketHandler handler) {
        handler.onUpdateEntityPos(this);
    }
}
