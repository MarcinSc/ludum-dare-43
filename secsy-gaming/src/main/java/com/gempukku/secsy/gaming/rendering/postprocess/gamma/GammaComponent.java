package com.gempukku.secsy.gaming.rendering.postprocess.gamma;

import com.gempukku.secsy.entity.Component;

public interface GammaComponent extends Component {
    long getEffectStart();

    void setEffectStart(long effectStart);

    long getEffectDuration();

    void setEffectDuration(long effectDuration);

    String getFactorRecipe();

    void setFactorRecipe(String factorRecipe);

    float getFactorMultiplier();

    void setFactorMultiplier(float factorMultiplier);
}
