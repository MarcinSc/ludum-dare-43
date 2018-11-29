package com.gempukku.secsy.gaming.time;

import com.gempukku.secsy.entity.event.Event;

public class TimeUpdated extends Event {
    private long time;

    public TimeUpdated(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
