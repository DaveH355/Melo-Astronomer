package com.dave.astronomer.common.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import lombok.Getter;

public class CollisionContact {
    @Getter
    private Fixture ownFixture;
    @Getter
    private Fixture otherFixture;
    private Contact contact;

    public CollisionContact(Fixture ownFixture, Fixture otherFixture, Contact contact) {
        this.ownFixture = ownFixture;
        this.otherFixture = otherFixture;
        this.contact = contact;
    }

    public Body getOtherBody() {
        return otherFixture.getBody();
    }
    public Vector2 getContactNormal() {
        return contact.getWorldManifold().getNormal();
    }
}
