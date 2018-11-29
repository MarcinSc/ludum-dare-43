package com.gempukku.secsy.gaming.rendering.pipeline;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public interface RenderPipeline {
    FrameBuffer getCurrentBuffer();

    void setCurrentBuffer(FrameBuffer frameBuffer);

    FrameBuffer getNewFrameBuffer(int width, int height);

    void returnFrameBuffer(FrameBuffer frameBuffer);
}
