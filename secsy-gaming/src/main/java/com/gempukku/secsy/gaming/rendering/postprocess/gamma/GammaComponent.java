package com.gempukku.secsy.gaming.rendering.postprocess.gamma;

import com.gempukku.secsy.entity.Component;

public interface GammaComponent extends Component {
    float getFactor();

    void setFactor(float factor);
}
