package com.gempukku.secsy.gaming.rendering.postprocess.blur;

import com.gempukku.secsy.gaming.component.TimedEffectComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;

public interface GaussianBlurComponent extends TimedEffectComponent {
    EasedValue getBlurRadius();

    void setBlurRadius(EasedValue easedValue);
}
