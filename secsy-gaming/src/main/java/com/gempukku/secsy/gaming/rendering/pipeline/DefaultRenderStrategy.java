package com.gempukku.secsy.gaming.rendering.pipeline;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;

public class DefaultRenderStrategy implements RenderStrategy {
    @Override
    public int getRenderBufferWidth(int screenWidth, int screenHeight) {
        return screenWidth;
    }

    @Override
    public int getRenderBufferHeight(int screenWidth, int screenHeight) {
        return screenHeight;
    }

    @Override
    public Pixmap.Format getRenderBufferFormat(int screenWidth, int screenHeight) {
        return Pixmap.Format.RGBA8888;
    }

    @Override
    public Color getScreenFillColor() {
        return Color.BLACK;
    }

    @Override
    public Rectangle getScreenRenderRectangle(int screenWidth, int screenHeight, int bufferWidth, int bufferHeight, Rectangle toUse) {
        return toUse.set(0, 0, bufferWidth, bufferHeight);
    }
}
