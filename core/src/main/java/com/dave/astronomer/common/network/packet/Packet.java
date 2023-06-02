package com.dave.astronomer.common.network.packet;

import com.esotericsoftware.kryonet.Connection;
import net.jodah.typetools.TypeResolver;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Packet<T extends PacketHandler> {
    public Connection sender;
    private static Map<Class<? extends Packet>, Class<? extends PacketHandler>> map = new HashMap<>();

    //get the generic parameter for all packets
    //needs to be done because of type erasure
    static {
        Reflections reflections = new Reflections(Packet.class, Scanners.SubTypes.filterResultsBy(c -> true));
        Set<Class<? extends Packet>> set = reflections.getSubTypesOf(Packet.class);

        for (Class<? extends Packet> packetClass : set) {
            Class<?> handlerType = TypeResolver.resolveRawArgument(Packet.class, packetClass);
            map.put(packetClass, (Class<? extends PacketHandler>) handlerType);
        }
    }

    public abstract void handle(T handler);

    public static Class<? extends PacketHandler> resolveHandler(Packet<?> packet) {
        return map.get(packet.getClass());
    }

}
