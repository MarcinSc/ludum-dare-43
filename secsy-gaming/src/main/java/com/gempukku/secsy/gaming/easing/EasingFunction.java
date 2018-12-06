package com.gempukku.secsy.gaming.easing;

public interface EasingFunction {
    String getFunctionTrigger();

    float evaluateFunction(String parameter, float input);
}
