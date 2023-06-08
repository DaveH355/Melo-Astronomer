package com.dave.astronomer.client.world.entity;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.dave.astronomer.client.world.CameraShake;
import com.dave.astronomer.client.world.component.SpriteComponent;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.entity.Player;

import java.util.UUID;

public abstract class AbstractClientPlayer extends Player {
    private boolean takingDamage = false;
    float flashDuration = 0.25f;
    float flashTimer = 0f;

    public AbstractClientPlayer(CoreEngine engine, UUID uuid) {
        super(engine, uuid);

    }
    @Override
    public void hurt() {
        takingDamage = true;
        flashTimer = 0;
    }


    @Override
    public void update(float delta) {
        super.update(delta);

        if (takingDamage) {
            flashTimer += delta;
            if (flashTimer > flashDuration) {
                flashTimer = 0f;
                takingDamage = false;
                getSpriteComponent().getSprite().setColor(Color.WHITE);
            }

            float flashAmount = Interpolation.linear.apply(0f, 1f, (flashTimer / flashDuration));

            Color originalColor = getSpriteComponent().getSprite().getColor();
            Color flashColor = originalColor.cpy().lerp(Color.BLACK, flashAmount);
            getSpriteComponent().getSprite().setColor(flashColor);
        }
    }

    public abstract SpriteComponent getSpriteComponent();
}
