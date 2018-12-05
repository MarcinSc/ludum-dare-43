package com.gempukku.secsy.gaming.rendering.postprocess.tint.color;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.rendering.postprocess.PostProcessEffectComponent;

public interface ColorTintComponent extends PostProcessEffectComponent {
    Color getColor();

    void setColor(Color color);

    EasedValue getAlpha();

    void setAlpha(EasedValue easedValue);
}
