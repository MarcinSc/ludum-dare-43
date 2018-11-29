package com.gempukku.secsy.gaming.physics.box2d;

public interface PhysicsFixture {
    enum Type {
        Box, Circle;
    }

    Type getType();

    float getX();

    float getY();

    float getDensity();

    float getFriction();

    short getCategory();

    short getContactMask();

    boolean isSensor();

    String getUserData();
}
