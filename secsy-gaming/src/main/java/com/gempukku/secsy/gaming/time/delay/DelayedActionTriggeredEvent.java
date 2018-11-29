package com.gempukku.secsy.gaming.time.delay;

import com.gempukku.secsy.entity.event.Event;

public class DelayedActionTriggeredEvent extends Event {
    private String actionId;

    public DelayedActionTriggeredEvent(String actionId) {
        this.actionId = actionId;
    }

    public String getActionId() {
        return actionId;
    }
}
