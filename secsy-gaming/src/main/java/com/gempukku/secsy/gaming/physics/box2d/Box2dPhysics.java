package com.gempukku.secsy.gaming.physics.box2d;

import com.gempukku.secsy.entity.EntityRef;

public interface Box2dPhysics {
    void addBody(EntityRef entity);

    void applyPulse(EntityRef entity, float x, float y);

    void setSpeedX(EntityRef entity, float speed);

    void setSpeedY(EntityRef entity, float speed);

    void removeBody(EntityRef entity);
}
