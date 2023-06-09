package com.dave.astronomer.server;

import com.badlogic.gdx.math.Vector2;
import com.dave.astronomer.common.ashley.core.EntitySystem;
import com.dave.astronomer.common.network.packet.ClientboundAddMainPlayerPacket;
import com.dave.astronomer.common.network.packet.ClientboundAddPlayerPacket;
import com.dave.astronomer.common.network.packet.ClientboundRemoveEntityPacket;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.MockableSystem;
import com.dave.astronomer.common.world.entity.Player;
import com.dave.astronomer.server.entity.ServerPlayer;


public class ServerEngine extends CoreEngine {
    private MAServer server;
    public ServerEngine(MAServer server, CoreEngine.EngineMetaData clientEngineMetaData) {
        super(false);

        this.server = server;

        for (EntitySystem prioritySystem : clientEngineMetaData.prioritySystems) {
            if (prioritySystem instanceof MockableSystem) {
                addPrioritySystems(prioritySystem);
            }
        }
        for (EntitySystem system : clientEngineMetaData.systems) {
            if (system instanceof MockableSystem) {
                addSystems(system);
            }
        }
    }

    @Override
    public void removeAndRespawnPlayer(Player player) {
        removeEntity(player);
        ClientboundRemoveEntityPacket packet = new ClientboundRemoveEntityPacket();
        packet.uuid = player.getUuid();

        server.sendToAllTCP(packet);

        ServerPlayer newPlayer = new ServerPlayer(this, ((ServerPlayer) player).getConnection());
        newPlayer.forcePosition(new Vector2(10, 8), 0);

        ClientboundAddMainPlayerPacket mainPlayerPacket = new ClientboundAddMainPlayerPacket();
        mainPlayerPacket.uuid = newPlayer.getUuid();
        mainPlayerPacket.position = newPlayer.getPosition();
        ((ServerPlayer) player).getConnection().sendTCP(mainPlayerPacket);

        ClientboundAddPlayerPacket addPlayerPacket = new ClientboundAddPlayerPacket(newPlayer);
        server.sendToAllExceptTCP(((ServerPlayer) player).getConnection().getID(), addPlayerPacket);

        addEntity(newPlayer);
    }

    @Override
    public boolean shouldProcess(EntitySystem system) {
        return !(system instanceof MockableSystem);
    }

}
