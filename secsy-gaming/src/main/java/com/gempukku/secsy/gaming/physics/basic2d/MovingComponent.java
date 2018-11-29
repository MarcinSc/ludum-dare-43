package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.Component;

public interface MovingComponent extends Component {
    float getSpeedX();

    void setSpeedX(float speedX);

    float getSpeedY();

    void setSpeedY(float speedY);
}
