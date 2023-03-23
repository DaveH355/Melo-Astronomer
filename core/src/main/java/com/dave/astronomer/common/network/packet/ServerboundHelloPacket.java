package com.dave.astronomer.common.network.packet;


import com.dave.astronomer.server.ServerGamePacketHandler;

public class ServerboundHelloPacket extends Packet<ServerGamePacketHandler> {

    public String message = "";


    @Override
    public void handle(ServerGamePacketHandler handler) {
        handler.onHello(this);
    }

}
