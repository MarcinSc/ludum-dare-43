package com.gempukku.ld43.level;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.basic2d.EntityMoved;

@RegisterSystem
public class BoundsCheck {
    @ReceiveEvent
    public void checkOutOfBounds(EntityMoved entityMoved, EntityRef entity) {
        Position2DComponent position = entity.getComponent(Position2DComponent.class);
        if (position.getY() < -1)
            entity.send(new EntityOutOfBounds());
    }
}
