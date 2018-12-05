package com.gempukku.secsy.gaming.rendering.postprocess.tint.grain;

import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.rendering.postprocess.PostProcessEffectComponent;

public interface GrainComponent extends PostProcessEffectComponent {
    EasedValue getGrainSize();

    void setGrainSize(EasedValue easedValue);

    EasedValue getAlpha();

    void setAlpha(EasedValue easedValue);
}
