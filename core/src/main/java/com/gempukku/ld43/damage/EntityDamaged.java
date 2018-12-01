package com.gempukku.ld43.damage;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.event.Event;

public class EntityDamaged extends Event {
    private EntityRef damagedBy;

    public EntityDamaged(EntityRef damagedBy) {
        this.damagedBy = damagedBy;
    }

    public EntityRef getDamagedBy() {
        return damagedBy;
    }
}
