package com.gempukku.secsy.gaming.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;

@RegisterSystem(profiles = {"audioManager"}, shared = AudioManager.class)
public class AudioSystem extends AbstractLifeCycleSystem implements AudioManager {
    private float masterVolume = 0.05f;
    private float musicVolume = 1f;
    private float fxVolume = 1f;

    private long musicFadeOutTime = 1000;

    private long musicSwitchTime;
    private Music oldBackgroundMusic;
    private Music backgroundMusic;

    @ReceiveEvent
    public void fadeMusic(GameLoopUpdate gameLoopUpdate) {
        long currentTime = System.currentTimeMillis();
        if (currentTime < musicSwitchTime + musicFadeOutTime) {
            float progress = 1f * (currentTime - musicSwitchTime) / musicFadeOutTime;
            if (oldBackgroundMusic != null)
                oldBackgroundMusic.setVolume(musicVolume * masterVolume * (1 - progress));
            if (backgroundMusic != null)
                backgroundMusic.setVolume(musicVolume * masterVolume * progress);
        } else {
            if (oldBackgroundMusic != null)
                oldBackgroundMusic.stop();
            oldBackgroundMusic = null;
            if (backgroundMusic != null)
                backgroundMusic.setVolume(musicVolume * masterVolume);
        }
    }

    @Override
    public void setMasterVolume(float volume) {
        masterVolume = volume;
    }

    @Override
    public void setMusicVolume(float volume) {
        musicVolume = volume;
    }

    @Override
    public void setFXVolume(float volume) {
        fxVolume = volume;
    }

    @Override
    public void playSound(Sound sound) {
        sound.play(fxVolume * masterVolume);
    }

    @Override
    public void setBackgroundMusic(Music music, long fadeTime) {
        if (backgroundMusic != music) {
            musicFadeOutTime = fadeTime;
            if (oldBackgroundMusic != null)
                oldBackgroundMusic.stop();
            oldBackgroundMusic = backgroundMusic;
            backgroundMusic = music;
            if (backgroundMusic != null) {
                backgroundMusic.setVolume(0);
                backgroundMusic.play();
            }
            musicSwitchTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean isBackgroundMusic(Music music) {
        return backgroundMusic == music;
    }
}
