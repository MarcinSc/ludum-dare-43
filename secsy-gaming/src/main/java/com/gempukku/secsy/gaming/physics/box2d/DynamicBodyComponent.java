package com.gempukku.secsy.gaming.physics.box2d;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.Container;

import java.util.List;

public interface DynamicBodyComponent extends Component {
    boolean isFixedRotation();

    float getLinearDumping();

    @Container(PhysicsFixture.class)
    List<PhysicsFixture> getFixtures();

    @Container(PhysicsFixture.class)
    void setFixtures(List<PhysicsFixture> fixtures);
}
