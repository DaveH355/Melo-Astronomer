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

public class ServerPlayer extends Player {
    @Getter @Setter private Body body;
    @Getter private PlayerConnection connection;
    public Vector2 lastestClientPosition;

    public ServerPlayer(CoreEngine engine, PlayerConnection connection) {
        super(engine);
        this.connection = connection;
        connection.serverPlayer = this;
        setMovementBehavior(new MovementBehavior.BasicLerp());

        body = PlayerData.createBody(engine.getSystem(PhysicsSystem.class).getWorld());
    }
}
