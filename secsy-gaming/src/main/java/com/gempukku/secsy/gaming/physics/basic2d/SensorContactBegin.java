package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.event.Event;

public class SensorContactBegin extends Event {
    private String sensorType;
    private EntityRef sensorTrigger;

    public SensorContactBegin(String sensorType, EntityRef sensorTrigger) {
        this.sensorType = sensorType;
        this.sensorTrigger = sensorTrigger;
    }

    public String getSensorType() {
        return sensorType;
    }

    public EntityRef getSensorTrigger() {
        return sensorTrigger;
    }
}
