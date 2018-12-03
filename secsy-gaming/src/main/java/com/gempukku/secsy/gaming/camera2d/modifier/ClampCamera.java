package com.gempukku.secsy.gaming.camera2d.modifier;

import com.badlogic.gdx.math.MathUtils;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.camera2d.Adjust2dCamera;
import com.gempukku.secsy.gaming.camera2d.component.ClampCameraComponent;

import static com.gempukku.secsy.gaming.camera2d.Camera2DProvider.CAMERA_2D_PROFILE;

@RegisterSystem(profiles = CAMERA_2D_PROFILE)
public class ClampCamera {
    @ReceiveEvent(priorityName = "gaming.camera2d.clamp")
    public void containCamera(Adjust2dCamera cameraLocation, EntityRef cameraEntity, ClampCameraComponent clampCamera) {
        float leftmostVisible = clampCamera.getMinX() + cameraLocation.getViewportWidth() / 2;
        float rightmostVisible = clampCamera.getMaxX() - cameraLocation.getViewportWidth() / 2;
        float lowestVisible = clampCamera.getMinY() + cameraLocation.getViewportHeight() / 2;
        float highestVisible = clampCamera.getMaxY() - cameraLocation.getViewportHeight() / 2;

        cameraLocation.set(
                MathUtils.clamp(cameraLocation.getX(), leftmostVisible, rightmostVisible),
                MathUtils.clamp(cameraLocation.getY(), lowestVisible, highestVisible));
    }
}
