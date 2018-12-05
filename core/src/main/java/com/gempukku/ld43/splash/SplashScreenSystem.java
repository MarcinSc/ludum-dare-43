package com.gempukku.ld43.splash;

import com.gempukku.ld43.menu.GoToMenu;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.gempukku.secsy.gaming.rendering.pipeline.CameraEntityProvider;
import com.gempukku.secsy.gaming.rendering.splash.SplashSeriesComponent;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem
public class SplashScreenSystem {
    @Inject
    private GameEntityProvider gameEntityProvider;
    @Inject
    private CameraEntityProvider cameraEntityProvider;
    @Inject
    private TimeManager timeManager;
    @Inject
    private EasingResolver easingResolver;

    @ReceiveEvent
    public void detectSplashEnd(GameLoopUpdate gameLoopUpdate) {
        if (cameraEntityProvider.getCameraEntity().hasComponent(SplashSeriesComponent.class)) {
            long time = timeManager.getTime();
            if (time >= 4000) {
                gameEntityProvider.getGameEntity().send(new GoToMenu());
            }
        }
    }
}
