package com.gempukku.ld43.model;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.entity.Component;

public interface DestroyOnCollisionComponent extends Component {
    String getParticleEffect();

    Color getParticleColor();
}
