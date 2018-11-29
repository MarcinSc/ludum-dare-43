package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.event.Event;

public class EntityMoved extends Event {
    private float oldX;
    private float oldY;

    public EntityMoved(float oldX, float oldY) {
        this.oldX = oldX;
        this.oldY = oldY;
    }

    public float getOldX() {
        return oldX;
    }

    public float getOldY() {
        return oldY;
    }
}
