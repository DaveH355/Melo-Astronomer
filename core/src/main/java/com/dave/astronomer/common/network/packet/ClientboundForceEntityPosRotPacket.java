package com.dave.astronomer.common.network.packet;


import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.client.multiplayer.ClientGamePacketHandler;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.server.entity.ServerPlayer;

import java.util.UUID;

public class ClientboundForceEntityPosRotPacket extends Packet<ClientGamePacketHandler> {
    public Vector2 position;
    public float angleRad;
    public UUID uuid;
    public ClientboundForceEntityPosRotPacket(){}

    public ClientboundForceEntityPosRotPacket(BaseEntity entity) {
        this.position = new Vector2(entity.getPosition());
        this.angleRad = entity.getBody().getAngle();
        this.uuid = entity.getUuid();
    }


    @Override
    public void handle(ClientGamePacketHandler handler) {
        handler.onForcePosRot(this);
    }
}
