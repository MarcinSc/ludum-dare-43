package com.gempukku.ld43.provider;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.time.TimeEntityProvider;

@RegisterSystem(shared = TimeEntityProvider.class)
public class TimeEntityProviderImpl extends AbstractLifeCycleSystem implements TimeEntityProvider {
    @Inject
    private EntityManager entityManager;
    private EntityRef timeEntity;

    @Override
    public void initialize() {
        timeEntity = entityManager.createEntityFromPrefab("timeEntity");
    }

    @Override
    public EntityRef getTimeEntity() {
        return timeEntity;
    }
}
