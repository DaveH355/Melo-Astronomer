package com.dave.astronomer.client.world.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.asset.AssetManagerResolving;
import com.dave.astronomer.client.world.ClientPhysicsSystem;
import com.dave.astronomer.client.world.component.InputComponent;
import com.dave.astronomer.client.world.component.SpriteComponent;
import com.dave.astronomer.common.AnimationUtils;
import com.dave.astronomer.common.Constants;
import com.dave.astronomer.common.PhysicsUtils;
import com.dave.astronomer.common.world.CoreEngine;
import lombok.Getter;

import java.util.Map;
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
        body = createBody(spriteComponent);

        addComponents(
                inputComponent,
                spriteComponent
        );

    }
    public static SpriteComponent createSpriteComponent() {
        AssetManagerResolving assetManager = MeloAstronomer.getInstance().getAssetManager();

        Map<String, Animation<TextureRegion>> animationMap = AnimationUtils.loadFromAseprite(assetManager.get("ma_temp_player.png", Texture.class), Gdx.files.internal("image/ma_temp_player.json"));



        SpriteComponent component = new SpriteComponent(animationMap);
        component.selectAnimationIfAbsent("idle", true);


        Sprite sprite = new Sprite(component.getRegion());
        sprite.setBounds(0, 0, sprite.getWidth() / Constants.PIXELS_PER_METER, sprite.getHeight() / Constants.PIXELS_PER_METER);

        component.setSprite(sprite);
        return component;

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
    private Body createBody(SpriteComponent spriteComponent) {
        ClientPhysicsSystem physicsSystem = getEngine().getSystem(ClientPhysicsSystem.class);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;


        Body b = physicsSystem.getWorld().createBody(def);

        FixtureDef fdef = new FixtureDef();



        Circle circle = PhysicsUtils.traceCircle(spriteComponent.getSprite(), true);
        fdef.shape = PhysicsUtils.toShape(circle);
        fdef.friction = 1;


        //TODO: collision system that stops players from colliding
        b.createFixture(fdef);

        return b;
    }



}
