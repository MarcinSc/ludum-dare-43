package com.gempukku.secsy.gaming.easing;

public class EasedValue {
    private final float multiplier;
    private final String recipe;

    public EasedValue(float multiplier) {
        this(multiplier, null);
    }

    public EasedValue(float multiplier, String recipe) {
        this.multiplier = multiplier;
        this.recipe = recipe;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public String getRecipe() {
        return recipe;
    }
}
