package com.gempukku.ld43.provider;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.physics.basic2d.GravityProvider;

@RegisterSystem(shared = GravityProvider.class)
public class GravityProviderImpl implements GravityProvider {
    @Override
    public float getGravityForEntity(EntityRef entity) {
        return -10;
    }

    @Override
    public float getTerminalVelocityForEntity(EntityRef entity) {
        return 5;
    }
}
