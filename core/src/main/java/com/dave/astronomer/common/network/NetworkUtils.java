package com.dave.astronomer.common.network;

import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.common.network.datagram.LanDiscoveryDatagram;
import com.dave.astronomer.common.network.packet.Packet;
import com.dave.astronomer.common.world.EntityType;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.serialization.KryoSerialization;
import com.esotericsoftware.minlog.Log;
import lombok.Getter;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;


public class NetworkUtils {
    private NetworkUtils(){}
    public static final int DATAGRAM_BUFFER_SIZE = 1024;
    public static final int TCP_PORT = 42590;
    public static final int UDP_PORT = 42591;
    public static final int BROADCAST_PORT = 5037;
    //multicast broadcast address
    public static final String BROADCAST_ADDRESS = "224.0.1.25";
    @Getter
    private static final KryoSerialization serialization = register(new KryoSerialization());

    /**
        Accepts only {@link EndPoint} or {@link KryoSerialization}
     */
    public static <T> T register(T t) {
        Reflections reflections = new Reflections(Packet.class, Scanners.SubTypes.filterResultsBy(c -> true));
        Set<Class<? extends Packet>> packets = reflections.getSubTypesOf(Packet.class);

        Kryo kryo;

        if (t instanceof EndPoint endPoint) {
            kryo = endPoint.getKryo();
        } else if (t instanceof KryoSerialization serialization) {
            kryo = serialization.getKryo();
        } else {
            Log.warn("Couldn't register kryo to network");
            return t;
        }

        packets.forEach(kryo::register);

        registerMisc(kryo);
        return t;
    }
    private static void registerMisc(Kryo kryo) {
        kryo.register(Class.class);
        kryo.register(UUID.class, new UUIDSerializer());
        kryo.register(Vector2.class);
        kryo.register(HashMap.class);

        kryo.register(LanDiscoveryDatagram.class);
        kryo.register(EntityType.class, new EntityType.EntityTypeSerializer());
    }
}
