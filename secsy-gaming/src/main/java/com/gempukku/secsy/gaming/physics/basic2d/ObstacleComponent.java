package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.component.DefaultValue;
import com.gempukku.secsy.gaming.component.Bounds2DComponent;

public interface ObstacleComponent extends Bounds2DComponent {
    @DefaultValue("true")
    boolean isAABB();

    ObstacleVertices getNonAABBVertices();
}
