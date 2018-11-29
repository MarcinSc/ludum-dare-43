package com.gempukku.secsy.gaming.rendering.splash;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;
import org.json.simple.JSONObject;

public class SplashDefinitionConverter implements ComponentFieldTypeConverter<SplashDefinition, JSONObject> {
    @Override
    public JSONObject convertFrom(SplashDefinition value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SplashDefinition convertTo(JSONObject value) {
        return new SplashDefinition(
                (String) value.get("textureName"),
                ((Number) value.get("duration")).longValue(),
                getColor((String) value.get("backgroundColor")));
    }

    private Color getColor(String backgroundColor) {
        String[] split = backgroundColor.split(",");
        int r = Integer.parseInt(split[0]);
        int g = Integer.parseInt(split[1]);
        int b = Integer.parseInt(split[2]);
        int a = (split.length == 4) ? Integer.parseInt(split[3]) : 255;
        return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    @Override
    public SplashDefinition convertTo(JSONObject value, Class<?> containedClass) {
        return convertTo(value);
    }

    @Override
    public SplashDefinition getDefaultValue() {
        throw new UnsupportedOperationException();
    }
}

