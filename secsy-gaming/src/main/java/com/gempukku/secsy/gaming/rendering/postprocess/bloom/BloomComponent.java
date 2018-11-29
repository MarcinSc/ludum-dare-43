package com.gempukku.secsy.gaming.rendering.postprocess.bloom;

import com.gempukku.secsy.entity.Component;

public interface BloomComponent extends Component {
    float getBlurRadius();

    void setBlurRadius(float blurRadius);

    float getMinimalBrightness();

    void setMinimalBrightness(float minimalBrightness);

    float getBloomStrength();

    void setBloomStrength(float bloomStrength);
}
