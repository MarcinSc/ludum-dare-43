package com.gempukku.ld43.render;

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
import com.gempukku.secsy.gaming.rendering.sprite.GatherSprites;
import com.gempukku.secsy.gaming.rendering.sprite.SpriteRenderer;

@RegisterSystem
public class PlatformRenderer extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex platformEntities;

    @Override
    public void initialize() {
        platformEntities = entityIndexManager.addIndexOnComponents(PlatformComponent.class);
    }

    @ReceiveEvent
    public void renderPlatforms(GatherSprites gatherSprites, EntityRef cameraEntity, GameScreenComponent gameScreen) {
        SpriteRenderer.SpriteSink spriteSink = gatherSprites.getSpriteSink();
        for (EntityRef platformEntity : platformEntities) {
            Position2DComponent position = platformEntity.getComponent(Position2DComponent.class);
            PlatformComponent platform = platformEntity.getComponent(PlatformComponent.class);
            float width = platform.getRight() - platform.getLeft();
            float height = platform.getUp() - platform.getDown();
            float x = position.getX() + platform.getLeft();
            float y = position.getY() + platform.getDown();
            spriteSink.addTiledSprite(5, "images/floor.png", x, y, width, height, width / height, 1);
        }
    }
}
