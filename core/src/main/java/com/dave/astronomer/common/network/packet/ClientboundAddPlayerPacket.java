package com.dave.astronomer.common.network.packet;

import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.client.multiplayer.ClientGamePacketHandler;
import com.dave.astronomer.common.world.entity.Player;

import java.util.UUID;

public class ClientboundAddPlayerPacket extends Packet<ClientGamePacketHandler> {

    public UUID uuid;
    public Vector2 position;

    public ClientboundAddPlayerPacket() {}
    public ClientboundAddPlayerPacket(Player player) {
        this.uuid = player.getUuid();
        this.position = player.getPosition();
    }
    @Override
    public void handle(ClientGamePacketHandler handler) {
        handler.onAddPlayer(this);
    }
}
