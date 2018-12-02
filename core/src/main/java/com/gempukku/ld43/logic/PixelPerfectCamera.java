package com.gempukku.ld43.logic;

import com.badlogic.gdx.math.MathUtils;
import com.gempukku.ld43.model.GameScreenComponent;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.camera2d.Adjust2dCamera;

@RegisterSystem
public class PixelPerfectCamera {
    @ReceiveEvent(priority = 100)
    public void adjustCamera(Adjust2dCamera cameraLocation, EntityRef camera, GameScreenComponent gameScreen) {
        float screenHeight = cameraLocation.getViewportHeight();

        int characterSpriteSize = 32;
        float intendedCharacterPropotion = 0.1f;

        float pixelSize = screenHeight * intendedCharacterPropotion / characterSpriteSize;
        int pixelScale = Math.max(1, MathUtils.round(pixelSize));

        float characterHeight = 0.2f;
        float characterHeightInPixels = pixelScale * characterSpriteSize / characterHeight;

        float visibleHeight = screenHeight / characterHeightInPixels;
        float visibleWidth = visibleHeight * cameraLocation.getViewportWidth() / screenHeight;

        cameraLocation.setViewport(visibleWidth, visibleHeight);
    }
}
