package com.gempukku.secsy.gaming.camera2d.modifier;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.camera2d.Adjust2dCamera;
import com.gempukku.secsy.gaming.camera2d.component.ScreenShakeCameraComponent;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.gempukku.secsy.gaming.noise.ImprovedNoise;
import com.gempukku.secsy.gaming.time.TimeManager;

import static com.gempukku.secsy.gaming.camera2d.Camera2DProvider.CAMERA_2D_PROFILE;

@RegisterSystem(profiles = CAMERA_2D_PROFILE)
public class ScreenShakeCamera {
    public static final String DEFAULT_EASING_RECIPE = "pow5,0-1-0";

    @Inject
    private TimeManager timeManager;
    @Inject
    private EasingResolver easingResolver;

    @ReceiveEvent(priorityName = "gaming.camera2d.screenShake")
    public void shakeCamera(Adjust2dCamera cameraLocation, EntityRef cameraEntity, ScreenShakeCameraComponent shakeCamera) {
        long time = timeManager.getTime();

        long shakeStartTime = shakeCamera.getShakeStartTime();
        long shakeEndTime = shakeCamera.getShakeEndTime();
        if (time > shakeStartTime && time < shakeEndTime) {
            long shakeDuration = shakeEndTime - shakeStartTime;
            float shakeSpeed = shakeCamera.getShakeSpeed();

            float shakeScaleGeneral = shakeCamera.getShakeSize();
            String shakeEasingRecipe = shakeCamera.getShakeEasingRecipe();
            if (shakeEasingRecipe == null)
                shakeEasingRecipe = DEFAULT_EASING_RECIPE;

            float noiseX = (float) ImprovedNoise.noise(time * shakeSpeed, 0, 0);
            float noiseY = (float) ImprovedNoise.noise((time + 5000) * shakeSpeed, 0, 0);

            float timeElement = 1f * (time - shakeStartTime) / shakeDuration;
            float shakeScaleFromTime = easingResolver.resolveValue(shakeEasingRecipe, timeElement);

            float sizeMultiplier = Math.min(cameraLocation.getViewportWidth(), cameraLocation.getViewportHeight());
            float totalShakeX = noiseX * shakeScaleGeneral * shakeScaleFromTime;
            float totalShakeY = noiseY * shakeScaleGeneral * shakeScaleFromTime;

            cameraLocation.setNonLastingX(totalShakeX * sizeMultiplier);
            cameraLocation.setNonLastingY(totalShakeY * sizeMultiplier);
        }
    }

    private float getShakeScale(float progress) {
        // 10% of time linear ramp up, and 10% of time linear ramp down
        if (progress < 0.1)
            return progress * 10;
        if (progress > 0.9)
            return (1 - progress) * 10;
        return 1;
    }
}
