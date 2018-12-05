package com.gempukku.secsy.gaming.rendering.postprocess.gamma;

import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.rendering.postprocess.PostProcessEffectComponent;

public interface GammaComponent extends PostProcessEffectComponent {
    EasedValue getGammaFactor();

    void setGammaFactor(EasedValue easedValue);
}
