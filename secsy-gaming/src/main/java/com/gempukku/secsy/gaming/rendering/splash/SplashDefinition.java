package com.gempukku.secsy.gaming.rendering.splash;

import com.badlogic.gdx.graphics.Color;

public class SplashDefinition {
    private String textureName;
    private long duration;
    private Color backgroundColor;

    public SplashDefinition(String textureName, long duration, Color backgroundColor) {
        this.textureName = textureName;
        this.duration = duration;
        this.backgroundColor = backgroundColor;
    }

    public String getTextureName() {
        return textureName;
    }

    public long getDuration() {
        return duration;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }
}
