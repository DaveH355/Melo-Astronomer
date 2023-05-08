package com.dave.astronomer.common.network.packet;

import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.client.multiplayer.ClientGamePacketHandler;

import java.util.UUID;

public class ClientboundAddEntityPacket extends Packet<ClientGamePacketHandler> {

    public UUID uuid;
    public Vector2 position;
    public float angleRad = 0;


    @Override
    public void handle(ClientGamePacketHandler handler) {
        handler.onAddPlayer(this);
    }
}
