package com.gempukku.secsy.gaming.time;

import com.gempukku.secsy.entity.Component;

public interface TimeComponent extends Component {
    long getTime();

    void setTime(long time);
}
