package com.gempukku.secsy.gaming.rendering.postprocess.tint.texture;

import com.gempukku.secsy.entity.component.DefaultValue;
import com.gempukku.secsy.gaming.component.TimedEffectComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;

public interface TextureTintComponent extends TimedEffectComponent {
    String getTextureAtlasId();

    void setTextureAtlasId(String textureAtlasId);

    String getTextureName();

    void setTextureName(String textureName);

    @DefaultValue("0")
    EasedValue getAlpha();

    void setAlpha(EasedValue easedValue);
}
