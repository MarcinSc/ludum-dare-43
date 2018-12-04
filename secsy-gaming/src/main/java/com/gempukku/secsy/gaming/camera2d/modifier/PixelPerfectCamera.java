package com.gempukku.secsy.gaming.camera2d.modifier;

import com.badlogic.gdx.math.MathUtils;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.camera2d.Adjust2dCamera;
import com.gempukku.secsy.gaming.camera2d.component.PixelPerfectCameraComponent;

import static com.gempukku.secsy.gaming.camera2d.Camera2DProvider.CAMERA_2D_PROFILE;

@RegisterSystem(profiles = CAMERA_2D_PROFILE)
public class PixelPerfectCamera {
    @ReceiveEvent(priorityName = "gaming.camera2d.pixelPerfect")
    public void adjustCamera(Adjust2dCamera cameraLocation, EntityRef camera, PixelPerfectCameraComponent pixelPerfectCamera) {
        float screenHeight = cameraLocation.getViewportHeight();

        float objectSpriteHeight = pixelPerfectCamera.getExampleObjectHeightInPixels();
        float intendedObjectPropotion = pixelPerfectCamera.getPartOfScreenItShouldTake();

        float pixelSize = screenHeight * intendedObjectPropotion / objectSpriteHeight;
        int pixelScale = Math.max(1, MathUtils.round(pixelSize));

        float objectHeightInUnits = pixelPerfectCamera.getExampleObjectHeightInUnits();
        float objectHeightInPixels = pixelScale * objectSpriteHeight / objectHeightInUnits;

        float visibleHeight = screenHeight / objectHeightInPixels;
        float visibleWidth = visibleHeight * cameraLocation.getViewportWidth() / screenHeight;

        cameraLocation.setViewport(visibleWidth, visibleHeight);
    }
}
