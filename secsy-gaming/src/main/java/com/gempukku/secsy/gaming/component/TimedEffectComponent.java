package com.gempukku.secsy.gaming.component;

import com.gempukku.secsy.entity.Component;

public interface TimedEffectComponent extends Component {
    long getEffectStart();

    void setEffectStart(long effectStart);

    long getEffectDuration();

    void setEffectDuration(long effectDuration);
}
