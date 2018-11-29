package com.gempukku.secsy.gaming.time.delay;

import com.gempukku.secsy.entity.event.Event;

public class PeriodicActionTriggeredEvent extends Event {
    private String actionId;

    public PeriodicActionTriggeredEvent(String actionId) {
        this.actionId = actionId;
    }

    public String getActionId() {
        return actionId;
    }
}
