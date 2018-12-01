package com.gempukku.ld43.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gempukku.ld43.model.*;
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
public class LevelRenderSystem extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;

    private ShapeRenderer shapeRenderer;

    private EntityIndex playerEntities;
    private EntityIndex platformEntities;
    private EntityIndex enemyEntities;
    private EntityIndex completionEntities;

    @Override
    public void initialize() {
        shapeRenderer = new ShapeRenderer();

        playerEntities = entityIndexManager.addIndexOnComponents(PlayerComponent.class);
        platformEntities = entityIndexManager.addIndexOnComponents(PlatformComponent.class);
        enemyEntities = entityIndexManager.addIndexOnComponents(EnemyComponent.class);
        completionEntities = entityIndexManager.addIndexOnComponents(CompletionComponent.class);
    }

    @ReceiveEvent
    public void renderToPipeline(RenderToPipeline renderToPipeline, EntityRef cameraEntity, GameScreenComponent gameScreen) {
        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();

        shapeRenderer.setProjectionMatrix(renderToPipeline.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        renderPlatforms();

        renderEnemies();

        renderPlayer();

        renderExit();

        shapeRenderer.end();

        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    private void renderExit() {
        shapeRenderer.setColor(Color.BLUE);
        for (EntityRef completionEntity : completionEntities) {
            Position2DComponent position = completionEntity.getComponent(Position2DComponent.class);
            CompletionComponent completion = completionEntity.getComponent(CompletionComponent.class);
            shapeRenderer.rect(position.getX() + completion.getLeft(), position.getY() + completion.getDown(),
                    completion.getRight() - completion.getLeft(), completion.getUp() - completion.getDown());
        }
    }

    private void renderEnemies() {
        shapeRenderer.setColor(Color.RED);
        for (EntityRef enemyEntity : enemyEntities) {
            Position2DComponent position = enemyEntity.getComponent(Position2DComponent.class);
            EnemyComponent ememy = enemyEntity.getComponent(EnemyComponent.class);
            shapeRenderer.rect(position.getX() + ememy.getLeft(), position.getY() + ememy.getDown(),
                    ememy.getRight() - ememy.getLeft(), ememy.getUp() - ememy.getDown());
        }
    }

    private void renderPlayer() {
        shapeRenderer.setColor(Color.GREEN);
        for (EntityRef playerEntity : playerEntities) {
            Position2DComponent position = playerEntity.getComponent(Position2DComponent.class);
            PlayerComponent player = playerEntity.getComponent(PlayerComponent.class);
            shapeRenderer.rect(position.getX() + player.getLeft(), position.getY() + player.getDown(),
                    player.getRight() - player.getLeft(), player.getUp() - player.getDown());
        }
    }

    private void renderPlatforms() {
        shapeRenderer.setColor(Color.YELLOW);
        for (EntityRef platformEntity : platformEntities) {
            Position2DComponent position = platformEntity.getComponent(Position2DComponent.class);
            PlatformComponent platform = platformEntity.getComponent(PlatformComponent.class);
            shapeRenderer.rect(position.getX() + platform.getLeft(), position.getY() + platform.getDown(),
                    platform.getRight() - platform.getLeft(), platform.getUp() - platform.getDown());
        }
    }

    @Override
    public void destroy() {
        shapeRenderer.dispose();
    }
}
