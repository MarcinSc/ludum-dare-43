package com.gempukku.secsy.gaming.easing.function;

import com.badlogic.gdx.math.MathUtils;
import com.gempukku.secsy.gaming.easing.EasingFunction;

public class MultiplyFractionEasingFunction implements EasingFunction {
    @Override
    public String getFunctionTrigger() {
        return "multFrac";
    }

    @Override
    public float evaluateFunction(String parameter, float input) {
        float multiplier = Float.parseFloat(parameter);
        float multiplied = multiplier * input;

        return multiplied - MathUtils.floor(multiplied);
    }
}
