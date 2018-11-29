package com.gempukku.secsy.gaming.camera2d.modifier;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.camera2d.Adjust2dCamera;
import com.gempukku.secsy.gaming.camera2d.component.ZoomCameraComponent;

import static com.gempukku.secsy.gaming.camera2d.Camera2DProvider.CAMERA_2D_PROFILE;

@RegisterSystem(profiles = CAMERA_2D_PROFILE)
public class ZoomCamera {
    @ReceiveEvent(priorityName = "gaming.camera2d.zoom")
    public void zoomCamera(Adjust2dCamera cameraLocation, EntityRef cameraEntity, ZoomCameraComponent zoomCamera) {
        cameraLocation.setViewport(
                cameraLocation.getViewportWidth() * zoomCamera.getXScale(),
                cameraLocation.getViewportHeight() * zoomCamera.getYScale());
    }
}
