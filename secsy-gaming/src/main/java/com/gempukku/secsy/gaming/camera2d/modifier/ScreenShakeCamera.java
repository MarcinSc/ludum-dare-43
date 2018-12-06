package com.gempukku.secsy.gaming.camera2d.modifier;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.camera2d.Adjust2dCamera;
import com.gempukku.secsy.gaming.camera2d.component.ScreenShakeCameraComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.gempukku.secsy.gaming.noise.ImprovedNoise;
import com.gempukku.secsy.gaming.time.TimeManager;

import static com.gempukku.secsy.gaming.camera2d.Camera2DProvider.CAMERA_2D_PROFILE;

@RegisterSystem(profiles = CAMERA_2D_PROFILE)
public class ScreenShakeCamera {
    @Inject
    private TimeManager timeManager;
    @Inject
    private EasingResolver easingResolver;

    @ReceiveEvent(priorityName = "gaming.camera2d.screenShake")
    public void shakeCamera(Adjust2dCamera cameraLocation, EntityRef cameraEntity, ScreenShakeCameraComponent shakeCamera) {
        long time = timeManager.getTime();

        long effectStart = shakeCamera.getEffectStart();
        long effectDuration = shakeCamera.getEffectDuration();
        if (effectStart <= time && time < effectStart + effectDuration) {
            float alpha = 1f * (time - effectStart) / effectDuration;
            float shakeSpeed = easingResolver.resolveValue(shakeCamera.getShakeSpeed(), alpha);

            EasedValue shakeSizeValue = shakeCamera.getShakeSize();
            float shakeSize = easingResolver.resolveValue(shakeSizeValue, alpha);

            float noiseX = ImprovedNoise.noise(time * shakeSpeed, 0, 0);
            float noiseY = ImprovedNoise.noise((time + 5000) * shakeSpeed, 0, 0);

            float sizeMultiplier = Math.min(cameraLocation.getViewportWidth(), cameraLocation.getViewportHeight());
            float totalShakeX = noiseX * shakeSize;
            float totalShakeY = noiseY * shakeSize;

            cameraLocation.setNonLastingX(totalShakeX * sizeMultiplier);
            cameraLocation.setNonLastingY(totalShakeY * sizeMultiplier);
        }
    }
}
