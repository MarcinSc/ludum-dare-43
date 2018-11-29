package com.gempukku.secsy.gaming.camera2d.modifier;

import com.badlogic.gdx.math.MathUtils;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.camera2d.Adjust2dCamera;
import com.gempukku.secsy.gaming.camera2d.component.LerpCameraComponent;
import com.gempukku.secsy.gaming.time.TimeManager;

import static com.gempukku.secsy.gaming.camera2d.Camera2DProvider.CAMERA_2D_PROFILE;

@RegisterSystem(profiles = CAMERA_2D_PROFILE)
public class LerpCamera {
    @Inject
    private TimeManager timeManager;

    @ReceiveEvent(priorityName = "gaming.camera2d.lerp")
    public void lerpCamera(Adjust2dCamera cameraLocation, EntityRef cameraEntity, LerpCameraComponent lerpCamera) {
        float progressX = lerpCamera.getXProgress() * timeManager.getTimeSinceLastUpdate() / 1000f;
        float progressY = lerpCamera.getYProgress() * timeManager.getTimeSinceLastUpdate() / 1000f;
        float lerpedX = MathUtils.lerp(cameraLocation.getLastX(), cameraLocation.getX(), progressX);
        float lerpedY = MathUtils.lerp(cameraLocation.getLastY(), cameraLocation.getY(), progressY);
        cameraLocation.set(lerpedX, lerpedY);
    }
}
