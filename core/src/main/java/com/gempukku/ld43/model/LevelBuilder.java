package com.gempukku.ld43.model;

import com.gempukku.ld43.menu.GoToGame;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.basic2d.ObstacleComponent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorTriggerComponent;

@RegisterSystem
public class LevelBuilder extends AbstractLifeCycleSystem {
    @Inject
    private EntityManager entityManager;

    @ReceiveEvent
    public void initialize(GoToGame goToGame) {
        createPlayer();
        createPlatforms();
        createEnemies();
        createExits();
    }

    private void createExits() {
        EntityRef levelExit = entityManager.createEntityFromPrefab("levelExit");
        Position2DComponent position = levelExit.getComponent(Position2DComponent.class);
        position.setX(5.5f);
        position.setY(1.1f);
        levelExit.saveChanges();
    }

    private void createEnemies() {
        createEnemy("patrollingEnemy", 2f, 0f);
    }

    private void createPlatforms() {
        createPlatform(0f, -0.1f, 3f, 0.1f);
        createPlatform(3f, 1f, 3f, 0.1f);
    }

    private void createEnemy(String prefab, float x, float y) {
        EntityRef enemy = entityManager.createEntityFromPrefab(prefab);
        Position2DComponent position = enemy.getComponent(Position2DComponent.class);
        position.setX(x);
        position.setY(y);
        enemy.saveChanges();
    }

    private void createPlayer() {
        EntityRef playerEntity = entityManager.createEntityFromPrefab("playerEntity");
        Position2DComponent position = playerEntity.getComponent(Position2DComponent.class);
        position.setX(0.1f);
        position.setY(3);
        playerEntity.saveChanges();
    }

    private void createPlatform(float x, float y, float width, float height) {
        EntityRef platformEntity = entityManager.createEntityFromPrefab("platform");

        Position2DComponent position = platformEntity.getComponent(Position2DComponent.class);
        position.setX(x);
        position.setY(y);

        ObstacleComponent obstacle = platformEntity.getComponent(ObstacleComponent.class);
        obstacle.setRight(width);
        obstacle.setUp(height);

        SensorTriggerComponent sensorTrigger = platformEntity.getComponent(SensorTriggerComponent.class);
        sensorTrigger.setRight(width);
        sensorTrigger.setUp(height);

        PlatformComponent platform = platformEntity.getComponent(PlatformComponent.class);
        platform.setRight(width);
        platform.setUp(height);

        platformEntity.saveChanges();
    }

    @Override
    public float getPriority() {
        return -1;
    }
}
