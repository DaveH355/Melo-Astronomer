package com.dave.astronomer.server.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.dave.astronomer.common.DeltaTimer;
import com.dave.astronomer.common.data.PlayerData;
import com.dave.astronomer.common.network.PlayerConnection;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.PhysicsSystem;
import com.dave.astronomer.common.world.entity.Player;
import com.dave.astronomer.common.world.movement.MovementBehavior;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

public class ServerPlayer extends Player {

    @Getter private PlayerConnection connection;
    public Vector2 lastestClientPosition;

    private DeltaTimer delayRespawnTimer = new DeltaTimer(5, TimeUnit.SECONDS);

    public ServerPlayer(CoreEngine engine, PlayerConnection connection) {
        super(engine);
        this.connection = connection;
        connection.serverPlayer = this;
        setMovementBehavior(new MovementBehavior.BasicLerp());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (isDead()) {
            if (delayRespawnTimer.update(delta)) {
                getEngine().removeAndRespawnPlayer(this);
            }
        }
    }

    @Override
    public Body createBody() {
        return PlayerData.createBody(getEngine().getSystem(PhysicsSystem.class).getWorld());
    }
}
