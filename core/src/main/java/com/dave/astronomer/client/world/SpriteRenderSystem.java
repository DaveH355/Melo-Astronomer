package com.dave.astronomer.client.world;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dave.astronomer.client.GameState;
import com.dave.astronomer.client.world.component.SpriteComponent;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.BaseEntitySystem;


public class SpriteRenderSystem extends BaseEntitySystem {

    private ComponentMapper<SpriteComponent> mapper = ComponentMapper.getFor(SpriteComponent.class);

    public SpriteRenderSystem() {
        super(Family.all(SpriteComponent.class).get());

    }



    @Override
    public void processEntity(BaseEntity entity, float deltaTime) {
        SpriteBatch batch = GameState.getInstance().getGameBatch();

        SpriteComponent component = mapper.get(entity);

        component.incrementStateTime(deltaTime);

        Sprite sprite = component.getSprite();
        sprite.setRegion(component.getRegion());
        sprite.draw(batch);
    }

}
