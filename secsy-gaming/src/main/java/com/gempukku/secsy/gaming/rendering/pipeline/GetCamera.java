package com.gempukku.secsy.gaming.rendering.pipeline;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.secsy.entity.event.Event;

public class GetCamera extends Event {
    private float delta;
    private int width;
    private int height;
    private Camera camera;

    public GetCamera(float delta, int width, int height) {
        this.delta = delta;
        this.width = width;
        this.height = height;
    }

    public float getDelta() {
        return delta;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
