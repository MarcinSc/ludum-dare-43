package com.gempukku.secsy.gaming.input2d;

import com.gempukku.secsy.entity.event.Event;

public class EntityJumped extends Event {
    private int jumpCount;

    public EntityJumped(int jumpCount) {
        this.jumpCount = jumpCount;
    }

    public int getJumpCount() {
        return jumpCount;
    }
}
