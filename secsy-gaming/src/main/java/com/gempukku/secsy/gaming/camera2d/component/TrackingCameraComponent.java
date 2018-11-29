package com.gempukku.secsy.gaming.camera2d.component;

import com.gempukku.secsy.entity.Component;

public interface TrackingCameraComponent extends Component {
    boolean isFollowingNotGrounded();

    float getOrientationBasedDistance();

    void setLastGroundY(float lastGroundY);

    float getLastGroundY();
}
