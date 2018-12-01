package com.gempukku.ld43.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.gempukku.ld43.model.PlayerComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.audio.AudioManager;
import com.gempukku.secsy.gaming.input2d.EntityJumped;

@RegisterSystem
public class PlayerSounds extends AbstractLifeCycleSystem {
    @Inject
    private AudioManager audioManager;
    private Sound jump;

    @Override
    public void initialize() {
        jump = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.wav"));
    }

    @ReceiveEvent
    public void playerJumped(EntityJumped entityJumped, EntityRef playerEntity, PlayerComponent player) {
        audioManager.playSound(jump);
    }

    @Override
    public void destroy() {
        jump.dispose();
    }
}
