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
        cameraLocation.set(
                MathUtils.clamp(cameraLocation.getX(), clampCamera.getMinX(), clampCamera.getMaxX()),
                MathUtils.clamp(cameraLocation.getY(), clampCamera.getMinY(), clampCamera.getMaxY()));
    }
}
