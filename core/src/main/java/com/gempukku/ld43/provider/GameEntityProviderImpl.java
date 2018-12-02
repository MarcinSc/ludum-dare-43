package com.gempukku.ld43.provider;

import com.gempukku.ld43.logic.level.GameStarted;
import com.gempukku.ld43.menu.GoToGame;
import com.gempukku.ld43.menu.GoToMenu;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.gaming.time.TimeEntityProvider;

@RegisterSystem(shared = {GameEntityProvider.class, TimeEntityProvider.class})
public class GameEntityProviderImpl extends AbstractLifeCycleSystem implements GameEntityProvider, TimeEntityProvider {
    @Inject
    private EntityManager entityManager;

    private EntityRef currentGameLoopEntity;

    @Override
    public void initialize() {
        currentGameLoopEntity = entityManager.createEntityFromPrefab("splashGameEntity");
    }


    @ReceiveEvent(priority = -100)
    public void switchToMainMenu(GoToMenu goToMenu) {
        entityManager.destroyEntity(currentGameLoopEntity);
        currentGameLoopEntity = entityManager.createEntityFromPrefab("menuGameEntity");
    }

    @ReceiveEvent(priority = -100)
    public void switchToGame(GoToGame goToGame) {
        entityManager.destroyEntity(currentGameLoopEntity);
        currentGameLoopEntity = entityManager.createEntityFromPrefab("gameGameEntity");
        currentGameLoopEntity.send(new GameStarted());
    }

    @Override
    public EntityRef getTimeEntity() {
        return currentGameLoopEntity;
    }

    @Override
    public EntityRef getGameEntity() {
        return currentGameLoopEntity;
    }
}
