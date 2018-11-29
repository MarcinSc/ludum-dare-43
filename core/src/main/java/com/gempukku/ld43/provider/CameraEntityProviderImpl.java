package com.gempukku.ld43.provider;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.rendering.pipeline.CameraEntityProvider;

@RegisterSystem(shared = CameraEntityProvider.class)
public class CameraEntityProviderImpl extends AbstractLifeCycleSystem implements CameraEntityProvider {
    @Inject
    private EntityManager entityManager;

    private EntityRef cameraEntity;

    @Override
    public void initialize() {
        cameraEntity = entityManager.createEntityFromPrefab("cameraEntity");
    }

    @Override
    public EntityRef getCameraEntity() {
        return cameraEntity;
    }
}
