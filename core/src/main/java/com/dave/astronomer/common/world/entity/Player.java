package com.dave.astronomer.common.world.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.EntityType;
import lombok.Getter;

import java.util.UUID;

public abstract class Player extends BaseEntity implements Disposable {

    @Getter private int hurtTimes;
    @Getter
    private boolean dead;
    public Player(CoreEngine engine, UUID uuid) {
       super(EntityType.PLAYER, engine);
       setUuid(uuid);
    }
    public Player(CoreEngine engine) {
        super(EntityType.PLAYER, engine);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (hurtTimes >= 10 && !dead) {
            die();
        }
    }

    //TODO: remove this temp method
    public Knife throwKnife(float targetAngleRad) {
        Vector2 bodyCenter = getBody().getWorldCenter();
        Vector2 knifePos = new Vector2(bodyCenter.x, bodyCenter.y);
        Knife knife = new Knife(getEngine(), knifePos, targetAngleRad, this);

        getEngine().addEntity(knife);
        return knife;

    }
    public void hurt() {
        hurtTimes++;
    }
    public void die() {
        dead = true;
        getBody().setLinearVelocity(0, 0);
    }
}
