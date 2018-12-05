package com.gempukku.secsy.gaming.rendering.postprocess.bloom;

import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.rendering.postprocess.PostProcessEffectComponent;

public interface BloomComponent extends PostProcessEffectComponent {
    EasedValue getBlurRadius();

    void setBlurRadius(EasedValue easedValue);

    EasedValue getMinimalBrightness();

    void setMinimalBrightness(EasedValue easedValue);

    EasedValue getBloomStrength();

    void setBloomStrength(EasedValue easedValue);
}
