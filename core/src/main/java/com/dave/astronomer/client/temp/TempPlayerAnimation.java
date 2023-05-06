package com.dave.astronomer.client.temp;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.asset.AssetManagerResolving;
import com.dave.astronomer.client.world.component.SpriteComponent;
import com.dave.astronomer.common.AnimationUtils;
import com.dave.astronomer.common.Constants;

import java.util.HashMap;
import java.util.Map;


public class TempPlayerAnimation {

    public static SpriteComponent createSpriteComponent() {
        AssetManagerResolving assetManager = MeloAstronomer.getInstance().getAssetManager();

        Texture texture = assetManager.get("temp_player.png", Texture.class);


        int width = 32;
        int height = 32;
        //cut idle, no blink
        int idleDuration = 200;
        Animation<TextureRegion> idle = AnimationUtils.loadFromTexture(texture, width, height,2, 0, 0, idleDuration);

        int walkDuration = 150;
        Animation<TextureRegion> walk = AnimationUtils.loadFromTexture(texture, width, height, 4, 2, 0, walkDuration);

        int dashDuration = 75;
        Animation<TextureRegion> dash = AnimationUtils.loadFromTexture(texture, width, height, 8, 3, 0, dashDuration);

        Map<String, Animation<TextureRegion>> map = new HashMap<>();
        map.put("idle", idle);
        map.put("walk", walk);
        map.put("dash", dash);


        SpriteComponent component = new SpriteComponent(map);
        component.selectAnimationIfAbsent("idle", true);


        Sprite sprite = new Sprite(component.getRegion());
        sprite.setBounds(0, 0, sprite.getWidth() / Constants.PIXELS_PER_METER, sprite.getHeight() / Constants.PIXELS_PER_METER);

        component.setSprite(sprite);
        return component;

    }
}
