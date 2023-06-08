package com.dave.astronomer.common.network.packet;

import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.client.multiplayer.ClientGamePacketHandler;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientboundAddEntityPacket extends Packet<ClientGamePacketHandler> {

    public UUID uuid;
    public Vector2 position;
    public float angleRad = 0;
    public EntityType<?> entityType;
    private Map<String, Object> dataMap = new HashMap<>();

    public ClientboundAddEntityPacket(BaseEntity baseEntity) {
        this.uuid = baseEntity.getUuid();
        this.position = baseEntity.getPosition();
        this.angleRad = baseEntity.getBody().getAngle();
        this.entityType = baseEntity.getEntityType();
    }
    public ClientboundAddEntityPacket() {

    }

    public void setData(String s, Object object) {
        dataMap.put(s, object);
    }
    public <T> T getData(String s) {
        return (T) dataMap.get(s);
    }

    @Override
    public void handle(ClientGamePacketHandler handler) {
        handler.onAddEntity(this);
    }
}
