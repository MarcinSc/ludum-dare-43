package com.gempukku.secsy.gaming.rendering.postprocess.bloom;

import com.gempukku.secsy.gaming.component.TimedEffectComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;

public interface BloomComponent extends TimedEffectComponent {
    EasedValue getBlurRadius();

    void setBlurRadius(EasedValue easedValue);

    EasedValue getMinimalBrightness();

    void setMinimalBrightness(EasedValue easedValue);

    EasedValue getBloomStrength();

    void setBloomStrength(EasedValue easedValue);
}
