package com.gempukku.secsy.gaming.camera2d.component;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.DefaultValue;

public interface ZoomCameraComponent extends Component {
    @DefaultValue("1")
    float getXScale();

    @DefaultValue("1")
    float getYScale();
}
