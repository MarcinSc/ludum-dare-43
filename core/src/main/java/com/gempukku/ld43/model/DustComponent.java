package com.gempukku.ld43.model;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.gaming.component.Bounds2DComponent;

public interface DustComponent extends Bounds2DComponent {
    Color getColor();

    String getShape();
}
