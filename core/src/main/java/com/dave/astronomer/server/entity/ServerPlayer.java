package com.dave.astronomer.server.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.dave.astronomer.common.data.PlayerData;
import com.dave.astronomer.common.network.PlayerConnection;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.PhysicsSystem;
import com.dave.astronomer.common.world.entity.Player;
import com.dave.astronomer.common.world.movement.MovementBehavior;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.Deque;

public class ServerPlayer extends Player {
    @Getter @Setter private Body body;
    @Getter private PlayerConnection connection;
    public Vector2 lastestClientPosition;
    @Getter private Deque<State> serverState = new ArrayDeque<>();
    @Getter private Deque<State> clientState = new ArrayDeque<>();

    public ServerPlayer(CoreEngine engine, PlayerConnection connection) {
        super(engine, connection.uuid);
        setMovementBehavior(new MovementBehavior.BasicLerp());
        this.connection = connection;

        body = PlayerData.createBody(engine.getSystem(PhysicsSystem.class).getWorld());
    }



    @Override
    public void forcePosition(Vector2 position, float angle) {
        super.forcePosition(position, angle);
        serverState.clear();
        clientState.clear();
    }
}
