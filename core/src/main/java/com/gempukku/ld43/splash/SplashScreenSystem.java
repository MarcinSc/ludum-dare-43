package com.gempukku.ld43.splash;

import com.gempukku.ld43.menu.GoToMenu;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.gempukku.secsy.gaming.rendering.pipeline.CameraEntityProvider;
import com.gempukku.secsy.gaming.rendering.postprocess.tint.grain.GrainComponent;
import com.gempukku.secsy.gaming.rendering.splash.SplashSeriesComponent;
import com.gempukku.secsy.gaming.rendering.splash.SplashSeriesEnded;
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
    public void splashSeriesEnded(SplashSeriesEnded splashSeriesEnded, EntityRef cameraEntity) {
        cameraEntity.removeComponents(SplashSeriesComponent.class);

        GrainComponent grain = cameraEntity.createComponent(GrainComponent.class);
        grain.setFactor(0f);
        grain.setGrainSize(5);

        cameraEntity.saveChanges();
    }

    @ReceiveEvent
    public void updateGrain(GameLoopUpdate gameLoopUpdate) {
        EntityRef cameraEntity = cameraEntityProvider.getCameraEntity();
        GrainComponent grain = cameraEntity.getComponent(GrainComponent.class);
        if (grain != null) {
            long time = timeManager.getTime();
            if (2000 <= time && time < 4000) {
                grain.setFactor(easingResolver.resolveValue("pow5,0-1-0", (time - 2000) / 2000f));
                cameraEntity.saveChanges();
            } else if (time >= 4000) {
                gameEntityProvider.getGameEntity().send(new GoToMenu());
            }
        }
    }
}
