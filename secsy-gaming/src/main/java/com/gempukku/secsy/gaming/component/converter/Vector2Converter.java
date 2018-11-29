package com.gempukku.secsy.gaming.component.converter;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;

public class Vector2Converter implements ComponentFieldTypeConverter<Vector2, String> {
    @Override
    public String convertFrom(Vector2 value) {
        return value.x + "," + value.y;
    }

    @Override
    public Vector2 convertTo(String value) {
        String[] split = value.split(",");
        return new Vector2(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
    }

    @Override
    public Vector2 convertTo(String value, Class<?> containedClass) {
        return convertTo(value);
    }

    @Override
    public Vector2 getDefaultValue() {
        return new Vector2();
    }
}
