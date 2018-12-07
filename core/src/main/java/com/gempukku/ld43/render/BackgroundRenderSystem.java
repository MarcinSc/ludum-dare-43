package com.gempukku.ld43.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gempukku.ld43.model.GameScreenComponent;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

@RegisterSystem
public class BackgroundRenderSystem extends AbstractLifeCycleSystem {
    private SpriteBatch spriteBatch;
    private Texture wallpaper;

    @Override
    public void initialize() {
        wallpaper = new Texture(Gdx.files.internal("images/wallpaper.png"));
        wallpaper.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        spriteBatch = new SpriteBatch();
    }

    @ReceiveEvent(priority = 1)
    public void renderBackground(RenderToPipeline renderToPipeline, EntityRef camera, GameScreenComponent gameScreen) {
        int width = renderToPipeline.getWidth();
        int height = renderToPipeline.getHeight();

        TextureRegion region = new TextureRegion(wallpaper, 0, 0,
                1f * width / wallpaper.getWidth(), 1f * height / wallpaper.getHeight());

        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        spriteBatch.begin();
        spriteBatch.draw(region, 0, 0, width, height);
        spriteBatch.end();
        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    @Override
    public void destroy() {
        wallpaper.dispose();
        spriteBatch.dispose();
    }
}
