package com.gempukku.secsy.gaming.camera2d;

import com.gempukku.secsy.entity.event.Event;

public class Adjust2dCamera extends Event {
    private final float lastViewportWidth;
    private final float lastViewportHeight;
    private final float lastX;
    private final float lastY;
    private float viewportWidth;
    private float viewportHeight;
    private float x;
    private float y;

    private float nonLastingX;
    private float nonLastingY;

    public Adjust2dCamera(
            float lastViewportWidth, float lastViewportHeight, float viewportWidth, float viewportHeight,
            float lastX, float lastY, float x, float y) {
        this.lastViewportWidth = lastViewportWidth;
        this.lastViewportHeight = lastViewportHeight;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.lastX = lastX;
        this.lastY = lastY;
        this.x = x;
        this.y = y;
    }

    public float getLastViewportWidth() {
        return lastViewportWidth;
    }

    public float getLastViewportHeight() {
        return lastViewportHeight;
    }

    public float getViewportWidth() {
        return viewportWidth;
    }

    public float getViewportHeight() {
        return viewportHeight;
    }

    public void setViewportWidth(float viewportWidth) {
        this.viewportWidth = viewportWidth;
    }

    public void setViewportHeight(float viewportHeight) {
        this.viewportHeight = viewportHeight;
    }

    public void setViewport(float width, float height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
    }

    public float getLastX() {
        return lastX;
    }

    public float getLastY() {
        return lastY;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getNonLastingX() {
        return nonLastingX;
    }

    public void setNonLastingX(float nonLastingX) {
        this.nonLastingX = nonLastingX;
    }

    public float getNonLastingY() {
        return nonLastingY;
    }

    public void setNonLastingY(float nonLastingY) {
        this.nonLastingY = nonLastingY;
    }
}
