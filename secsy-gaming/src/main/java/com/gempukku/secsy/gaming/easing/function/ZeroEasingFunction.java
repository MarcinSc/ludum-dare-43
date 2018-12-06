package com.gempukku.secsy.gaming.easing.function;

import com.gempukku.secsy.gaming.easing.EasingFunction;

public class ZeroEasingFunction implements EasingFunction {
    @Override
    public String getFunctionTrigger() {
        return "0";
    }

    @Override
    public float evaluateFunction(String parameter, float input) {
        return 0;
    }
}
