package com.gempukku.secsy.gaming.physics.basic2d;

public class Obstacle {
    public final int entityId;
    public float left;
    public float right;
    public float down;
    public float up;

    public float oldX;
    public float oldY;
    public float newX;
    public float newY;

    public boolean isAABB;

    public float[] nonAABBVertices;

    public Obstacle(int entityId, float left, float right, float down, float up) {
        this.entityId = entityId;
        this.left = left;
        this.right = right;
        this.down = down;
        this.up = up;
        this.isAABB = true;
    }

    public Obstacle(int entityId, float[] vertices) {
        this.entityId = entityId;
        this.isAABB = false;
        assignLeftRight(vertices);
        assignDownUp(vertices);
        this.nonAABBVertices = vertices;
    }

    private void assignDownUp(float[] vertices) {
        float minY = vertices[1];
        float maxY = vertices[1];
        for (int i = 3; i < vertices.length; i += 2) {
            if (vertices[i] < minY)
                minY = vertices[i];
            else if (vertices[i] > maxY)
                maxY = vertices[i];
        }
        this.down = minY;
        this.up = maxY;
    }

    private void assignLeftRight(float[] verices) {
        float minX = verices[0];
        float maxX = verices[0];
        for (int i = 2; i < verices.length; i += 2) {
            if (verices[i] < minX)
                minX = verices[i];
            else if (verices[i] > maxX)
                maxX = verices[i];
        }
        this.left = minX;
        this.right = maxX;
    }

    public void updatePositions(float oldX, float oldY, float newX, float newY) {
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
    }
}
