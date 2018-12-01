package com.gempukku.ld43.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gempukku.ld43.model.GameScreenComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.context.util.PriorityCollection;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

@RegisterSystem
public class SpriteRenderer extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TextureAtlasProvider textureAtlasProvider;

    private SpriteBatch spriteBatch;

    private EntityIndex spriteEntities;
    private PriorityCollection<EntityRef> sprites = new PriorityCollection<EntityRef>();

    @Override
    public void initialize() {
        spriteBatch = new SpriteBatch();


        spriteEntities = entityIndexManager.addIndexOnComponents(SpriteComponent.class);
    }

    @ReceiveEvent
    public void renderSprites(RenderToPipeline renderToPipeline, EntityRef cameraEntity, GameScreenComponent gameScreen) {
        sprites.clear();
        for (EntityRef spriteEntity : spriteEntities) {
            SpriteComponent sprite = spriteEntity.getComponent(SpriteComponent.class);
            float priority = sprite.getPriority();
            sprites.put(spriteEntity, priority);
        }

        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();

        Camera camera = renderToPipeline.getCamera();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        renderSprites();
        spriteBatch.end();

        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    private void renderSprites() {
        for (EntityRef spriteEntity : sprites) {
            Position2DComponent position = spriteEntity.getComponent(Position2DComponent.class);
            SpriteComponent sprite = spriteEntity.getComponent(SpriteComponent.class);
            HorizontalOrientationComponent horizontal = spriteEntity.getComponent(HorizontalOrientationComponent.class);

            TextureRegion spriteTexture = textureAtlasProvider.getTexture("sprites", sprite.getFileName());
            if (horizontal != null && !horizontal.isFacingRight())
                spriteBatch.draw(spriteTexture, position.getX() + sprite.getRight(), position.getY() + sprite.getDown(),
                        sprite.getLeft() - sprite.getRight(), sprite.getUp() - sprite.getDown());
            else
                spriteBatch.draw(spriteTexture, position.getX() + sprite.getLeft(), position.getY() + sprite.getDown(),
                        sprite.getRight() - sprite.getLeft(), sprite.getUp() - sprite.getDown());
        }
    }

    @Override
    public void destroy() {
        spriteBatch.dispose();
    }
}
