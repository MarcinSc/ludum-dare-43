package com.gempukku.secsy.gaming.easing;

public interface EasingFunction {
    String getFunctionTrigger();

    float evaluateFunction(float input);
}
