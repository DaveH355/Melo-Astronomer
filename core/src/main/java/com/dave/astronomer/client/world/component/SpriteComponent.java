package com.dave.astronomer.client.world.component;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dave.astronomer.common.world.BaseComponent;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


public class SpriteComponent extends BaseComponent {
    private Animation<TextureRegion> animation;
    private Map<String, Animation<TextureRegion>> animationMap;
    @Getter @Setter private Sprite sprite;
    private boolean looping;
    private float stateTime;

    public SpriteComponent(Map<String, Animation<TextureRegion>> animationMap){
        this.animationMap = animationMap;
    }

    public TextureRegion getRegion() {
        return animation.getKeyFrame(stateTime, looping);
    }

    public void incrementStateTime(float delta) {
        stateTime += delta;
    }
    public void selectAnimationIfAbsent(String name, boolean looping) {
        if(this.animation == animationMap.get(name)) return;

        this.looping = looping;
        this.animation = animationMap.get(name);
        stateTime = 0;
    }
}
