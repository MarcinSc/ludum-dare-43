package com.gempukku.secsy.gaming.rendering.pipeline;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.secsy.entity.event.Event;

public class RenderToPipeline extends Event {
    private RenderPipeline renderPipeline;
    private Camera camera;
    private float delta;
    private int width;
    private int height;

    public RenderToPipeline(RenderPipeline renderPipeline, Camera camera, float delta, int width, int height) {
        this.renderPipeline = renderPipeline;
        this.camera = camera;
        this.delta = delta;
        this.width = width;
        this.height = height;
    }

    public RenderPipeline getRenderPipeline() {
        return renderPipeline;
    }

    public Camera getCamera() {
        return camera;
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
}
