package com.gempukku.secsy.gaming.rendering.sprite;

import com.gempukku.secsy.gaming.component.Bounds2DComponent;

public interface SpriteComponent extends Bounds2DComponent {
    String getFileName();

    float getPriority();
}
