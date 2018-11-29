package com.gempukku.secsy.gaming.component;

import com.gempukku.secsy.entity.Component;

public interface Bounds2DComponent extends Component {
    float getLeft();

    void setLeft(float left);

    float getRight();

    void setRight(float right);

    float getUp();

    void setUp(float up);

    float getDown();

    void setDown(float down);
}
