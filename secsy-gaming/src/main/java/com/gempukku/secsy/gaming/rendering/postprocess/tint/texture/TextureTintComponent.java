package com.gempukku.secsy.gaming.rendering.postprocess.tint.texture;

import com.gempukku.secsy.entity.Component;

public interface TextureTintComponent extends Component {
    String getTextureAtlasId();

    String getTextureName();

    float getFactor();

    void setFactor(float factor);
}
