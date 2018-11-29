package com.gempukku.secsy.gaming.camera2d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.camera2d.component.Camera2DComponent;
import com.gempukku.secsy.gaming.rendering.pipeline.GetCamera;

import static com.gempukku.secsy.gaming.camera2d.Camera2DProvider.CAMERA_2D_PROFILE;

@RegisterSystem(profiles = CAMERA_2D_PROFILE)
public class Camera2DProvider {
    public static final String CAMERA_2D_PROFILE = "2dCamera";

    private float lastX;
    private float lastY;
    private Camera camera = new OrthographicCamera();

    @ReceiveEvent
    public void getCamera(GetCamera getCamera, EntityRef cameraEntity, Camera2DComponent camera2D) {
        Adjust2dCamera cameraLocation = new Adjust2dCamera(
                camera.viewportWidth, camera.viewportHeight,
                getCamera.getWidth(), getCamera.getHeight(),
                lastX, lastY,
                0, 0);
        cameraEntity.send(cameraLocation);

        camera.viewportWidth = cameraLocation.getViewportWidth();
        camera.viewportHeight = cameraLocation.getViewportHeight();
        lastX = cameraLocation.getX();
        lastY = cameraLocation.getY();
        camera.position.set(lastX + cameraLocation.getNonLastingX(), lastY + cameraLocation.getNonLastingY(), 0);
        camera.update();

        getCamera.setCamera(camera);
    }
}
