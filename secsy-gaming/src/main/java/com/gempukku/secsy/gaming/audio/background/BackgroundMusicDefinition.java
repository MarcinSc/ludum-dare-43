package com.gempukku.secsy.gaming.audio.background;

public class BackgroundMusicDefinition {
    private String path;
    private boolean looping;
    private long duration;

    public BackgroundMusicDefinition(String path, boolean looping, long duration) {
        this.path = path;
        this.looping = looping;
        this.duration = duration;
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
}
