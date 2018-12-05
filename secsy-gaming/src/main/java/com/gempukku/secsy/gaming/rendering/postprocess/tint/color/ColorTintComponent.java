package com.gempukku.secsy.gaming.rendering.postprocess.tint.color;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.entity.Component;

public interface ColorTintComponent extends Component {
    long getEffectStart();

    void setEffectStart(long effectStart);

    long getEffectDuration();

    void setEffectDuration(long effectDuration);

    Color getColor();

    void setColor(Color color);

    String getFactorRecipe();

    void setFactorRecipe(String factorRecipe);

    float getFactorMultiplier();

    void setFactorMultiplier(float factorMultiplier);
}
