package com.gempukku.secsy.gaming.input2d;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.DefaultValue;

public interface ControlledByInputComponent extends Component {
    float getJumpImpulse();

    float getJumpSpeed();

    float getMoveSpeed();

    @DefaultValue("1")
    int getJumpMaxCount();

    void setJumpMaxCount(int jumpMaxCount);

    int getJumpCount();
    void setJumpCount(int jumpCount);
}
