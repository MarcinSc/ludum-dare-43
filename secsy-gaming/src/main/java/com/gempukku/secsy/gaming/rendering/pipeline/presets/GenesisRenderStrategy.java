package com.gempukku.secsy.gaming.rendering.pipeline.presets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderStrategy;

@RegisterSystem(profiles = "genesisSimulation", shared = RenderStrategy.class)
public class GenesisRenderStrategy implements RenderStrategy {
    @Override
    public int getRenderBufferWidth(int screenWidth, int screenHeight) {
        return 320;
    }

    @Override
    public int getRenderBufferHeight(int screenWidth, int screenHeight) {
        return 240;
    }

    @Override
    public Pixmap.Format getRenderBufferFormat(int screenWidth, int screenHeight) {
        return Pixmap.Format.RGB565;
    }

    @Override
    public Color getScreenFillColor() {
        return Color.BLACK;
    }

    @Override
    public Rectangle getScreenRenderRectangle(int screenWidth, int screenHeight, int bufferWidth, int bufferHeight, Rectangle toUse) {
        float screenRatio = 1f * screenWidth / screenHeight;

        if (screenRatio > 4f / 3) {
            int width = screenHeight * 4 / 3;
            toUse.set((screenWidth - width) / 2, 0, width, screenHeight);
        } else {
            int height = screenWidth * 3 / 4;
            toUse.set(0, (screenHeight - height) / 2, screenWidth, height);
        }
        return toUse;
    }
}
