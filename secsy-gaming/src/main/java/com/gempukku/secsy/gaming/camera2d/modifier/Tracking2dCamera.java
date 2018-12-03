package com.gempukku.secsy.gaming.camera2d.modifier;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.camera2d.Adjust2dCamera;
import com.gempukku.secsy.gaming.camera2d.component.CameraTrackedComponent;
import com.gempukku.secsy.gaming.camera2d.component.TrackingCameraComponent;
import com.gempukku.secsy.gaming.component.GroundedComponent;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;

import static com.gempukku.secsy.gaming.camera2d.Camera2DProvider.CAMERA_2D_PROFILE;

@RegisterSystem(profiles = CAMERA_2D_PROFILE)
public class Tracking2dCamera extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    private EntityIndex cameraTrackedEntities;

    @Override
    public void initialize() {
        cameraTrackedEntities = entityIndexManager.addIndexOnComponents(CameraTrackedComponent.class);
    }

    @ReceiveEvent(priorityName = "gaming.camera2d.tracking")
    public void trackObject(Adjust2dCamera cameraLocation, EntityRef cameraEntity, TrackingCameraComponent trackingCamera) {
        EntityRef firstTracked = getTrackedEntity();
        if (firstTracked != null) {
            Position2DComponent position = firstTracked.getComponent(Position2DComponent.class);
            CameraTrackedComponent cameraTracked = firstTracked.getComponent(CameraTrackedComponent.class);
            float newX = position.getX() + cameraTracked.getX();
            newX += getOrientationBasedDifference(cameraLocation, trackingCamera, firstTracked);
            cameraLocation.setX(newX);
            float y = position.getY() + cameraTracked.getY();
            if (trackingCamera.isFollowingNotGrounded()) {
                cameraLocation.setY(y);
            } else {
                GroundedComponent grounded = firstTracked.getComponent(GroundedComponent.class);
                if (grounded.isGrounded()) {
                    cameraLocation.setY(y);
                    trackingCamera.setLastGroundY(y);
                    cameraEntity.saveChanges();
                } else {
                    cameraLocation.setY(trackingCamera.getLastGroundY());
                }
            }
        } else {
            cameraLocation.setX(cameraLocation.getLastX());
            cameraLocation.setY(cameraLocation.getLastY());
        }

    }

    private EntityRef getTrackedEntity() {
        for (EntityRef cameraTrackedEntity : cameraTrackedEntities) {
            return cameraTrackedEntity;
        }

        return null;
    }

    private float getOrientationBasedDifference(Adjust2dCamera cameraLocation, TrackingCameraComponent trackingCamera, EntityRef firstTracked) {
        float orientationBasedDistance = trackingCamera.getOrientationBasedDistance();
        if (orientationBasedDistance != 0) {
            HorizontalOrientationComponent horizontalOrientation = firstTracked.getComponent(HorizontalOrientationComponent.class);
            if (horizontalOrientation != null && horizontalOrientation.isFacingRight())
                return orientationBasedDistance * cameraLocation.getViewportWidth();
            else if (horizontalOrientation != null && !horizontalOrientation.isFacingRight())
                return -orientationBasedDistance * cameraLocation.getViewportWidth();
        }
        return 0;
    }
}
