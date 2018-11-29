package com.gempukku.secsy.gaming.camera2d.component;

import com.gempukku.secsy.entity.Component;

public interface ScreenShakeCameraComponent extends Component {
    long getShakeStartTime();

    void setShakeStartTime(long shakeStartTime);

    long getShakeEndTime();

    void setShakeEndTime(long shakeEndTime);

    // This should be somewhere around 0.01 to look good
    float getShakeSpeed();

    void setShakeSpeed(float shakeSpeed);

    // How much the shake amplitude is (in screen size %)
    float getShakeSize();

    void setShakeSize(float shakeSize);

    // If overwritten, it's advisable to use 0-1-0 as the last function in the recipe,
    // that way - the shake will die down, rather than stop abruptly.
    // Default is "pow5,0-1-0"
    String getShakeEasingRecipe();

    void setShakeEasingRecipe(String shakeEasingRecipe);
}
