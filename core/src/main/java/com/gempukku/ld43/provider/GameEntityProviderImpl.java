package com.gempukku.ld43.provider;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.game.GameEntityProvider;

@RegisterSystem(shared = GameEntityProvider.class)
public class GameEntityProviderImpl extends AbstractLifeCycleSystem implements GameEntityProvider {
    @Inject
    private EntityManager entityManager;

    private EntityRef gameLoopEntity;

    @Override
    public void initialize() {
        gameLoopEntity = entityManager.createEntityFromPrefab("gameEntity");
    }

    @Override
    public EntityRef getGameEntity() {
        return gameLoopEntity;
    }
}
