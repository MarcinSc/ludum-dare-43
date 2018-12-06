package com.gempukku.secsy.gaming.rendering.postprocess.shockwave;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.gaming.component.TimedEffectComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;

public interface ColorShockWaveComponent extends TimedEffectComponent {
    Vector3 getPosition();

    void setPosition(Vector3 position);

    Color getColor();

    void setColor(Color color);

    EasedValue getDistance();

    void setDistance(EasedValue distance);

    EasedValue getSize();

    void setSize(EasedValue size);

    EasedValue getAlpha();

    void setAlpha(EasedValue alpha);

    EasedValue getNoiseVariance();

    void setNoiseVariance(EasedValue noiseVariance);

    EasedValue getNoiseImpact();

    void setNoiseImpact(EasedValue noiseImpact);
}
