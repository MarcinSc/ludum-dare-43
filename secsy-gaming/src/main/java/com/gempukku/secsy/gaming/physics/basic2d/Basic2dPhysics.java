package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.EntityRef;
import com.google.common.base.Predicate;

public interface Basic2dPhysics {
    Iterable<EntityRef> getContactsForSensor(EntityRef sensorEntity, String type, Predicate<EntityRef> sensorTriggerPredicate);
}
