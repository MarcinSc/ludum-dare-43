package com.gempukku.ld43.logic.spawn;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.component.Position2DComponent;

@RegisterSystem
public class SpawnSystem {
    @Inject
    private EntityManager entityManager;

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
