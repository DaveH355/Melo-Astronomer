package com.dave.astronomer.client;

import com.dave.astronomer.common.network.NetworkUtils;
import com.esotericsoftware.kryonet.Client;

public class NonGameClient extends Client {
    public NonGameClient() {
        NetworkUtils.registerAll(this);

    }
}
