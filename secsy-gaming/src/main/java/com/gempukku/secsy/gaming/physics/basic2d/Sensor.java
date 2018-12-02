package com.gempukku.secsy.gaming.physics.basic2d;

public class Sensor {
    public int entityId;
    public final String type;
    public final float left;
    public final float right;
    public final float down;
    public final float up;

    public Sensor(String type, float left, float right, float down, float up) {
        this.type = type;
        this.left = left;
        this.right = right;
        this.down = down;
        this.up = up;
    }

    public Sensor(Sensor sensor) {
        this.type = sensor.type;
        this.left = sensor.left;
        this.right = sensor.right;
        this.down = sensor.down;
        this.up = sensor.up;
    }
}
