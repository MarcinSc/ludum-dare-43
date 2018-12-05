package com.gempukku.secsy.gaming.rendering.postprocess.tint.color;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.gaming.component.TimedEffectComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;

public interface ColorTintComponent extends TimedEffectComponent {
    Color getColor();

    void setColor(Color color);

    EasedValue getAlpha();

    void setAlpha(EasedValue easedValue);
}
