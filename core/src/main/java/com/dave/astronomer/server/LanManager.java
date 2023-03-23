package com.dave.astronomer.server;

import com.badlogic.gdx.utils.Disposable;
import com.dave.astronomer.common.PolledTimer;
import com.dave.astronomer.common.network.NetworkUtils;
import com.dave.astronomer.common.network.discovery.LanDiscoveryDatagram;
import com.esotericsoftware.kryonet.serialization.Serialization;
import com.esotericsoftware.minlog.Log;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class LanManager implements Disposable {
    private DatagramSocket socket;
    private InetAddress address;
    private Serialization serialization;
    @Getter @Setter private boolean openToLan = true;
    private PolledTimer timer = new PolledTimer(2, TimeUnit.SECONDS);

    public LanManager(MAServer server) throws IOException {

        serialization = server.getSerialization();


        socket = new DatagramSocket();

        address = InetAddress.getByName(NetworkUtils.BROADCAST_ADDRESS);
    }

    public void broadcast() {
        if (!timer.update()) return;


        LanDiscoveryDatagram data = new LanDiscoveryDatagram();
        data.serverInfo = "X & Y";
        data.serverName = "Dave's Server :>";

        ByteBuffer buffer = ByteBuffer.allocate(512);
        serialization.write(null, buffer, data);
        buffer.flip();

        DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.limit());
        packet.setAddress(address);
        packet.setPort(NetworkUtils.BROADCAST_PORT);


        try {
            socket.send(packet);


        } catch (IOException e) {
            Log.error("", e);
        }



    }

    @Override
    public void dispose() {
        socket.close();
    }
}
