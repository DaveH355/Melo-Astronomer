package com.dave.astronomer.common.network;

import com.dave.astronomer.server.entity.ServerPlayer;
import com.esotericsoftware.kryonet.Connection;

public class PlayerConnection extends Connection {
    public ServerPlayer serverPlayer;
}
