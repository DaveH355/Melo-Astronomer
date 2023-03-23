package com.dave.astronomer.common.world;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import lombok.Getter;

public class MapCell extends TiledMapTileLayer.Cell {
    @Getter private int cellX;
    @Getter private int cellY;

    @Getter private TiledMapTileLayer layer;


    public MapCell(TiledMapTileLayer layer, TiledMapTileLayer.Cell cell, int cellX, int cellY) {
        this.layer = layer;
        this.cellX = cellX;
        this.cellY = cellY;

        if (cell.getTile() != null) {
            setTile(cell.getTile());
        }


        setFlipHorizontally(cell.getFlipHorizontally());
        setFlipVertically(cell.getFlipVertically());
        setRotation(cell.getRotation());
    }
}
