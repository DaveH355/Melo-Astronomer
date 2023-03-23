package com.dave.astronomer.common.network.packet;


import com.dave.astronomer.common.world.entity.Player;
import com.dave.astronomer.server.ServerGamePacketHandler;

public class ServerboundPlayerUpdateStatePacket extends Packet<ServerGamePacketHandler> {
    public Player.State state;

    public ServerboundPlayerUpdateStatePacket(Player.State state) {
        this.state = state;
    }

    public ServerboundPlayerUpdateStatePacket(){}
    @Override
    public void handle(ServerGamePacketHandler handler) {
        handler.onUpdatePlayerState(this);
    }
}
