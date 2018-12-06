package com.gempukku.secsy.gaming.rendering.postprocess.shockwave;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.component.DefaultValue;
import com.gempukku.secsy.gaming.component.TimedEffectComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;

public interface DisplacementShockWaveComponent extends TimedEffectComponent {
    Vector3 getPosition();

    void setPosition(Vector3 position);

    EasedValue getDistance();

    void setDistance(EasedValue distance);

    EasedValue getSize();

    void setSize(EasedValue size);

    @DefaultValue("1")
    EasedValue getAlpha();

    void setAlpha(EasedValue alpha);

    @DefaultValue("1")
    EasedValue getNoiseVariance();

    void setNoiseVariance(EasedValue noiseVariance);

    @DefaultValue("0")
    EasedValue getNoiseImpact();

    void setNoiseImpact(EasedValue noiseImpact);
}
