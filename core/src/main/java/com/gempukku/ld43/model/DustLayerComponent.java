package com.gempukku.ld43.model;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.gaming.component.Bounds2DComponent;

public interface DustLayerComponent extends Bounds2DComponent {
    float[] getLayerDepth();

    void setLayerDepth(float[] layerDepth);

    float getDustGrowthPerSecond();

    String getSpawnedEntity();

    Color getDustColor();
}
