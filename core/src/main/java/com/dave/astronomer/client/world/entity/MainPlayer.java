package com.dave.astronomer.client.world.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Body;
import com.dave.astronomer.client.temp.TempPlayerAnimation;
import com.dave.astronomer.client.world.CameraShake;
import com.dave.astronomer.client.world.component.InputComponent;
import com.dave.astronomer.client.world.component.SpriteComponent;
import com.dave.astronomer.common.data.PlayerData;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.PhysicsSystem;
import lombok.Getter;

import java.util.UUID;

public class MainPlayer extends AbstractClientPlayer {
    @Getter private SpriteComponent spriteComponent;
    @Getter private Body body;
    @Getter private InputComponent inputComponent;
    @Getter private InputComponent.KeyAction walkUpKey, walkDownKey, walkLeftKey, walkRightKey, dashKey;



    public MainPlayer(CoreEngine engine, UUID uuid) {
        super(engine, uuid);


        spriteComponent = createSpriteComponent();
        inputComponent = createInputComponent();
        body = PlayerData.createBody(engine.getSystem(PhysicsSystem.class).getWorld());


        addComponents(
                inputComponent,
                spriteComponent
        );

    }

    @Override
    public void hurt() {
        super.hurt();
        CameraShake.shake(0.03f, 0.25f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    public static SpriteComponent createSpriteComponent() {
        return TempPlayerAnimation.createSpriteComponent();
    }
    private InputComponent createInputComponent() {
        InputComponent inputComponent = new InputComponent();

        inputComponent.addKeyAction(
            walkUpKey = new InputComponent.KeyAction(Input.Keys.W),
            walkLeftKey = new InputComponent.KeyAction(Input.Keys.A),
            walkDownKey = new InputComponent.KeyAction(Input.Keys.S),
            walkRightKey = new InputComponent.KeyAction(Input.Keys.D),
            dashKey = new InputComponent.KeyAction(Input.Keys.SPACE)
        );

        return inputComponent;
    }



}
