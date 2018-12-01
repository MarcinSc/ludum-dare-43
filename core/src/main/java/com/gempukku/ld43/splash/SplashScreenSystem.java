package com.gempukku.ld43.splash;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.gaming.rendering.splash.SplashSeriesComponent;
import com.gempukku.secsy.gaming.rendering.splash.SplashSeriesEnded;

@RegisterSystem
public class SplashScreenSystem {
    @Inject
    private GameEntityProvider gameEntityProvider;

    @ReceiveEvent
    public void splashSeriesEnded(SplashSeriesEnded splashSeriesEnded, EntityRef cameraEntity) {
        cameraEntity.removeComponents(SplashSeriesComponent.class);
        cameraEntity.saveChanges();

        gameEntityProvider.getGameEntity().send(new GoToMenu());
    }
}
