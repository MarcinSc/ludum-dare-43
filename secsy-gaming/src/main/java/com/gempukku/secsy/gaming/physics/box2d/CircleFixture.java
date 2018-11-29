package com.gempukku.secsy.gaming.physics.box2d;


public class CircleFixture implements PhysicsFixture {
    private float x;
    private float y;
    private float radius;
    private float density;
    private float friction;
    private short category;
    private short contactMask;
    private boolean sensor;
    private String userData;

    public CircleFixture(float x, float y, float radius, float density, float friction, short category, short contactMask,
                         boolean sensor, String userData) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.density = density;
        this.friction = friction;
        this.category = category;
        this.contactMask = contactMask;
        this.sensor = sensor;
        this.userData = userData;
    }

    @Override
    public Type getType() {
        return Type.Circle;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }

    public float getDensity() {
        return density;
    }

    public float getFriction() {
        return friction;
    }

    public short getCategory() {
        return category;
    }

    public short getContactMask() {
        return contactMask;
    }

    public boolean isSensor() {
        return sensor;
    }

    public String getUserData() {
        return userData;
    }
}
