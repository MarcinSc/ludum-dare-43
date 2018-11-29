package com.gempukku.secsy.gaming.time.delay;

import com.gempukku.secsy.entity.Component;

import java.util.Map;

public interface DelayedActionComponent extends Component {
    Map<String, Long> getActionIdWakeUp();

    void setActionIdWakeUp(Map<String, Long> actionWakeUps);
}
