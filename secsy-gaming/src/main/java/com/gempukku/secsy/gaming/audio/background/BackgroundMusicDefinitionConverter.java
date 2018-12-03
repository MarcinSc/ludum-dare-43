package com.gempukku.secsy.gaming.audio.background;

import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;
import org.json.simple.JSONObject;

public class BackgroundMusicDefinitionConverter implements ComponentFieldTypeConverter<BackgroundMusicDefinition, JSONObject> {
    @Override
    public BackgroundMusicDefinition convertTo(JSONObject value) {
        return new BackgroundMusicDefinition(
                (String) value.get("path"), (Boolean) value.get("looping"), ((Number) value.get("duration")).longValue(),
                ((Number) value.get("fadeInDuration")).longValue());
    }

    @Override
    public BackgroundMusicDefinition convertTo(JSONObject value, Class<?> containedClass) {
        return convertTo(value);
    }

    @Override
    public JSONObject convertFrom(BackgroundMusicDefinition value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BackgroundMusicDefinition getDefaultValue() {
        return null;
    }
}
