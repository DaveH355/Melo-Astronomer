package com.dave.astronomer.client.world.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Body;
import com.dave.astronomer.client.temp.TempPlayerAnimation;
import com.dave.astronomer.client.world.ClientPhysicsSystem;
import com.dave.astronomer.client.world.component.InputComponent;
import com.dave.astronomer.client.world.component.SpriteComponent;
import com.dave.astronomer.common.data.PlayerData;
import com.dave.astronomer.common.world.CoreEngine;
import lombok.Getter;

import java.util.UUID;

public class MainPlayer extends AbstractClientPlayer {

    @Getter private SpriteComponent spriteComponent;
    @Getter private Body body;
    @Getter private InputComponent inputComponent;
    @Getter private InputComponent.KeyAction walkUpKey, walkDownKey, walkLeftKey, walkRightKey;

    public MainPlayer(CoreEngine engine, UUID uuid) {
        super(engine, uuid);


        spriteComponent = createSpriteComponent();
        inputComponent = createInputComponent();
        body = PlayerData.createBody(engine.getSystem(ClientPhysicsSystem.class).getWorld());

        addComponents(
                inputComponent,
                spriteComponent
        );

    }
    public static SpriteComponent createSpriteComponent() {
        return TempPlayerAnimation.createSpriteComponent();

    }
    private InputComponent createInputComponent() {
        InputComponent inputComponent = new InputComponent();


        walkUpKey = new InputComponent.KeyAction(Input.Keys.W);
        walkLeftKey = new InputComponent.KeyAction(Input.Keys.A);
        walkDownKey = new InputComponent.KeyAction(Input.Keys.S);
        walkRightKey = new InputComponent.KeyAction(Input.Keys.D);

        inputComponent.addKeyAction(
                walkDownKey,
                walkUpKey,
                walkLeftKey,
                walkRightKey
        );

        return inputComponent;
    }



}
