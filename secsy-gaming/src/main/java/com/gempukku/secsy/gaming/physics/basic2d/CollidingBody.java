package com.gempukku.secsy.gaming.physics.basic2d;

public class CollidingBody {
    public final int entityId;
    public float left;
    public float right;
    public float down;
    public float up;

    public float oldX;
    public float oldY;
    public float newX;
    public float newY;
    public boolean hadCollisionX;
    public boolean hadCollisionY;
    public float adjustedX;
    public float adjustedY;

    public CollidingBody(int entityId, float left, float right, float down, float up) {
        this.entityId = entityId;
        this.left = left;
        this.right = right;
        this.down = down;
        this.up = up;
    }

    public void updatePositions(float oldX, float oldY, float newX, float newY) {
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
    }
}
