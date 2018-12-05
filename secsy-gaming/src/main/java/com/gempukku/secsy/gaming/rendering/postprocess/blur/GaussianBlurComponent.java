package com.gempukku.secsy.gaming.rendering.postprocess.blur;

import com.gempukku.secsy.entity.Component;

public interface GaussianBlurComponent extends Component {
    long getEffectStart();

    void setEffectStart(long effectStart);

    long getEffectDuration();

    void setEffectDuration(long effectDuration);

    String getBlurRadiusRecipe();

    void setBlurRadiusRecipe(String blurRadiusRecipe);

    float getBlurRadiusMultiplier();

    void setBlurRadiusMultiplier(float blurRadiusMultiplier);
}
