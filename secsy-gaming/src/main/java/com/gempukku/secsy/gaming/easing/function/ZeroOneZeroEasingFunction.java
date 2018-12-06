package com.gempukku.secsy.gaming.easing.function;

import com.gempukku.secsy.gaming.easing.EasingFunction;

// Values between 0-0.5 are treated as between 0-1, values between 0.5-1 are treated as 1-0
public class ZeroOneZeroEasingFunction implements EasingFunction {
    @Override
    public String getFunctionTrigger() {
        return "0-1-0";
    }

    @Override
    public float evaluateFunction(String parameter, float input) {
        if (input <= 0.5)
            return input * 2;
        else
            return 2 - input * 2;
    }
}
