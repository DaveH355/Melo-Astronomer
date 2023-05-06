package com.dave.astronomer.server;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.dave.astronomer.common.world.CoreEngine;


public record WorldData (CoreEngine.EngineMetaData clientEngineMetaData, TiledMap map) {

}

