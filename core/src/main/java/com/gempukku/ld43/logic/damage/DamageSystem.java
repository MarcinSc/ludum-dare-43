package com.gempukku.ld43.logic.damage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.gempukku.ld43.model.DustBunnyComponent;
import com.gempukku.ld43.model.PlayerComponent;
import com.gempukku.ld43.render.RenderText;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.audio.AudioManager;
import com.gempukku.secsy.gaming.physics.basic2d.MovingComponent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;

import java.util.Random;

@RegisterSystem
public class DamageSystem extends AbstractLifeCycleSystem {
    @Inject
    private AudioManager audioManager;

    private Sound stomp;
    private String[] stompTexts = new String[]{
            "I did not go to college for that...",
            "Another one bites the dust.",
            "Oh! So fluffy!"
    };

    @Override
    public void initialize() {
        stomp = Gdx.audio.newSound(Gdx.files.internal("sounds/stomp.wav"));
    }

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
    public void stompTrigger(SensorContactBegin sensorContactBegin, EntityRef sensorEntity, PlayerComponent player) {
        if (sensorContactBegin.getSensorType().equals("stompSensor")) {
            EntityRef sensorTrigger = sensorContactBegin.getSensorTrigger();
            if (sensorTrigger.hasComponent(DustBunnyComponent.class)) {
                sensorTrigger.send(new EntityDamaged(sensorEntity));
                MovingComponent moving = sensorEntity.getComponent(MovingComponent.class);
                moving.setSpeedY(3);
                sensorEntity.saveChanges();

                audioManager.playSound(stomp);

                sensorEntity.send(new RenderText(stompTexts[new Random().nextInt(stompTexts.length)], 3000));
            }
        }
    }

    @Override
    public void destroy() {
        stomp.dispose();
    }
}
