package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.event.Event;

public class EntityCollided extends Event {
    private boolean xAxis;
    private boolean yAxis;
    private boolean positiveX;
    private boolean positiveY;
    private float speedX;
    private float speedY;

    public EntityCollided(boolean xAxis, boolean yAxis, boolean positiveX, boolean positiveY, float speedX, float speedY) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.positiveX = positiveX;
        this.positiveY = positiveY;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public boolean isXAxis() {
        return xAxis;
    }

    public boolean isYAxis() {
        return yAxis;
    }

    public float getSpeedX() {
        return speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    public boolean isPositiveX() {
        return positiveX;
    }

    public boolean isPositiveY() {
        return positiveY;
    }
}


