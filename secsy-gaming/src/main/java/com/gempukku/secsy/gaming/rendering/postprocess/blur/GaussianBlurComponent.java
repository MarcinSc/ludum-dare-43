package com.gempukku.secsy.gaming.rendering.postprocess.blur;

import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.rendering.postprocess.PostProcessEffectComponent;

public interface GaussianBlurComponent extends PostProcessEffectComponent {
    EasedValue getBlurRadius();

    void setBlurRadius(EasedValue easedValue);
}
