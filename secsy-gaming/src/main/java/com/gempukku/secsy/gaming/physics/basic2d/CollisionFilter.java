package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.EntityRef;

public interface CollisionFilter {
    boolean canCollideWith(EntityRef collidingBody, EntityRef obstacle);

    boolean canSensorContact(String sensorType, EntityRef sensorEntity, EntityRef sensorTriggerEntity);
}
