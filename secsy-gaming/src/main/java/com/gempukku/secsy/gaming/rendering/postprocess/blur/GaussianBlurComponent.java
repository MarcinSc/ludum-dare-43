package com.gempukku.secsy.gaming.rendering.postprocess.blur;

import com.gempukku.secsy.entity.Component;

public interface GaussianBlurComponent extends Component {
    int getBlurRadius();

    void setBlurRadius(int blurRadius);
}
