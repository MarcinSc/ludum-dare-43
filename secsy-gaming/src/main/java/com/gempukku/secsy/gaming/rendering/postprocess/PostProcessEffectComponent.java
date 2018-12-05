package com.gempukku.secsy.gaming.rendering.postprocess;

import com.gempukku.secsy.entity.Component;

public interface PostProcessEffectComponent extends Component {
    long getEffectStart();

    void setEffectStart(long effectStart);

    long getEffectDuration();

    void setEffectDuration(long effectDuration);
}
