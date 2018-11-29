package com.gempukku.secsy.gaming.physics.box2d;


public class BoxFixture implements PhysicsFixture {
    private float x;
    private float y;
    private float width;
    private float height;
    private float density;
    private float friction;
    private short category;
    private short contactMask;
    private boolean sensor;
    private float angle;
    private String userData;

    public BoxFixture(float x, float y, float width, float height, float density, float friction, short category, short contactMask,
                      boolean sensor, float angle, String userData) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.density = density;
        this.friction = friction;
        this.category = category;
        this.contactMask = contactMask;
        this.sensor = sensor;
        this.angle = angle;
        this.userData = userData;
    }

    @Override
    public Type getType() {
        return Type.Box;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
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

    public float getAngle() {
        return angle;
    }

    public String getUserData() {
        return userData;
    }
}
