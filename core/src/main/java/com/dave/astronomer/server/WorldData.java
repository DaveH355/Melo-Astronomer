package com.dave.astronomer.server;

import com.badlogic.gdx.maps.tiled.TiledMap;
import lombok.Getter;

public class WorldData {
    @Getter private TiledMap map;

    public WorldData(TiledMap map) {
        this.map = map;

    }
}
