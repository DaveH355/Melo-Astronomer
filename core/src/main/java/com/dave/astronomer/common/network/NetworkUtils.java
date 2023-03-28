package com.dave.astronomer.common.network;

import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.common.network.discovery.LanDiscoveryDatagram;
import com.dave.astronomer.common.network.packet.Packet;
import com.dave.astronomer.common.world.entity.Player;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import net.jodah.typetools.TypeResolver;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class NetworkUtils {
    private NetworkUtils(){}
    public static final int TCP_PORT = 42590;
    public static final int UDP_PORT = 42591;
    public static final int BROADCAST_PORT = 5037;
    public static final String BROADCAST_ADDRESS = "224.0.1.25";

    private static Map<Class<?>, Class<?>> map = new HashMap<>();

    //get the generic parameter for all packets
    //needs to be done because of type erasure
    static {
        Set<Class<? extends Packet>> set = getSetFor(Packet.class.getPackageName(), Packet.class);

        for (Class<? extends Packet> packetClass : set) {
            Class<?> type = TypeResolver.resolveRawArgument(Packet.class, packetClass);
            map.put(packetClass, type);
        }
    }

    public static Class<?> getHandlerTypeFromPacket(Packet<?> packet) {
        return map.get(packet.getClass());
    }


    public static void registerAll(EndPoint endPoint) {
        Set<Class<? extends Packet>> packets = getSetFor(Packet.class.getPackageName(), Packet.class);

        Kryo kryo = endPoint.getKryo();

        packets.forEach(kryo::register);

        registerMisc(kryo);
    }
    public static void registerMisc(Kryo kryo) {
        kryo.register(Class.class);
        kryo.register(UUID.class, new UUIDSerializer());
        kryo.register(Vector2.class);
        kryo.register(Player.State.class);
        kryo.register(LanDiscoveryDatagram.class);
    }
    private static <T> Set<Class<? extends T>> getSetFor(String packageName, Class<T> type) {
        Reflections reflections = new Reflections(packageName, Scanners.SubTypes.filterResultsBy(c -> true));
        return reflections.getSubTypesOf(type);
    }



}
