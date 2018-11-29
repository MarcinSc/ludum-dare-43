package com.gempukku.secsy.gaming.component.converter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;

public class ColorConverter implements ComponentFieldTypeConverter<Color, String> {
    @Override
    public String convertFrom(Color value) {
        return MathUtils.round(value.r * 255) + ","
                + MathUtils.round(value.g * 255) + ","
                + MathUtils.round(value.b * 255) + ","
                + MathUtils.round(value.a * 255);
    }

    @Override
    public Color convertTo(String value) {
        String[] split = value.split(",");
        int r = Integer.parseInt(split[0]);
        int g = Integer.parseInt(split[1]);
        int b = Integer.parseInt(split[2]);
        int a = (split.length == 4) ? Integer.parseInt(split[3]) : 255;
        return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    @Override
    public Color convertTo(String value, Class<?> containedClass) {
        return convertTo(value);
    }

    @Override
    public Color getDefaultValue() {
        return new Color();
    }
}
