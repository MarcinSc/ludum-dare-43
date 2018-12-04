package com.gempukku.secsy.gaming.particle2d;

import com.badlogic.gdx.graphics.Color;

public interface ParticleEngine {
    void addParticleEffect(String path, float x, float y, Color color, CompletionCallback callback);

    interface CompletionCallback {
        void effectEnded(String path);
    }
}
