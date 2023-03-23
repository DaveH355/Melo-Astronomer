package com.dave.astronomer.server;

import com.dave.astronomer.common.network.BufferedListener;
import com.dave.astronomer.common.network.NetworkUtils;
import com.dave.astronomer.common.network.PacketHandler;
import com.dave.astronomer.common.network.PlayerConnection;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.MapSystem;
import com.dave.astronomer.common.world.PhysicsSystem;
import com.dave.astronomer.server.system.ServerPlayerSystem;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MAServer extends Server {

    private CoreEngine engine;
    private BufferedListener bufferedListener;
    private LanManager lanManager;
    private Map<Class<? extends PacketHandler>, PacketHandler> handlers = new HashMap<>();

    public MAServer(WorldData data) throws IOException {
        NetworkUtils.registerAll(this);

        ServerState.getInstance().setWorldData(data);



        try {
            lanManager = new LanManager(this);
        } catch (IOException e) {
            Log.warn("Unable to start LAN manager: " + e.getMessage());
        }
        PhysicsSystem physicsSystem = new PhysicsSystem();

        engine = new CoreEngine();
        engine.addSystems(
                new ServerPlayerSystem(this),
                physicsSystem,
                new MapSystem(data.getMap(), physicsSystem.getWorld())

        );


        ServerGamePacketHandler serverGamePacketHandler = new ServerGamePacketHandler(engine, this);
        bufferedListener = new BufferedListener(serverGamePacketHandler);


        addHandler(serverGamePacketHandler);
        addListener(bufferedListener);


        try {
            bind(NetworkUtils.TCP_PORT, NetworkUtils.UDP_PORT);
        } catch (IOException e) {
            dispose();
            throw e;
        }


    }
    public void update(float delta) {
        engine.update(delta);

        bufferedListener.processPacketBuffer(handlers);

        if (lanManager != null && lanManager.isOpenToLan()) {
            lanManager.broadcast();
        }
    }



    @Override
    public void dispose() throws IOException {

        engine.dispose();
        super.dispose();

    }
    private void addHandler(PacketHandler handler) {
        handlers.put(handler.getClass(), handler);
    }
    @Override
    protected Connection newConnection() {
        return new PlayerConnection();
    }

}
