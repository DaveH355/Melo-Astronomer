package com.dave.astronomer.server.entity;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.dave.astronomer.client.world.component.BodyComponent;
import com.dave.astronomer.common.PhysicsUtils;
import com.dave.astronomer.common.network.PlayerConnection;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.PhysicsSystem;
import com.dave.astronomer.common.world.entity.Player;
import com.dave.astronomer.server.ServerState;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class ServerPlayer extends Player {
    @Getter @Setter private BodyComponent bodyComponent;

    @Getter private PlayerConnection connection;

    @Getter @Setter private boolean isReady = false;

    @Getter @Setter private Vector2 clientVelocity = Vector2.Zero;

    @Getter private Map<Integer, State> serverState = new HashMap<>();
    @Getter private Map<Integer, State> clientState = new HashMap<>();

    public ServerPlayer(CoreEngine engine, PlayerConnection connection) {
        super(engine, connection.uuid);
        this.connection = connection;

        bodyComponent = createBodyComponent();
    }


    private BodyComponent createBodyComponent() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        PhysicsSystem physicsSystem = getEngine().getSystem(PhysicsSystem.class);

        Body body = physicsSystem.getWorld().createBody(bodyDef);
        FixtureDef fdef = new FixtureDef();

        Circle circle = ServerState.getInstance().getWorldData().getPlayerBoundingCircle();
        fdef.shape = PhysicsUtils.toShape(circle);
        fdef.restitution = 1;
        fdef.friction = 0;

        body.createFixture(fdef);

        return new BodyComponent(body);
    }

}
