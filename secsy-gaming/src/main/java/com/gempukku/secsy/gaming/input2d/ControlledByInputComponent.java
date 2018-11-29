package com.gempukku.secsy.gaming.input2d;

import com.gempukku.secsy.entity.Component;

public interface ControlledByInputComponent extends Component {
    float getJumpImpulse();

    float getJumpSpeed();

    float getMoveSpeed();

    int getJumpMaxCount();

    int getJumpCount();

    void setJumpCount(int jumpCount);
}
