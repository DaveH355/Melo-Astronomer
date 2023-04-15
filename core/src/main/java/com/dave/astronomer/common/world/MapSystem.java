package com.dave.astronomer.common.world;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.dave.astronomer.common.PhysicsUtils;
import com.esotericsoftware.minlog.Log;
import lombok.Getter;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapSystem extends EntitySystem {
    private List<String> debug = new ArrayList<>();
    private TiledMap map;

    @Getter private TiledMapTileLayer backLayer;

    @Getter private TiledMapTileLayer alwaysFrontLayer;

    private String alwaysFront = "Always Front";
    private String back = "Back";
    private String collision = "Collision";


    public MapSystem(TiledMap map, World world) {
        this.map = map;
        loadMap(map, world);

    }


    @CheckForNull
    public MapCell getCellAt(Vector2 position, TiledMapTileLayer layer) {

        //position should be in meters, conveniently each meter is same as tile size
        //no modify on position needed
        int y = Math.round(position.y);
        int x = Math.round(position.x);


        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell == null) return null;

        return new MapCell(layer,cell, x, y);
    }


    private void loadMap(TiledMap map, World world) {
        MapLayers layers = map.getLayers();
        backLayer = (TiledMapTileLayer) layers.get(back);

        alwaysFrontLayer = ((TiledMapTileLayer) layers.get(alwaysFront));


        loadCells(backLayer);
        loadCells(alwaysFrontLayer);


        MapLayer collisionLayer = layers.get(collision);
        collisionLayer.getObjects().forEach(mapObject -> handleAsPhysicsObject(mapObject, world));


        //print stats
        Map<String, Long> counts =
                debug.stream().collect(Collectors.groupingBy(s -> s, Collectors.counting()));
        String string = Arrays.toString(counts.entrySet().toArray());
        Log.info("Processed map, " + string);

        debug.clear();
    }
    private void loadCells(TiledMapTileLayer layer) {
        for (int row = 0; row < layer.getWidth(); row++) {
            for (int col = 0; col < layer.getHeight(); col++) {

                TiledMapTileLayer.Cell cell = layer.getCell(row, col);

                if (cell != null) {
                    TiledMapTile tile = cell.getTile();

                    //TODO: actually load cell
                    MapProperties properties = tile.getProperties();
                    String type = properties.get("type", String.class);
                    if (type == null) {
                        debug.add("Null");
                        continue;
                    }

                    debug.add(type);
                }
            }
        }


    }

    private void handleAsPhysicsObject(MapObject mapObject, World world) {
        Shape shape;
        if (mapObject instanceof RectangleMapObject object) {
            Rectangle rectangle = object.getRectangle();

            Polygon polygon = PhysicsUtils.bevelRectangle(rectangle, 3);
            shape = PhysicsUtils.toShape(polygon);

        } else if (mapObject instanceof PolygonMapObject object) {
            shape = PhysicsUtils.toShape(object);

        } else if (mapObject instanceof PolylineMapObject object) {
            shape = PhysicsUtils.toShape(object);

        } else if (mapObject instanceof CircleMapObject object) {
            shape = PhysicsUtils.toShape(object);

        } else {
            Log.warn("Couldn't load " + mapObject + " as physics object");
            return;
        }

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);


        body.createFixture(shape, 0);
        shape.dispose();
    }

}
