package com.dave.astronomer.server.entity;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.dave.astronomer.common.PhysicsUtils;
import com.dave.astronomer.common.data.PlayerData;
import com.dave.astronomer.common.network.PlayerConnection;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.PhysicsSystem;
import com.dave.astronomer.common.world.entity.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class ServerPlayer extends Player {
    @Getter @Setter private Body body;

    @Getter private PlayerConnection connection;

    @Getter @Setter private boolean isReady = false;

    @Getter @Setter private Vector2 clientPosition = new Vector2(0, 0);

    @Getter private Map<Integer, State> serverState = new HashMap<>();
    @Getter private Map<Integer, State> clientState = new HashMap<>();

    public ServerPlayer(CoreEngine engine, PlayerConnection connection) {
        super(engine, connection.uuid);
        this.connection = connection;

        body = createBody();
    }


    private Body createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        PhysicsSystem physicsSystem = getEngine().getSystem(PhysicsSystem.class);

        Body b = physicsSystem.getWorld().createBody(bodyDef);
        FixtureDef fdef = new FixtureDef();


        Circle circle = PlayerData.getBoundingCircle();
        fdef.shape = PhysicsUtils.toShape(circle);
        fdef.friction = 1;


        b.createFixture(fdef);

        return b;
    }

    @Override
    public void forcePosition(Vector2 position, float angle) {
        super.forcePosition(position, angle);
        clientPosition = position;
    }
}
