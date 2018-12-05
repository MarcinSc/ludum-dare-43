package com.gempukku.secsy.gaming.rendering.postprocess.tint.grain;

import com.gempukku.secsy.gaming.component.TimedEffectComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;

public interface GrainComponent extends TimedEffectComponent {
    EasedValue getGrainSize();

    void setGrainSize(EasedValue easedValue);

    EasedValue getAlpha();

    void setAlpha(EasedValue easedValue);
}
