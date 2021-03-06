package com.gempukku.ld43.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.gempukku.ld43.model.PlayerComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.audio.AudioManager;
import com.gempukku.secsy.gaming.camera2d.component.ScreenShakeCameraComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.input2d.EntityJumped;
import com.gempukku.secsy.gaming.input2d.EntityLanded;
import com.gempukku.secsy.gaming.rendering.pipeline.CameraEntityProvider;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem
public class PlayerInteractions extends AbstractLifeCycleSystem {
    @Inject
    private AudioManager audioManager;
    @Inject
    private CameraEntityProvider cameraEntityProvider;
    @Inject
    private TimeManager timeManager;

    private Sound jump;
    private Sound landed;


    @Override
    public void initialize() {
        jump = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.wav"));
        landed = Gdx.audio.newSound(Gdx.files.internal("sounds/land.wav"));
    }

    @ReceiveEvent
    public void playerJumped(EntityJumped entityJumped, EntityRef playerEntity, PlayerComponent player) {
        audioManager.playSound(jump);
    }

    @ReceiveEvent
    public void playerLanded(EntityLanded entityLanded, EntityRef playerEntity, PlayerComponent player) {
        audioManager.playSound(landed);

        EntityRef cameraEntity = cameraEntityProvider.getCameraEntity();
        ScreenShakeCameraComponent screenShake = cameraEntity.getComponent(ScreenShakeCameraComponent.class);
        long time = timeManager.getTime();
        screenShake.setEffectStart(time);
        screenShake.setEffectDuration(500);
        screenShake.setShakeSize(new EasedValue(0.05f, "pow5,0-1-0"));
        screenShake.setShakeSpeed(new EasedValue(0.01f));
        cameraEntity.saveChanges();
    }

    @Override
    public void destroy() {
        jump.dispose();
        landed.dispose();
    }
}
