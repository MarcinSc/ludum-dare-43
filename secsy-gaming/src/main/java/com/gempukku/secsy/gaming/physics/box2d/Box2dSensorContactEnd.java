package com.gempukku.secsy.gaming.physics.box2d;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.gempukku.secsy.entity.event.Event;

public class Box2dSensorContactEnd extends Event {
    private Fixture sensorFixture;
    private Fixture otherFixture;

    public Box2dSensorContactEnd(Fixture sensorFixture, Fixture otherFixture) {
        this.sensorFixture = sensorFixture;
        this.otherFixture = otherFixture;
    }

    public Fixture getSensorFixture() {
        return sensorFixture;
    }

    public Fixture getOtherFixture() {
        return otherFixture;
    }
}
