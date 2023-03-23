package com.dave.astronomer.common.network.packet;

import com.dave.astronomer.common.network.PacketHandler;
import com.esotericsoftware.kryonet.Connection;



public abstract class Packet<T extends PacketHandler> {
    public Connection sender;

    public abstract void handle(T handler);
}
