package com.dave.astronomer.client.world;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.dave.astronomer.common.Constants;
import com.dave.astronomer.common.OrthogonalTiledMapRendererBleeding;
import com.dave.astronomer.common.ashley.core.Engine;
import com.dave.astronomer.common.ashley.core.EntitySystem;
import com.dave.astronomer.common.world.MapSystem;
import com.dave.astronomer.common.world.MockableSystem;


public class ClientMapSystem extends MapSystem implements Disposable, MockableSystem {
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    public ClientMapSystem(TiledMap map, World world, SpriteBatch batch,OrthographicCamera camera) {
        super(map, world);
        this.camera = camera;
        renderer = new OrthogonalTiledMapRendererBleeding(map, 1 / Constants.PIXELS_PER_METER, batch);

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        renderer.setView(camera);

        renderer.renderTileLayer(getBackLayer());


    }

    public class AlwaysFrontMapRenderer extends EntitySystem {

        @Override
        public void update(float deltaTime) {
            renderer.renderTileLayer(getAlwaysFrontLayer());
        }
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
