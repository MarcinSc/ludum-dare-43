package com.gempukku.ld43.level;

import com.gempukku.ld43.model.CompletionComponent;
import com.gempukku.ld43.model.PlayerComponent;
import com.gempukku.ld43.render.RenderText;
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
            entity.send(new RenderText("And at the end of the day he will come home\nand tell me how tired HE is...", 5000));
        }
    }
}
