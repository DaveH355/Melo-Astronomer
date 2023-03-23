package com.dave.astronomer.common.network;

import com.esotericsoftware.kryonet.Connection;
import lombok.Getter;

import java.util.UUID;

public class PlayerConnection extends Connection {

    @Getter public final UUID uuid = UUID.randomUUID();

}
