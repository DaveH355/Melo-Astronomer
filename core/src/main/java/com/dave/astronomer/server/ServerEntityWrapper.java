package com.dave.astronomer.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Null;
import com.dave.astronomer.common.ashley.core.Entity;
import com.dave.astronomer.common.network.PlayerConnection;
import com.dave.astronomer.common.world.BaseEntity;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;

//wrapper class for validation between client and server entities
public class ServerEntityWrapper extends Entity {
    @Getter private BaseEntity serverEntity;

    @Getter Vector2 latestClientPos;
    @Getter @Null private final PlayerConnection connection;

    public ServerEntityWrapper(BaseEntity serverEntity, PlayerConnection connection) {
        this.serverEntity = serverEntity;
        this.connection = connection;
    }
    public ServerEntityWrapper(BaseEntity serverEntity) {
        this.serverEntity = serverEntity;
        this.connection = null;
    }
}
