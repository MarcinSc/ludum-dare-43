package com.gempukku.secsy.gaming.rendering.postprocess.gamma;

import com.gempukku.secsy.gaming.component.TimedEffectComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;

public interface GammaComponent extends TimedEffectComponent {
    EasedValue getGammaFactor();

    void setGammaFactor(EasedValue easedValue);
}
