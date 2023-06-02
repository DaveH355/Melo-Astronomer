package com.dave.astronomer.client.multiplayer;

import com.badlogic.gdx.utils.Disposable;
import com.dave.astronomer.common.network.NetworkUtils;
import com.dave.astronomer.common.network.datagram.LanDiscoveryDatagram;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.minlog.Log;
import lombok.Getter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LanServerDetector extends Thread implements Disposable {
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    @Getter private final List<LanServer> serverList = new ArrayList<>();
    private final InetAddress pingGroup;
    private final MulticastSocket socket;
    private Input input = new Input();

    public boolean dirty = false;


    public LanServerDetector() throws IOException {
        super("LanServerDetector #" + UNIQUE_THREAD_ID.incrementAndGet());

        this.socket = new MulticastSocket(NetworkUtils.BROADCAST_PORT);
        this.socket.setSoTimeout(5000);

        this.pingGroup = InetAddress.getByName(NetworkUtils.BROADCAST_ADDRESS);

        this.socket.joinGroup(this.pingGroup);

        setDaemon(true);
        Log.debug("Multicast Receiver running at: " + socket.getLocalSocketAddress());
    }

    public void run() {

        while(!isInterrupted()) {
            byte[] buffer = new byte[NetworkUtils.DATAGRAM_BUFFER_SIZE];
            DatagramPacket data = new DatagramPacket(buffer, buffer.length);
            try {
                this.socket.receive(data);
            } catch (SocketTimeoutException sockettimeoutexception) {
                continue;
            } catch (IOException e) {
                Log.error("Couldn't ping server: " + e.getMessage());
                break;
            }

            input.setBuffer(buffer);

            LanDiscoveryDatagram packet = (LanDiscoveryDatagram) NetworkUtils.getSerialization()
                .read(null, ByteBuffer.wrap(buffer));

            LanServer lanServer = new LanServer();
            lanServer.serverInfo = packet.serverInfo;
            lanServer.serverName = packet.serverName;
            lanServer.address = data.getAddress();


            updateList(lanServer);
        }

    }

    private void updateList(LanServer server) {
        boolean isNewServer = true;

        for (LanServer s : serverList) {
            if (s.equals(server)) {
                isNewServer = false;
                //TODO: ping server
            }
        }

        if (isNewServer) {
            serverList.add(server);
            dirty = true;
        }
    }


    @Override
    public void dispose() {
        interrupt();
        input.close();
        try {
            socket.leaveGroup(this.pingGroup);
        } catch (IOException ioexception) {
        }

        this.socket.close();
    }
}
