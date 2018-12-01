package com.gempukku.ld43.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
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
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

@RegisterSystem
public class PlatformRenderer extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;

    private Texture texture;
    private SpriteBatch spriteBatch;

    private EntityIndex platformEntities;

    @Override
    public void initialize() {
        spriteBatch = new SpriteBatch();

        platformEntities = entityIndexManager.addIndexOnComponents(PlatformComponent.class);

        texture = new Texture(Gdx.files.internal("images/floor.png"));
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
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
        for (EntityRef platformEntity : platformEntities) {
            Position2DComponent position = platformEntity.getComponent(Position2DComponent.class);
            PlatformComponent platform = platformEntity.getComponent(PlatformComponent.class);
            float width = platform.getRight() - platform.getLeft();
            float height = platform.getUp() - platform.getDown();
            TextureRegion floor = new TextureRegion(texture, 0, 0, width / height, 1);
            spriteBatch.draw(floor, position.getX() + platform.getLeft(), position.getY() + platform.getDown(),
                    width, height);
        }

    }

    @Override
    public void destroy() {
        texture.dispose();
        spriteBatch.dispose();
    }
}
