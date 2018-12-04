package com.gempukku.secsy.gaming.camera2d.component;

import com.gempukku.secsy.entity.Component;

public interface PixelPerfectCameraComponent extends Component {
    float getExampleObjectHeightInPixels();

    float getExampleObjectHeightInUnits();

    float getPartOfScreenItShouldTake();
}
