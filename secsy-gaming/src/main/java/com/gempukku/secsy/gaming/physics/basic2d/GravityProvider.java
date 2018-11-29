package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.EntityRef;

public interface GravityProvider {
    float getGravityForEntity(EntityRef entity);

    float getTerminalVelocityForEntity(EntityRef entity);
}
