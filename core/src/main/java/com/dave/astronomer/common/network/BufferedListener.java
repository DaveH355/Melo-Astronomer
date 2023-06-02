package com.dave.astronomer.common.network;

import com.badlogic.gdx.utils.SnapshotArray;
import com.dave.astronomer.common.network.packet.Packet;
import com.dave.astronomer.common.network.packet.PacketHandler;
import com.dave.astronomer.server.ServerGamePacketHandler;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import java.util.Map;

public class BufferedListener implements Listener {
    private final SnapshotArray<Packet<?>> buffer = new SnapshotArray<>(true, 16);
    private ServerGamePacketHandler serverGamePacketHandler;
    public int packetsDown = 0;

    public BufferedListener(ServerGamePacketHandler serverGamePacketHandler) {
        this.serverGamePacketHandler = serverGamePacketHandler;

    }
    public BufferedListener() {
    }

    @Override
    public void connected(Connection c) {
        if (c instanceof PlayerConnection connection && serverGamePacketHandler != null) {
            serverGamePacketHandler.onConnection(connection);
        }
    }

    @Override
    public void disconnected(Connection c) {
        if (serverGamePacketHandler != null) {
            serverGamePacketHandler.onDisconnection((PlayerConnection) c);
        }
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof FrameworkMessage) {
            packetsDown++;
            return;
        }

        if (!(object instanceof Packet<?> packet)) {
            Log.warn(object + " is not a known packet");
            return;
        }
        packetsDown++;

        packet.sender = connection;
        buffer.add(packet);
    }
    public void processPacketBuffer(Map<Class<? extends PacketHandler>, PacketHandler> map) {
        while (!buffer.isEmpty()) {
            Packet<?> packet = buffer.pop();
            PacketHandler handler = map.get(Packet.resolveHandler(packet));

            handle(packet, handler);

        }
    }
    @SuppressWarnings("unchecked")
    private static <T extends PacketHandler> void handle(Packet<T> packet, PacketHandler handler) {
        if (handler == null) {
            Log.error(packet.getClass().getName() + " has no handler");
            return;
        }
        packet.handle((T)handler);

    }
}
