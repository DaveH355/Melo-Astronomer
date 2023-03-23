package com.dave.astronomer.common.network.packet;

import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.client.multiplayer.ClientGamePacketHandler;
import com.dave.astronomer.common.world.BaseEntity;

import java.util.UUID;

public class ClientboundUpdateEntityPosPacket extends Packet<ClientGamePacketHandler> {
    public Vector2 deltaPosition;
    public UUID uuid;

    public ClientboundUpdateEntityPosPacket(BaseEntity entity) {
        this.deltaPosition = entity.getPosition();
        this.uuid = entity.getUuid();
    }
    public ClientboundUpdateEntityPosPacket() {}

    @Override
    public void handle(ClientGamePacketHandler handler) {
        handler.onUpdateEntityPos(this);
    }
}
