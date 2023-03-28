package com.dave.astronomer.server;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Circle;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.common.PhysicsUtils;
import lombok.Getter;

public class WorldData {
    @Getter private TiledMap map;

    public WorldData(TiledMap map) {
        this.map = map;

    }
}
