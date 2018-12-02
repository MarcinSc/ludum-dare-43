package com.gempukku.ld43.provider;

import com.gempukku.ld43.menu.GoToGame;
import com.gempukku.ld43.menu.GoToMenu;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;

@RegisterSystem(shared = GameEntityProvider.class)
public class GameEntityProviderImpl extends AbstractLifeCycleSystem implements GameEntityProvider {
    @Inject
    private EntityManager entityManager;

    private EntityRef splashGameLoopEntity;
    private EntityRef menuGameLoopEntity;
    private EntityRef gameGameLoopEntity;

    private EntityRef currentGameLoopEntity;

    @Override
    public void initialize() {
        splashGameLoopEntity = entityManager.createEntityFromPrefab("splashGameEntity");
        menuGameLoopEntity = entityManager.createEntityFromPrefab("menuGameEntity");
        gameGameLoopEntity = entityManager.createEntityFromPrefab("gameGameEntity");

        currentGameLoopEntity = splashGameLoopEntity;
    }


    @ReceiveEvent
    public void switchToMainMenu(GoToMenu goToMenu) {
        currentGameLoopEntity = menuGameLoopEntity;
    }

    @ReceiveEvent
    public void switchToGame(GoToGame goToGame) {
        currentGameLoopEntity = gameGameLoopEntity;
    }

    @Override
    public EntityRef getGameEntity() {
        return currentGameLoopEntity;
    }
}
