package com.gempukku.ld43.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gempukku.ld43.model.GameScreenComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

@RegisterSystem
public class BackgroundRenderSystem extends AbstractLifeCycleSystem {
    @Inject
    private TextureAtlasProvider textureAtlasProvider;
    private SpriteBatch spriteBatch;

    @Override
    public void initialize() {
        spriteBatch = new SpriteBatch();
    }

    @ReceiveEvent(priority = 1)
    public void renderBackground(RenderToPipeline renderToPipeline, EntityRef camera, GameScreenComponent gameScreen) {
        int width = renderToPipeline.getWidth();
        int height = renderToPipeline.getHeight();

        TextureRegion wallpaper = textureAtlasProvider.getTexture("sprites", "images/wallpaper.png");

        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
        spriteBatch.begin();
        for (int x = 0; x < width; x += wallpaper.getRegionWidth()) {
            for (int y = 0; y < height; y += wallpaper.getRegionHeight()) {
                spriteBatch.draw(wallpaper, x, y, wallpaper.getRegionWidth(), wallpaper.getRegionHeight());
            }
        }
        spriteBatch.end();
        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    @Override
    public void destroy() {
        spriteBatch.dispose();
    }
}
