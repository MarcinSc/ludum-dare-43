package com.gempukku.secsy.gaming.rendering.pipeline;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;

public interface RenderStrategy {
    int getRenderBufferWidth(int screenWidth, int screenHeight);

    int getRenderBufferHeight(int screenWidth, int screenHeight);

    Pixmap.Format getRenderBufferFormat(int screenWidth, int screenHeight);

    Color getScreenFillColor();

    Rectangle getScreenRenderRectangle(int screenWidth, int screenHeight, int bufferWidth, int bufferHeight, Rectangle toUse);
}
