package com.gempukku.secsy.gaming.time;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem(
        profiles = "time", shared = {TimeManager.class, InternalTimeManager.class})
public class DefaultTimeManager implements TimeManager, InternalTimeManager {
    @Inject
    private TimeEntityProvider timeEntityProvider;

    private long timeSinceLastUpdate = 0;

    @Override
    public void updateTime(long timeDiff) {
        EntityRef timeEntity = timeEntityProvider.getTimeEntity();
        TimeComponent time = getTimeComponent(timeEntity);

        TimeUpdated timeUpdated = new TimeUpdated(timeDiff);
        timeEntity.send(timeUpdated);
        timeDiff = timeUpdated.getTime();

        long lastTime = time.getTime();
        timeSinceLastUpdate = timeDiff;
        time.setTime(lastTime + timeDiff);
        timeEntity.saveChanges();
    }

    @ReceiveEvent(priorityName = "gaming.time.pause")
    public void processPause(TimeUpdated timeUpdated, EntityRef timeEntity, PausedComponent paused) {
        timeUpdated.setTime(0);
    }

    private TimeComponent getTimeComponent(EntityRef timeEntity) {
        TimeComponent time = timeEntity.getComponent(TimeComponent.class);

        if (time == null) {
            time = timeEntity.createComponent(TimeComponent.class);
            time.setTime(0);
            timeEntity.saveChanges();
            time = timeEntity.getComponent(TimeComponent.class);
        }
        return time;
    }

    @Override
    public long getTime() {
        EntityRef timeEntity = timeEntityProvider.getTimeEntity();
        TimeComponent time = getTimeComponent(timeEntity);
        return time.getTime();
    }

    @Override
    public long getTimeSinceLastUpdate() {
        return timeSinceLastUpdate;
    }
}
