package com.gempukku.secsy.gaming.asset.texture;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public interface TextureParametersProvider {
    int getPageWidth(String textureAtlasId);

    int getPageHeight(String textureAtlasId);

    int getPadding(String textureAtlasId);

    Pixmap.Format getFormat(String textureAtlasId);

    Texture.TextureFilter getMinTextureFilter(String textureAtlasId);

    Texture.TextureFilter getMagTextureFilter(String textureAtlasId);

    boolean getUseMipMaps(String textureAtlasId);

    boolean getDuplicateBorder(String textureAtlasId);
}
