package com.gempukku.secsy.gaming.easing;

public interface EasingResolver {
    float resolveValue(String recipe, float value);

    float resolveValue(EasedValue easedValue, float value);
}
