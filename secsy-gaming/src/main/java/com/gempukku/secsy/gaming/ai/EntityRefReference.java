package com.gempukku.secsy.gaming.ai;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.ai.map.MapAIReference;

public class EntityRefReference extends MapAIReference {
    private EntityRef entityRef;

    public EntityRefReference(EntityRef entityRef) {
        super(entityRef.getComponent(AIComponent.class).getValues());
        this.entityRef = entityRef;
    }

    public EntityRef getEntityRef() {
        return entityRef;
    }

    @Override
    public void storeValues() {
        entityRef.getComponent(AIComponent.class).setValues(getValues());
        entityRef.saveChanges();
    }
}
