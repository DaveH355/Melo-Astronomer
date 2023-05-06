package com.dave.astronomer.common.world.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.dave.astronomer.common.world.BaseEntity;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.EntityType;

public class Projectile extends BaseEntity {


    public Projectile(EntityType<?> entityType, CoreEngine engine) {
        super(entityType, engine);
    }

    @Override
    public Body getBody() {
        return null;
    }
}
