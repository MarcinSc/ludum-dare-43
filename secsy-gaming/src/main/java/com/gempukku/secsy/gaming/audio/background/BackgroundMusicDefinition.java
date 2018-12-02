package com.gempukku.secsy.gaming.audio.background;

public class BackgroundMusicDefinition {
    private String path;
    private boolean looping;
    private long duration;
    private long fadeInDuration;

    public BackgroundMusicDefinition(String path, boolean looping, long duration, long fadeInDuration) {
        this.path = path;
        this.looping = looping;
        this.duration = duration;
        this.fadeInDuration = fadeInDuration;
    }

    public String getPath() {
        return path;
    }

    public boolean isLooping() {
        return looping;
    }

    public long getDuration() {
        return duration;
    }

    public long getFadeInDuration() {
        return fadeInDuration;
    }
}
