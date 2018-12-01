package com.gempukku.ld43.damage;

import com.gempukku.ld43.model.PlayerComponent;
import com.gempukku.ld43.render.RenderText;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;

@RegisterSystem
public class DamageSystem {
    @ReceiveEvent
    public void sensorTrigger(SensorContactBegin sensorContactBegin, EntityRef sensorEntity, VulnerableComponent vulnerable) {
        if (sensorContactBegin.getSensorType().equals("vulnerableSensor")) {
            EntityRef sensorTrigger = sensorContactBegin.getSensorTrigger();
            if (sensorTrigger.hasComponent(CausesVulnerabilityComponent.class)) {
                sensorEntity.send(new EntityDamaged(sensorTrigger));
            }
        }
    }

    @ReceiveEvent
    public void playerDamaged(EntityDamaged entityDamaged, EntityRef entity, PlayerComponent player) {
        entity.send(new RenderText("You've been home all day, and you did not " +
                "even clean the house?", 3000));
    }
}
