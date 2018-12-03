package com.gempukku.ld43.logic.spawn;

import com.gempukku.ld43.model.SpawningComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.basic2d.MovingComponent;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem
public class SpawnSystem extends AbstractLifeCycleSystem {
    @Inject
    private EntityManager entityManager;
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;

    private EntityIndex spawningEntities;

    @Override
    public void initialize() {
        spawningEntities = entityIndexManager.addIndexOnComponents(SpawningComponent.class);
    }

    @ReceiveEvent
    public void spawnEntities(GameLoopUpdate gameLoop) {
        long time = timeManager.getTime();
        for (EntityRef spawningEntity : spawningEntities) {
            SpawningComponent spawning = spawningEntity.getComponent(SpawningComponent.class);
            if (time > spawning.getLastSpawnTime() + spawning.getFrequency()) {
                String prefab = spawning.getPrefab();
                spawning.setLastSpawnTime(time);
                spawningEntity.saveChanges();

                EntityRef spawned = entityManager.createEntityFromPrefab(prefab);
                Position2DComponent spawnedPosition = spawned.getComponent(Position2DComponent.class);
                if (spawnedPosition != null) {
                    Position2DComponent spawningPosition = spawningEntity.getComponent(Position2DComponent.class);
                    spawnedPosition.setX(spawningPosition.getX());
                    spawnedPosition.setY(spawningPosition.getY());

                    MovingComponent moving = spawned.getComponent(MovingComponent.class);
                    HorizontalOrientationComponent orientation = spawningEntity.getComponent(HorizontalOrientationComponent.class);
                    if (moving != null && orientation != null) {
                        float speedX = moving.getSpeedX();
                        moving.setSpeedX(orientation.isFacingRight() ? speedX : -speedX);
                    }

                    spawned.saveChanges();
                }
            }
        }
    }

    @ReceiveEvent
    public void spawnEntity(SpawnEntity spawnEntity, EntityRef spawningEntity) {
        EntityRef entity = entityManager.createEntityFromPrefab(spawnEntity.getPrefab());
        Position2DComponent position = entity.getComponent(Position2DComponent.class);
        if (position != null) {
            position.setX(spawnEntity.getX());
            position.setY(spawnEntity.getY());
            entity.saveChanges();
        }
    }
}
