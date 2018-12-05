package com.gempukku.secsy.gaming.rendering.postprocess.bloom;

import com.gempukku.secsy.entity.Component;

public interface BloomComponent extends Component {
    long getEffectStart();

    void setEffectStart(long effectStart);

    long getEffectDuration();

    void setEffectDuration(long effectDuration);

    String getBlurRadiusRecipe();

    void setBlurRadiusRecipe(String blurRadiusRecipe);

    float getBlurRadiusMultiplier();

    void setBlurRadiusMultiplier(float blurRadiusMultiplier);

    String getMinimalBrightnessRecipe();

    void setMinimalBrightnessRecipe(String minimalBrightnessRecipe);

    float getMinimalBrightnessMultiplier();

    void setMinimalBrightnessMultiplier(float minimalBrightnessMultiplier);

    String getBloomStrengthRecipe();

    void setBloomStrengthRecipe(String bloomStrengthRecipe);

    float getBloomStrengthMultiplier();

    void setBloomStrengthMultiplier(float bloomStrengthMultiplier);
}
