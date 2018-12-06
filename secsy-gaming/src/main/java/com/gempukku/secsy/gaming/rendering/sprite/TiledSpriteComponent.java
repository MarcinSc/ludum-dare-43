package com.gempukku.secsy.gaming.rendering.sprite;

import com.gempukku.secsy.entity.component.DefaultValue;
import com.gempukku.secsy.gaming.component.Bounds2DComponent;

public interface TiledSpriteComponent extends Bounds2DComponent {
    String getFileName();

    void setFileName(String fileName);

    float getPriority();

    void setPriority(float priority);

    @DefaultValue("1")
    float getTileXCount();

    void setTileXCount(float tileXCount);

    @DefaultValue("1")
    float getTileYCount();

    void setTileYCount(float tileYCount);
}
