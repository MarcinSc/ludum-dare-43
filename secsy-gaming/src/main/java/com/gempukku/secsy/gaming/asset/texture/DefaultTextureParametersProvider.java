package com.gempukku.secsy.gaming.asset.texture;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class DefaultTextureParametersProvider implements TextureParametersProvider {
    @Override
    public int getPageWidth(String textureAtlasId) {
        return 512;
    }

    @Override
    public int getPageHeight(String textureAtlasId) {
        return 512;
    }

    @Override
    public int getPadding(String textureAtlasId) {
        return 2;
    }

    @Override
    public Pixmap.Format getFormat(String textureAtlasId) {
        return Pixmap.Format.RGBA8888;
    }

    @Override
    public Texture.TextureFilter getMinTextureFilter(String textureAtlasId) {
        return Texture.TextureFilter.Nearest;
    }

    @Override
    public Texture.TextureFilter getMagTextureFilter(String textureAtlasId) {
        return Texture.TextureFilter.Nearest;
    }

    @Override
    public boolean getUseMipMaps(String textureAtlasId) {
        return false;
    }

    @Override
    public boolean getDuplicateBorder(String textureAtlasId) {
        return true;
    }
}
