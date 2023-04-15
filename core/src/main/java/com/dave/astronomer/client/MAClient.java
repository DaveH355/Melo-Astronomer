package com.dave.astronomer.client;


import com.dave.astronomer.client.multiplayer.ClientGamePacketHandler;
import com.dave.astronomer.common.PolledTimer;
import com.dave.astronomer.common.network.BufferedListener;
import com.dave.astronomer.common.network.NetworkUtils;
import com.dave.astronomer.common.network.PacketHandler;
import com.dave.astronomer.common.network.packet.ServerboundHelloPacket;
import com.dave.astronomer.common.world.ecs.CoreEngine;
import com.esotericsoftware.kryonet.Client;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MAClient extends Client {
    private BufferedListener bufferedListener = new BufferedListener();
    @Getter @Setter private boolean isReadyForGame = false;
    private Map<Class<? extends PacketHandler>, PacketHandler> handlers = new HashMap<>();
    private PolledTimer timer = new PolledTimer(1, TimeUnit.SECONDS);
    private int rawPacketsUp = 0;
    private int rawBytesUp = 0;
    public int packetsDownPerSec = 0;
    public int packetsUpPerSec = 0;
    public int bytesUpPerSec = 0;


    public MAClient(CoreEngine engine) {
        NetworkUtils.registerAll(this);

        addListener(bufferedListener);
        addHandler(new ClientGamePacketHandler(engine, this));
    }
    public void requestGameStart() {
        ServerboundHelloPacket helloPacket = new ServerboundHelloPacket();
        helloPacket.message = "Hello from the client!";


        sendTCP(helloPacket);
    }
    private void addHandler(PacketHandler handler) {
        handlers.put(handler.getClass(), handler);
    }

    public void update() {
        bufferedListener.processPacketBuffer(handlers);


        if (timer.update()) {
            updateReturnTripTime();

            packetsUpPerSec = rawPacketsUp;
            packetsDownPerSec = bufferedListener.packetsDown;
            bytesUpPerSec = rawBytesUp;

            rawPacketsUp = 0;
            bufferedListener.packetsDown = 0;
            rawBytesUp = 0;
        }
    }


    @Override
    public int sendTCP(Object object) {
        rawPacketsUp++;
        int bytes = super.sendTCP(object);
        rawBytesUp += bytes;

        return bytes;
    }

    @Override
    public int sendUDP(Object object) {
        rawPacketsUp++;
        int bytes = super.sendUDP(object);
        rawBytesUp += bytes;

        return bytes;
    }
}
