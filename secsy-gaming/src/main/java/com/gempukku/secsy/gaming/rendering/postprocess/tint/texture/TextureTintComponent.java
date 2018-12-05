package com.gempukku.secsy.gaming.rendering.postprocess.tint.texture;

import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.rendering.postprocess.PostProcessEffectComponent;

public interface TextureTintComponent extends PostProcessEffectComponent {
    String getTextureAtlasId();

    void setTextureAtlasId(String textureAtlasId);

    String getTextureName();

    void setTextureName(String textureName);

    EasedValue getAlpha();

    void setAlpha(EasedValue easedValue);
}
