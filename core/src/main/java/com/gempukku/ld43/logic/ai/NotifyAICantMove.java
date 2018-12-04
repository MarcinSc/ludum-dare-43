package com.gempukku.ld43.logic.ai;

import com.gempukku.ld43.model.PlatformComponent;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.ai.movement.AIApplyMovementIfPossibleComponent;
import com.gempukku.secsy.gaming.ai.movement.AICantMove;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactEnd;

@RegisterSystem
public class NotifyAICantMove {
    @ReceiveEvent
    public void cantMove(SensorContactEnd sensorContactEnd, EntityRef entity, AIApplyMovementIfPossibleComponent aiApplyMovement,
                         HorizontalOrientationComponent horizontalOrientation) {
        boolean right = horizontalOrientation.isFacingRight();
        if (right && sensorContactEnd.getSensorType().equals("rightGroundSensor")
                && sensorContactEnd.getSensorTrigger().hasComponent(PlatformComponent.class)) {
            entity.send(new AICantMove());
        } else if (!right && sensorContactEnd.getSensorType().equals("leftGroundSensor")
                && sensorContactEnd.getSensorTrigger().hasComponent(PlatformComponent.class)) {
            entity.send(new AICantMove());
        }
    }
}
