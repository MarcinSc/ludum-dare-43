package com.gempukku.secsy.gaming.component;

import com.gempukku.secsy.entity.Component;

public interface GroundedComponent extends Component {
    boolean isGrounded();

    void setGrounded(boolean grounded);
}
