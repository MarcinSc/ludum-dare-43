package com.gempukku.secsy.gaming.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public interface AudioManager {
    void setMasterVolume(float volume);

    void setMusicVolume(float volume);

    void setFXVolume(float volume);

    void playSound(Sound sound);

    void setBackgroundMusic(Music music, long fadeTime);

    boolean isBackgroundMusic(Music music);
}
