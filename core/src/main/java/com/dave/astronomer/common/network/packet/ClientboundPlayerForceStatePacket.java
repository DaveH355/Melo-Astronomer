package com.dave.astronomer.common.network.packet;


import com.dave.astronomer.client.multiplayer.ClientGamePacketHandler;
import com.dave.astronomer.common.world.entity.Player;

public class ClientboundPlayerForceStatePacket extends Packet<ClientGamePacketHandler> {
    public Player.State state;
    public ClientboundPlayerForceStatePacket(){}
    public ClientboundPlayerForceStatePacket(Player.State state) {
        this.state = state;
    }

    @Override
    public void handle(ClientGamePacketHandler handler) {
        handler.onForceState(this);
    }
}
