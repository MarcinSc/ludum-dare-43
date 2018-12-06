package com.gempukku.secsy.gaming.easing.function;

import com.gempukku.secsy.gaming.easing.EasingFunction;

public class OneEasingFunction implements EasingFunction {
    @Override
    public String getFunctionTrigger() {
        return "1";
    }

    @Override
    public float evaluateFunction(String parameter, float input) {
        return 1;
    }
}
