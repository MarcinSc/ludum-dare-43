package com.gempukku.ld43.render;

import com.gempukku.secsy.entity.event.Event;

public class RenderText extends Event {
    private String text;
    private long duration;

    public RenderText(String text, long duration) {
        this.text = text;
        this.duration = duration;
    }

    public String getText() {
        return text;
    }

    public long getDuration() {
        return duration;
    }
}
