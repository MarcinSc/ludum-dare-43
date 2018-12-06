package com.gempukku.secsy.gaming.camera2d.component;

import com.gempukku.secsy.entity.component.DefaultValue;
import com.gempukku.secsy.gaming.component.TimedEffectComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;

public interface ScreenShakeCameraComponent extends TimedEffectComponent {
    // This should be somewhere around 0.01 to look good
    @DefaultValue("0.01")
    EasedValue getShakeSpeed();

    void setShakeSpeed(EasedValue shakeSpeed);

    // How much the shake amplitude is (in screen size %)
    // If overwritten, it's advisable to use 0-1-0 as the last function in the recipe,
    // that way - the shake will die down, rather than stop abruptly.
    @DefaultValue("0.1*pow5,0-1-0")
    EasedValue getShakeSize();

    void setShakeSize(EasedValue shakeSize);
}
