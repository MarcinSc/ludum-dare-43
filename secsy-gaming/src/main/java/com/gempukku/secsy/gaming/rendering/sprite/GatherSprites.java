package com.gempukku.secsy.gaming.rendering.sprite;

import com.gempukku.secsy.entity.event.Event;

public class GatherSprites extends Event {
    private SpriteRenderer.SpriteSink spriteSink;

    public GatherSprites(SpriteRenderer.SpriteSink spriteSink) {
        this.spriteSink = spriteSink;
    }

    public SpriteRenderer.SpriteSink getSpriteSink() {
        return spriteSink;
    }
}
