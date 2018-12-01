package com.gempukku.ld43.level;

import com.gempukku.ld43.model.CompletionComponent;
import com.gempukku.ld43.model.PlayerComponent;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;

@RegisterSystem
public class LevelSystem {
    @ReceiveEvent
    public void levelComplete(SensorContactBegin sensorContactBegin, EntityRef entity, PlayerComponent player) {
        if (sensorContactBegin.getSensorType().equals("completionSensor")
                && sensorContactBegin.getSensorTrigger().hasComponent(CompletionComponent.class)) {
            System.out.println("Level completed");
        }
    }
}
