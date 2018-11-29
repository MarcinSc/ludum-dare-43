package com.gempukku.secsy.gaming.component;

import com.gempukku.secsy.entity.Component;

public interface HorizontalOrientationComponent extends Component {
    boolean isFacingRight();

    void setFacingRight(boolean facingRight);
}
