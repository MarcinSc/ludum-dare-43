package com.gempukku.secsy.gaming.audio.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.gaming.audio.AudioManager;
import com.gempukku.secsy.gaming.time.TimeManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RegisterSystem(profiles = "backgroundMusic")
public class BackgroundMusicSystem extends AbstractLifeCycleSystem {
    @Inject
    private AudioManager audioManager;
    @Inject
    private TimeManager timeManager;

    private Map<String, Music> musicMap;

    @Override
    public void initialize() {
        musicMap = new HashMap<String, Music>();
    }

    @ReceiveEvent
    public void updateBackgroundMusic(GameLoopUpdate gameLoopUpdate, EntityRef gameEntity, BackgroundMusicComponent backgroundMusic) {
        long time = timeManager.getTime();
        long startTime = backgroundMusic.getStartTime();
        if (time >= startTime) {
            List<BackgroundMusicDefinition> backgroundMusicDefinitions = backgroundMusic.getBackgroundMusicDefinitions();
            BackgroundMusicDefinition backgroundMusicDefinition = getBackgroundMusicDefinition(backgroundMusicDefinitions, time - startTime);
            Music music = null;
            long fadein = 1000;
            if (backgroundMusicDefinition != null) {
                music = getMusic(backgroundMusicDefinition.getPath());
                music.setLooping(backgroundMusicDefinition.isLooping());
                fadein = backgroundMusicDefinition.getFadeInDuration();
            }
            if (!audioManager.isBackgroundMusic(music))
                audioManager.setBackgroundMusic(music, fadein);
        }
    }

    private Music getMusic(String path) {
        Music music = musicMap.get(path);
        if (music == null) {
            music = Gdx.audio.newMusic(Gdx.files.internal(path));
            musicMap.put(path, music);
        }
        return music;
    }

    private BackgroundMusicDefinition getBackgroundMusicDefinition(List<BackgroundMusicDefinition> backgroundMusicDefinitions, long timeSinceStart) {
        for (BackgroundMusicDefinition backgroundMusicDefinition : backgroundMusicDefinitions) {
            long musicDuration = backgroundMusicDefinition.getDuration();
            if (musicDuration == -1 || musicDuration > timeSinceStart)
                return backgroundMusicDefinition;
            timeSinceStart -= musicDuration;
        }
        return null;
    }

    @Override
    public void destroy() {
        for (Music music : musicMap.values()) {
            music.dispose();
        }
    }
}
