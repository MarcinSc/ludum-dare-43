package com.gempukku.secsy.gaming.camera2d.component;

import com.gempukku.secsy.entity.Component;

public interface ClampCameraComponent extends Component {
    float getMinX();

    float getMaxX();

    float getMinY();

    float getMaxY();
}
