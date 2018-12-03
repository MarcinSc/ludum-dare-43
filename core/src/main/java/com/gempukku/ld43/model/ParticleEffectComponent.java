package com.gempukku.ld43.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.gempukku.secsy.entity.Component;

public interface ParticleEffectComponent extends Component {
    String getPath();

    void setPath(String path);

    ParticleEffect getParticleEffect();

    void setParticleEffect(ParticleEffect particleEffect);

    boolean isDestroyOnEffectEnd();

    void setDestroyOnEffectEnd(boolean destroyOnEffectEnd);

    Color getColor();

    void setColor(Color color);
}
