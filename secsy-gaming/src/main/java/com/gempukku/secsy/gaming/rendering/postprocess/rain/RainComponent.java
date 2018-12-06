package com.gempukku.secsy.gaming.rendering.postprocess.rain;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.entity.component.DefaultValue;
import com.gempukku.secsy.gaming.component.TimedEffectComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;

public interface RainComponent extends TimedEffectComponent {
    Color getRainColor();

    void setRainColor(Color rainColor);

    @DefaultValue("0")
    EasedValue getAlpha();

    void setAlpha(EasedValue easedValue);

    @DefaultValue("0")
    EasedValue getRainAngle();

    void setRainAngle(EasedValue rainAngle);

    @DefaultValue("0.7")
    EasedValue getRainAngleVariance();

    void setRainAngleVariance(EasedValue rainAngleVariance);

    // This should be really small, usually no bigger than 0.001, unless you want to simulate some crazy changing winds
    @DefaultValue("0.001")
    EasedValue getRainAngleVarianceSpeed();

    void setRainAngleVarianceSpeed(EasedValue rainAngleVarianceSpeed);
}
