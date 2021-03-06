package com.gempukku.ld43.provider;

import com.gempukku.ld43.menu.GoToGame;
import com.gempukku.ld43.menu.GoToMenu;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.rendering.pipeline.CameraEntityProvider;

@RegisterSystem(shared = CameraEntityProvider.class)
public class CameraEntityProviderImpl extends AbstractLifeCycleSystem implements CameraEntityProvider {
    @Inject
    private EntityManager entityManager;

    private EntityRef splashCameraEntity;
    private EntityRef menuCameraEntity;
    private EntityRef gameCameraEntity;

    private EntityRef currentCamera;

    @Override
    public void initialize() {
        splashCameraEntity = entityManager.createEntityFromPrefab("splashCameraEntity");
        menuCameraEntity = entityManager.createEntityFromPrefab("menuCameraEntity");
        gameCameraEntity = entityManager.createEntityFromPrefab("gameCameraEntity");

        currentCamera = splashCameraEntity;
    }

    @ReceiveEvent
    public void switchToMainMenu(GoToMenu goToMenu) {
        currentCamera = menuCameraEntity;
    }

    @ReceiveEvent
    public void switchToGame(GoToGame goToGame) {
        currentCamera = gameCameraEntity;
    }

    @Override
    public EntityRef getCameraEntity() {
        return currentCamera;
    }
}
