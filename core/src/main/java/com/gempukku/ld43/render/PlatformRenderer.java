package com.gempukku.ld43.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gempukku.ld43.model.GameScreenComponent;
import com.gempukku.ld43.model.PlatformComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

@RegisterSystem
public class PlatformRenderer extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TextureAtlasProvider textureAtlasProvider;

    private SpriteBatch spriteBatch;

    private EntityIndex platformEntities;

    @Override
    public void initialize() {
        spriteBatch = new SpriteBatch();

        platformEntities = entityIndexManager.addIndexOnComponents(PlatformComponent.class);
    }

    @ReceiveEvent
    public void renderToPipeline(RenderToPipeline renderToPipeline, EntityRef cameraEntity, GameScreenComponent gameScreen) {
        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();

        Camera camera = renderToPipeline.getCamera();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        renderPlatforms();
        spriteBatch.end();

        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    private void renderPlatforms() {
        TextureRegion floor = textureAtlasProvider.getTexture("sprites", "images/floor.png");

        for (EntityRef platformEntity : platformEntities) {
            Position2DComponent position = platformEntity.getComponent(Position2DComponent.class);
            PlatformComponent platform = platformEntity.getComponent(PlatformComponent.class);
            spriteBatch.draw(floor, position.getX() + platform.getLeft(), position.getY() + platform.getDown(),
                    platform.getRight() - platform.getLeft(), platform.getUp() - platform.getDown());
        }

    }

    @Override
    public void destroy() {
        spriteBatch.dispose();
    }
}
